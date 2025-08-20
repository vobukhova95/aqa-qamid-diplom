package test.api;

import data.api.*;
import data.bd.SQLHelper;
import data.ui.DataHelper;
import io.qameta.allure.*;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

public class PaymentApiTests {

    private final String statusApproved = DataHelper.CommonValues.getStatusApproved();
    private final String statusDeclined = DataHelper.CommonValues.getStatusDeclined();
    private final int amountTravel = DataHelper.CommonValues.getAmountTravel();

  @BeforeEach
    void deleteData() {
        SQLHelper.cleanDatabase();
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Positive")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should approve transaction when using an approved card")
    void shouldApproveTransactionWithApprovedCard() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Positive")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should reject transaction when using a declined card")
    void shouldRejectTransactionWithDeclinedCard() {
        String cardNumber = DataHelper.CardNumber.declinedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.DECLINED);

        SQLHelper.checkPaymentStatus(statusDeclined);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should not allow transaction when card number contains less than 16 digits")
    void shouldNotAllowTransactionWhenCardNumberIsLessThan16Digits() {
        String invalidCardNumber = DataHelper.CommonValues.generateDigits(15);
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(invalidCardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should not allow transaction when card number contains more than 16 digits")
    void shouldNotAllowTransactionWhenCardNumberExceeds16Digits() {
        String invalidCardNumber = DataHelper.CommonValues.generateDigits(17);
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(invalidCardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should reject transaction when card number contains is '0000 0000 0000 0000'")
    void shouldRejectTransactionWhenCardNumberContainsAllZeros() {
        String invalidCardNumber = DataHelper.CardNumber.invalidCardNumberAllZeros();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(invalidCardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should reject transaction when card number contains Latin letters")
    void shouldRejectTransactionWhenCardNumberContainsLatinLetters() {
        String invalidCardNumber = DataHelper.CommonValues.generateLetters(16);
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(invalidCardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should reject transaction when card number contains Cyrillic letters")
    void shouldRejectTransactionWhenCardNumberContainsCyrillicLetters() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueCyrillic(16);
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(invalidCardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should reject transaction when card number contains special symbols")
    void shouldRejectTransactionWhenCardNumberContainsSymbols() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueSymbols(16);
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(invalidCardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should reject transaction when card number contains spaces in invalid positions")
    void shouldRejectTransactionWhenCardNumberContainsSpacesInside() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueSpace();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(invalidCardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("API")
    @DisplayName("Should reject transaction when card number field is empty")
    void shouldRejectTransactionWhenCardNumberIsEmpty() {
        String invalidCardNumber = DataHelper.CommonValues.getValueEmpty();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(invalidCardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Positive")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should approve transaction when month is current month of current year")
    void shouldApproveTransactionWhenMonthIsCurrentMonthOfCurrentYear() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String validMonth = DataHelper.Month.validCurrentMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, validMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Positive")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should approve transaction when month is 01 (January)")
    void shouldApproveTransactionWhenMonthIsJanuary() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String validMonth = DataHelper.Month.validMonth01();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, validMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Positive")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should approve transaction when month is 12 (December)")
    void shouldApproveTransactionWhenMonthIsDecember() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String validMonth = DataHelper.Month.validMonth12();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, validMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month is previous month of current year")
    void shouldRejectTransactionWhenMonthIsPreviousMonthOfCurrentYear() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.Month.invalidMonthCurrentMonthMinus1();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month contains three digits")
    void shouldRejectTransactionWhenMonthHasThreeDigits() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.generateDigits(3);
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month contains only one digit")
    void shouldRejectTransactionWhenMonthHasOneDigit() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.generateDigits(1);
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month is 00")
    void shouldRejectTransactionWhenMonthIsZero() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.Month.invalidMonth00();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month is greater than 12")
    void shouldRejectTransactionWhenMonthExceeds12() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.Month.invalidMonth13();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month contains Latin letters")
    void shouldRejectTransactionWhenMonthContainsLatinLetters() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.generateLetters(2);
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month contains Cyrillic letters")
    void shouldRejectTransactionWhenMonthContainsCyrillicLetters() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.invalidValueCyrillic(2);
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month contains special symbols")
    void shouldRejectTransactionWhenMonthContainsSymbols() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.invalidValueSymbols(2);
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month contains only spaces")
    void shouldRejectTransactionWhenMonthContainsSpaces() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.invalidValueSpace();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("API")
    @DisplayName("Should reject transaction when month field is empty")
    void shouldRejectTransactionWhenMonthIsEmpty() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.getValueEmpty();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, invalidMonth, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Positive")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should approve transaction when year is the current year")
    void shouldApproveTransactionWhenYearIsCurrentYear() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth12();
        String validYear = DataHelper.CardYear.generateYearOffset(0);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, validYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Positive")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should approve transaction when year is current year + 4")
    void shouldApproveTransactionWhenYearIsCurrentYearPlus4() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String validYear = DataHelper.CardYear.generateYearOffset(4);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, validYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Positive")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should approve transaction when year is current year + 5")
    void shouldApproveTransactionWhenYearIsCurrentYearPlus5() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String validYear = DataHelper.CardYear.generateYearOffset(5);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, validYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should reject transaction when year is in the past")
    void shouldRejectTransactionWhenYearIsPrevious() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CardYear.generateYearOffset(-1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, invalidYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should reject transaction when year contains three digits")
    void shouldRejectTransactionWhenYearHasThreeDigits() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.generateDigits(3);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, invalidYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should reject transaction when year contains one digits")
    void shouldRejectTransactionWhenYearHasOneDigit() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.generateDigits(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, invalidYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should reject transaction when year is current year + 6")
    void shouldRejectTransactionWhenYearIsCurrentYearPlus6() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CardYear.generateYearOffset(6);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, invalidYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should reject transaction when year contains Latin letters")
    void shouldRejectTransactionWhenYearContainsLatinLetters() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.generateLetters(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, invalidYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should reject transaction when year contains Cyrillic letters")
    void shouldRejectTransactionWhenYearContainsCyrillicLetters() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.invalidValueCyrillic(2);
        String expectedYear = DataHelper.CommonValues.getValueEmpty();
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, invalidYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should reject transaction when year contains special symbols")
    void shouldRejectTransactionWhenYearContainsSymbols() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.invalidValueSymbols(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, invalidYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should reject transaction when year contains only spaces")
    void shouldRejectTransactionWhenYearContainsSpaces() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.invalidValueSpace();
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, invalidYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("API")
    @DisplayName("Should reject transaction when year field is empty")
    void shouldRejectTransactionWhenYearIsEmpty() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.getValueEmpty();
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, invalidYear, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Positive")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should approve transaction when holder contains hyphen")
    void shouldApproveTransactionWhenHolderContainsHyphen() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String validHolder = DataHelper.Holder.validHolderHyphenate();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, validHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Positive")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should approve transaction when holder contains apostrophe")
    void shouldApproveTransactionWhenHolderContainsApostrophe() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String validHolder = DataHelper.Holder.validHolderApostrophe();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, validHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Positive")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should approve transaction when holder contains double surname")
    void shouldApproveTransactionWhenHolderContainsDoubleSurname() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String validHolder = DataHelper.Holder.validHolderMultipleNames();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, validHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Positive")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should approve transaction when holder contains minimal valid value (two letters separated by space)")
    void shouldApproveTransactionWhenHolderContainsTwoLettersAndSpaceAsMinimum() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String validHolder = DataHelper.Holder.validHolderTwoLettersAndSpace();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, validHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Positive")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should approve transaction when holder contains boundary minimal valid value (three letters + space)")
    void shouldApproveTransactionWhenHolderContainsThreeLettersAndSpaceAsBoundary() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String validHolder = DataHelper.Holder.validHolderThreeLettersAndSpace();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, validHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Positive")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should approve transaction when holder contains value of length 50 (max allowed)")
    void shouldApproveTransactionWhenHolderContainsMax50Chars() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String validHolder = DataHelper.CommonValues.generateLetters(50);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, validHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should reject transaction when holder contains a single word (no space)")
    void shouldRejectTransactionWhenHolderContainsSingleWord() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.Holder.invalidHolderOneWord();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, invalidHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should reject transaction when holder length is less than minimal allowed")
    void shouldRejectTransactionWhenHolderContainsTooShort() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.CommonValues.generateLetters(2);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, invalidHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should reject transaction when holder contains more than 50 chars")
    void shouldRejectTransactionWhenHolderContainsMoreThan50Chars() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.CommonValues.generateLetters(51);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, invalidHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should reject transaction when holder contains Cyrillic characters")
    void shouldRejectTransactionWhenHolderContainsCyrillicLetters() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.CommonValues.invalidValueCyrillic(7);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, invalidHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should reject transaction when holder contains digits")
    void shouldRejectTransactionWhenHolderContainsDigits() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.CommonValues.generateDigits(5);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, invalidHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should reject transaction when holder contains special symbols")
    void shouldRejectTransactionWhenHolderContainsSymbols() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.CommonValues.invalidValueSymbols(8);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, invalidHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should reject transaction when holder contains only spaces")
    void shouldRejectTransactionWhenHolderContainsSpaces() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.CommonValues.invalidValueSpace();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, invalidHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("API")
    @DisplayName("Should reject transaction when holder field is empty")
    void shouldRejectTransactionWhenHolderIsEmpty() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.CommonValues.getValueEmpty();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, invalidHolder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Positive")
    @Tag("CVC")
    @Tag("API")
    @DisplayName("Should approve transaction when CVC contains 000")
    void shouldApproveTransactionWhenCVCContains000() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String holder = DataHelper.Holder.validHolder();
        String validCvc = DataHelper.CVC.validCVC000();

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, validCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Positive")
    @Tag("CVC")
    @Tag("API")
    @DisplayName("Should approve transaction when CVC contains 999")
    void shouldApproveTransactionWhenCVCContains999() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String holder = DataHelper.Holder.validHolder();
        String validCvc = DataHelper.CVC.validCVC999();

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, validCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.APPROVED);

        SQLHelper.checkPaymentStatus(statusApproved);
        SQLHelper.checkPaymentAmount(amountTravel);
        SQLHelper.checkOrderLinkedToPayment();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("API")
    @DisplayName("Should reject transaction when CVC contains two digit")
    void shouldRejectTransactionWhenCvcContainsTwoDigits() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String invalidCvc = DataHelper.CommonValues.generateDigits(2);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, invalidCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("API")
    @DisplayName("Should reject transaction when CVC contains four digit")
    void shouldRejectTransactionWhenCvcContainsFourDigits() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String invalidCvc = DataHelper.CommonValues.generateDigits(4);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, invalidCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("API")
    @DisplayName("Should reject transaction when CVC contains Latin letters")
    void shouldRejectTransactionWhenCvcContainsLatinLetters() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String invalidCvc = DataHelper.CommonValues.generateLetters(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, invalidCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("API")
    @DisplayName("Should reject transaction when CVC contains Cyrillic letters")
    void shouldRejectTransactionWhenCvcContainsCyrillicLetters() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String invalidCvc = DataHelper.CommonValues.invalidValueCyrillic(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, invalidCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("API")
    @DisplayName("Should reject transaction when CVC contains special symbols")
    void shouldRejectTransactionWhenCvcContainsSymbols() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String invalidCvc = DataHelper.CommonValues.invalidValueSymbols(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, invalidCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("API")
    @DisplayName("Should reject transaction when CVC contains only spaces")
    void shouldRejectTransactionWhenCvcContainsSpaces() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String invalidCvc = DataHelper.CommonValues.invalidValueSpace();

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, invalidCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("API")
    @DisplayName("Should reject transaction when CVC field is empty")
    void shouldRejectTransactionWhenCvcIsEmpty() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String holder = DataHelper.Holder.validHolder();
        String invalidCvc = DataHelper.CommonValues.getValueEmpty();

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, invalidCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Owner("Veronika Obukhova")
    @Story("Required field validation")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Negative")
    @Tag("API")
    @DisplayName("Should display required-field errors for all fields when the form is submitted empty")
    void shouldShowAllRequiredErrorsWhenSubmittingEmptyForm() {
        String invalidCardNumber = DataHelper.CommonValues.getValueEmpty();
        String invalidMonth = DataHelper.CommonValues.getValueEmpty();
        String invalidYear = DataHelper.CommonValues.getValueEmpty();
        String invalidHolder = DataHelper.CommonValues.getValueEmpty();
        String invalidCvc = DataHelper.CommonValues.getValueEmpty();

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(invalidCardNumber, invalidMonth, invalidYear, invalidHolder, invalidCvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }
}
