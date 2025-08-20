package test.ui;

import data.ui.DataHelper;
import data.bd.SQLHelper;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import page.CardPaymentPage;
import page.FieldName;
import page.PaymentMethodPage;

import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

public class CardPaymentTests {


    private PaymentMethodPage mainPage;
    private CardPaymentPage cardPage;

    private final String statusApproved = DataHelper.CommonValues.getStatusApproved();
    private final String statusDeclined = DataHelper.CommonValues.getStatusDeclined();
    private final int amountTravel = DataHelper.CommonValues.getAmountTravel();
    private final String errorTextRequiredField = DataHelper.CommonValues.getErrorTextRequiredField();
    private final String errorTextInvalidFormat = DataHelper.CommonValues.getErrorTextInvalidFormat();
    private final String errorTextExpirationDateIncorrect = DataHelper.CommonValues.getErrorTextExpirationDateIsIncorrect();
    private final String errorTextCardExpired = DataHelper.CommonValues.getErrorTextCardExpired();


    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }


    @BeforeEach
    void setUp() {
        open("http://localhost:8080/");

        mainPage = new PaymentMethodPage();
        cardPage = mainPage.selectCardPayment();
    }

    @BeforeEach
    void deleteData(TestInfo testInfo) {
        if (testInfo.getTags().contains("CleanDB")) {
            SQLHelper.cleanDatabase();
        }
    }

    @BeforeAll
    static void beforeAll() {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true));
    }

    @AfterAll
    static void afterAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("CardNumber")
    @Tag("UI")
    @DisplayName("Should process purchase with an approved card successfully")
    void shouldAcceptFormWhenCardNumberIsApproved() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("CardNumber")
    @Tag("UI")
    @DisplayName("Should display bank-decline notification when the card number is the declined test card")
    void shouldShowDeclineNotificationWhenCardNumberIsDeclined() {
        String cardNumber = DataHelper.CardNumber.declinedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkErrorNotification();


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
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("UI")
    @DisplayName("Should display validation error when card number has 15 digits")
    void shouldShowInvalidFormatWhenCardNumberIsFifteenDigits() {
        String invalidCardNumber = DataHelper.CommonValues.generateDigits(15);
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(invalidCardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.CARD_NUMBER, errorTextInvalidFormat);

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
    @Tag("UI")
    @DisplayName("Should keep only 16 digits when user attempts to enter 17 digits")
    void shouldTrimCardNumberWhenEnteringSeventeenDigits() {
        String invalidCardNumber = DataHelper.CommonValues.generateDigits(17);
        String expectedCardNumber = DataHelper.CardNumber.trimTo16Digits(invalidCardNumber);
        cardPage.fillOneField(FieldName.CARD_NUMBER, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.CARD_NUMBER, expectedCardNumber);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("UI")
    @DisplayName("Should display 'Invalid format' when card number is '0000 0000 0000 0000'")
    void shouldShowInvalidFormatWhenCardNumberIsAllZeros() {
        String invalidCardNumber = DataHelper.CardNumber.invalidCardNumberAllZeros();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(invalidCardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.CARD_NUMBER, errorTextInvalidFormat);

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
    @Tag("UI")
    @DisplayName("Should leave card number empty when entering Latin letters")
    void shouldRejectLatinLettersInCardNumber() {
        String invalidCardNumber = DataHelper.CommonValues.generateLetters(16);
        String expectedCardNumber = DataHelper.CommonValues.getValueEmpty();
        cardPage.fillOneField(FieldName.CARD_NUMBER, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.CARD_NUMBER, expectedCardNumber);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("UI")
    @DisplayName("Should leave card number empty when entering Cyrillic letters")
    void shouldRejectCyrillicInCardNumber() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueCyrillic(16);
        String expectedCardNumber = DataHelper.CommonValues.getValueEmpty();
        cardPage.fillOneField(FieldName.CARD_NUMBER, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.CARD_NUMBER, expectedCardNumber);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("UI")
    @DisplayName("Should leave card number empty when entering symbols/special characters")
    void shouldRejectSymbolsInCardNumber() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueSymbols(16);
        String expectedCardNumber = DataHelper.CommonValues.getValueEmpty();
        cardPage.fillOneField(FieldName.CARD_NUMBER, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.CARD_NUMBER, expectedCardNumber);
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("UI")
    @DisplayName("Should leave card number empty when entering only spaces")
    void shouldRejectSpacesInCardNumber() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueSpace();
        String expectedCardNumber = DataHelper.CommonValues.getValueEmpty();
        cardPage.fillOneField(FieldName.CARD_NUMBER, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.CARD_NUMBER, expectedCardNumber);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Number Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("CardNumber")
    @Tag("UI")
    @DisplayName("Should display required-field error when card number is empty")
    void shouldShowRequiredErrorForEmptyCardNumber() {
        String invalidCardNumber = DataHelper.CommonValues.getValueEmpty();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(invalidCardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.CARD_NUMBER, errorTextRequiredField);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should accept the form when expiration month is current month and year")
    void shouldAcceptFormWhenMonthIsCurrentMonthCurrentYear() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String validMonth = DataHelper.Month.validCurrentMonth();
        String year = DataHelper.CardYear.generateYearOffset(0);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, validMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should accept the form when expiration month is 01")
    void shouldAcceptFormWhenMonthIsJanuary() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String validMonth = DataHelper.Month.validMonth01();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, validMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should accept the form when expiration month is 12")
    void shouldAcceptFormWhenMonthIsDecember() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String validMonth = DataHelper.Month.validMonth12();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, validMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should display expiration-date error when month is previous month in current year")
    void shouldShowCardExpirationErrorWhenMonthIsPreviousInCurrentYear() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.Month.invalidMonthCurrentMonthMinus1();
        String year = DataHelper.CardYear.generateYearOffset(0);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, errorTextCardExpired);

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
    @Tag("UI")
    @DisplayName("Should keep only first two digits when user enters 3 digits into month")
    void shouldTrimMonthWhenEnteringThreeDigits() {
        String invalidMonth = DataHelper.CommonValues.generateDigits(3);
        String expectedMonth = DataHelper.CommonValues.truncateToMaxLength(invalidMonth, 2);
        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should display 'Invalid format' when month contains one digit")
    void shouldShowInvalidFormatWhenMonthIsOneDigit() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.generateDigits(1);
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, errorTextInvalidFormat);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should display expiration-date error when month is 00")
    void shouldShowCardExpirationErrorWhenMonthIsAllZero() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.Month.invalidMonth00();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, errorTextExpirationDateIncorrect);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should display expiration-date error when month is 13")
    void shouldShowCardExpirationErrorWhenMonthIsThirteen() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.Month.invalidMonth13();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, errorTextExpirationDateIncorrect);

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
    @Tag("UI")
    @DisplayName("Should leave month field empty when entering Latin letters")
    void shouldRejectLatinLettersInMonth() {
        String invalidMonth = DataHelper.CommonValues.generateLetters(2);
        String expectedMonth = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should leave month field empty when entering Cyrillic letters")
    void shouldRejectCyrillicInMonth() {
        String invalidMonth = DataHelper.CommonValues.invalidValueCyrillic(2);
        String expectedMonth = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should leave month field empty when entering symbols in month")
    void shouldRejectSymbolsInMonth() {
        String invalidMonth = DataHelper.CommonValues.invalidValueSymbols(2);
        String expectedMonth = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should leave month field empty when entering only spaces in month")
    void shouldRejectSpacesInMonth() {
        String invalidMonth = DataHelper.CommonValues.invalidValueSpace();
        String expectedMonth = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Month Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Month")
    @Tag("UI")
    @DisplayName("Should display required-field error when month is empty")
    void shouldShowRequiredErrorForEmptyMonth() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.getValueEmpty();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, errorTextRequiredField);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should accept the form when expiration year is the current year")
    void shouldAcceptFormWhenYearIsCurrentYear() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth12();
        String validYear = DataHelper.CardYear.generateYearOffset(0);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, validYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should accept the form when expiration year is current year + 4")
    void shouldAcceptFormWhenYearIsCurrentPlusFour() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String validYear = DataHelper.CardYear.generateYearOffset(4);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, validYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should accept the form when expiration year is current year + 5")
    void shouldAcceptFormWhenYearIsCurrentPlusFive() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String validYear = DataHelper.CardYear.generateYearOffset(5);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, validYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should display card expired error when year is in the past")
    void shouldShowExpiredErrorWhenYearIsPrevious() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CardYear.generateYearOffset(-1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, invalidYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.YEAR, errorTextCardExpired);

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
    @Tag("UI")
    @DisplayName("Should keep only first two digits when user enters 3 digits into year")
    void shouldTrimYearWhenEnteringThreeDigits() {
        String invalidYear = DataHelper.CommonValues.generateDigits(3);
        String expectedYear = DataHelper.CommonValues.truncateToMaxLength(invalidYear, 2);

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should display 'Invalid format' when year contains one digit")
    void shouldShowInvalidFormatWhenYearIsOneDigit() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.generateDigits(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, invalidYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.YEAR, errorTextInvalidFormat);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should display expiration-date error when year is current year + 6")
    void shouldShowCardExpirationErrorWhenYearIsCurrentPlusSix() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CardYear.generateYearOffset(6);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, invalidYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.YEAR, errorTextExpirationDateIncorrect);

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
    @Tag("UI")
    @DisplayName("Should leave year field empty when entering Latin letters")
    void shouldRejectLatinLettersInYear() {
        String invalidYear = DataHelper.CommonValues.generateLetters(2);
        String expectedYear = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should leave year field empty when entering Cyrillic letters")
    void shouldRejectCyrillicInYear() {
        String invalidYear = DataHelper.CommonValues.invalidValueCyrillic(2);
        String expectedYear = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should leave year field empty when entering symbols in year")
    void shouldRejectSymbolsInYear() {
        String invalidYear = DataHelper.CommonValues.invalidValueSymbols(2);
        String expectedYear = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should leave year field empty when entering only spaces in year")
    void shouldRejectSpacesInYear() {
        String invalidYear = DataHelper.CommonValues.invalidValueSpace();
        String expectedYear = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Year Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Year")
    @Tag("UI")
    @DisplayName("Should display required-field error when year is empty")
    void shouldShowRequiredErrorForEmptyYear() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.getValueEmpty();
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, invalidYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.YEAR, errorTextRequiredField);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should accept holder name containing a hyphen")
    void shouldAcceptHolderWithHyphen() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String validHolder = DataHelper.Holder.validHolderHyphenate();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should accept holder name containing an apostrophe")
    void shouldAcceptHolderWithApostrophe() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String validHolder = DataHelper.Holder.validHolderApostrophe();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should accept holder name containing multiple parts")
    void shouldAcceptHolderWithMultipleParts() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String validHolder = DataHelper.Holder.validHolderMultipleNames();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should accept minimal valid holder value (two letters separated by space)")
    void shouldAcceptHolderTwoLettersAndSpaceAsMinimum() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String validHolder = DataHelper.Holder.validHolderTwoLettersAndSpace();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should accept boundary minimal valid holder (three letters + space)")
    void shouldAcceptHolderThreeLettersAndSpaceAsBoundary() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String validHolder = DataHelper.Holder.validHolderThreeLettersAndSpace();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should accept holder value of length 50 (max allowed)")
    void shouldAcceptHolderWithMaxFiftyChars() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String validHolder = DataHelper.CommonValues.generateLetters(50);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should display validation error when holder is a single word (no space)")
    void shouldShowInvalidFormatWhenHolderIsSingleWord() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.Holder.invalidHolderOneWord();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, invalidHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.HOLDER, errorTextInvalidFormat);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should display validation error when holder length is less than minimal allowed")
    void shouldShowInvalidFormatWhenHolderTooShort() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.CommonValues.generateLetters(2);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, invalidHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.HOLDER, errorTextInvalidFormat);

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
    @Tag("UI")
    @DisplayName("Should trim holder to 50 characters when entering more than 50 chars")
    void shouldTrimHolderWhenEnteringMoreThanFiftyChars() {
        String invalidHolder = DataHelper.CommonValues.generateLetters(51);
        String expectedHolder = DataHelper.CommonValues.truncateToMaxLength(invalidHolder, 50);

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should display validation error when holder contains Cyrillic characters")
    void shouldRejectCyrillicInHolder() {
        String invalidHolder = DataHelper.CommonValues.invalidValueCyrillic(7);
        String expectedHolder = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should display validation error when holder contains digits")
    void shouldRejectDigitsInHolder() {
        String invalidHolder = DataHelper.CommonValues.generateDigits(5);
        String expectedHolder = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should display validation error when holder contains special symbols")
    void shouldRejectSymbolsInHolder() {
        String invalidHolder = DataHelper.CommonValues.invalidValueSymbols(8);
        String expectedHolder = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should display required-field error when holder contains only spaces")
    void shouldShowRequiredErrorWhenHolderIsSpacesOnly() {
        String invalidHolder = DataHelper.CommonValues.invalidValueSpace();
        String expectedHolder = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("Card Holder Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("Holder")
    @Tag("UI")
    @DisplayName("Should display required-field error when holder is empty")
    void shouldShowRequiredErrorForEmptyHolder() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String invalidHolder = DataHelper.CommonValues.getValueEmpty();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, invalidHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.HOLDER, errorTextRequiredField);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("CVC")
    @Tag("UI")
    @DisplayName("Should accept CVC value 000")
    void shouldAcceptCvcAllZero() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String holder = DataHelper.Holder.validHolder();
        String validCvc = DataHelper.CVC.validCVC000();

        cardPage.fillCardForm(cardNumber, month, year, holder, validCvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Positive")
    @Tag("CVC")
    @Tag("UI")
    @DisplayName("Should accept CVC value 999")
    void shouldAcceptCvcAllNine() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(3);
        String holder = DataHelper.Holder.validHolder();
        String validCvc = DataHelper.CVC.validCVC999();

        cardPage.fillCardForm(cardNumber, month, year, holder, validCvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.checkSuccessNotification();

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
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("CVC")
    @Tag("UI")
    @DisplayName("Should display 'Invalid format' when CVC contains only two digits")
    void shouldShowInvalidFormatWhenCvcIsTwoDigits() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String invalidCvc = DataHelper.CommonValues.generateDigits(2);

        cardPage.fillCardForm(cardNumber, month, year, holder, invalidCvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.CVC, errorTextInvalidFormat);

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
    @Tag("UI")
    @DisplayName("Should keep only first three digits when user enters 4 digits into CVC")
    void shouldTrimCvcWhenEnteringFourDigits() {
        String invalidCvc = DataHelper.CommonValues.generateDigits(4);
        String expectedCvc = DataHelper.CommonValues.truncateToMaxLength(invalidCvc, 3);

        cardPage.fillOneField(FieldName.CVC, invalidCvc);
        cardPage.checkFieldValue(FieldName.CVC, expectedCvc);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("UI")
    @DisplayName("Should leave CVC field empty when entering Latin letters")
    void shouldRejectLatinLettersInCvc() {
        String invalidCvc = DataHelper.CommonValues.generateLetters(3);
        String expectedCvc = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.CVC, invalidCvc);
        cardPage.checkFieldValue(FieldName.CVC, expectedCvc);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("UI")
    @DisplayName("Should leave CVC field empty when entering Cyrillic letters")
    void shouldRejectCyrillicInCvc() {
        String invalidCvc = DataHelper.CommonValues.invalidValueCyrillic(3);
        String expectedCvc = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.CVC, invalidCvc);
        cardPage.checkFieldValue(FieldName.CVC, expectedCvc);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("UI")
    @DisplayName("Should leave CVC field empty when entering symbols in CVC")
    void shouldRejectSymbolsInCvc() {
        String invalidCvc = DataHelper.CommonValues.invalidValueSymbols(3);
        String expectedCvc = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.CVC, invalidCvc);
        cardPage.checkFieldValue(FieldName.CVC, expectedCvc);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Negative")
    @Tag("CVC")
    @Tag("UI")
    @DisplayName("Should leave CVC field empty when entering only spaces in CVC")
    void shouldRejectSpacesInCvc() {
        String invalidCvc = DataHelper.CommonValues.invalidValueSpace();
        String expectedCvc = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.CVC, invalidCvc);
        cardPage.checkFieldValue(FieldName.CVC, expectedCvc);
    }


    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment Form")
    @Story("CVC Field Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("CleanDB")
    @Tag("Negative")
    @Tag("CVC")
    @Tag("UI")
    @DisplayName("Should display required-field error when CVC is empty")
    void shouldShowRequiredErrorForEmptyCvc() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = DataHelper.Holder.validHolder();
        String invalidCvc = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillCardForm(cardNumber, month, year, holder, invalidCvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.CVC, errorTextRequiredField);

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
    @Tag("CleanDB")
    @Tag("UI")
    @DisplayName("Should display required-field errors for all fields when the form is submitted empty")
    void shouldShowAllRequiredErrorsWhenSubmittingEmptyForm() {
        cardPage.clickContinueButton();

        cardPage.searchError(FieldName.CARD_NUMBER, errorTextRequiredField);
        cardPage.searchError(FieldName.MONTH, errorTextRequiredField);
        cardPage.searchError(FieldName.YEAR, errorTextRequiredField);
        cardPage.searchError(FieldName.HOLDER, errorTextRequiredField);
        cardPage.searchError(FieldName.CVC, errorTextRequiredField);

        SQLHelper.assertNoOrders();
        SQLHelper.assertNoPayments();
    }
}



