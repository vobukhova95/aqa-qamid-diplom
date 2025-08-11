package test;

import data.DataHelper;
import data.SQLHelper;
import org.junit.jupiter.api.*;
import page.CardPaymentPage;
import page.FieldName;
import page.PaymentMethodPage;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.getOrderEntity;
import static data.SQLHelper.getPaymentEntity;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

public class CardPaymentTests {


    private PaymentMethodPage mainPage;
    private CardPaymentPage cardPage;

    private final String statusApproved = DataHelper.CommonValues.getStatusApproved();
    private final String statusDeclined = DataHelper.CommonValues.getStatusDeclined();
    private final int costTravel = DataHelper.CommonValues.getCostTravel();
    private final String errorTextRequiredField = DataHelper.CommonValues.getErrorTextRequiredField();
    private final String errorTextIncorrectFormat = DataHelper.CommonValues.getErrorTextIncorrectFormat();
    private final String errorTextExpirationDateIncorrect = DataHelper.CommonValues.getErrorTextExpirationDateIsIncorrect();
    private final String getErrorTextCardExpired = DataHelper.CommonValues.getErrorTextCardExpired();



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

    /*@AfterEach
    void deleteData() {
        SQLHelper.cleanDatabase();}*/


    @Test
    @DisplayName("Should submit the form successfully when an approved card number is provided")
    void shouldSubmitFormSuccessfullyWithApprovedCardNumber() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);
        cardPage.fillCardForm(cardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();;
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());

    }

    @Test
    @DisplayName("Should submit the form successfully when a declined card number is provided")
    void shouldSubmitFormSuccessfullyWithDeclinedCardNumber() {
        String cardNumber = DataHelper.CardNumber.declinedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);
        cardPage.fillCardForm(cardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();;
        cardPage.unsuccessfulCardOperation();


        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusDeclined, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }


    @Test
    @DisplayName("Entering 15-digit card number should be rejected")
    void shouldFailWith15DigitCardNumber() {
        String invalidCardNumber = DataHelper.CommonValues.generateDigits(15);
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(invalidCardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.NUMBER_CARD, errorTextIncorrectFormat);

    }

    @Test
    @DisplayName("Should trim the 17th digit of the card number")
    void shouldTrim17DigitCardNumber() {
        String invalidCardNumber = DataHelper.CommonValues.generateDigits(17);
        String expectedCardNumber = DataHelper.CardNumber.trimTo16Digits(invalidCardNumber);
        cardPage.fillOneField(FieldName.NUMBER_CARD, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.NUMBER_CARD, expectedCardNumber);

    }

    @Test
    @DisplayName("Entering all zeros in card number should be rejected")
    void shouldFailWithAllZerosInCardNumber() {
        String invalidCardNumber = DataHelper.CardNumber.invalidCardNumberAllZeros();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(invalidCardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.NUMBER_CARD, errorTextIncorrectFormat);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the latin")
    void shouldClearCardNumberFieldWhenEnteringLatin() {
        String invalidCardNumber = DataHelper.CommonValues.generateLetters(16);
        String expectedCardNumber = DataHelper.CommonValues.getValueEmpty();
        cardPage.fillOneField(FieldName.NUMBER_CARD, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.NUMBER_CARD, expectedCardNumber);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the cyrillic")
    void shouldClearCardNumberFieldWhenEnteringCyrillic() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueCyrillic(16);
        String expectedCardNumber = DataHelper.CommonValues.getValueEmpty();
        cardPage.fillOneField(FieldName.NUMBER_CARD, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.NUMBER_CARD, expectedCardNumber);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the symbols")
    void shouldClearCardNumberFieldWhenEnteringSymbols() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueSymbols(16);
        String expectedCardNumber = DataHelper.CommonValues.getValueEmpty();
        cardPage.fillOneField(FieldName.NUMBER_CARD, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.NUMBER_CARD, expectedCardNumber);
    }


    @Test
    @DisplayName("The card number must remain blank after entering the space")
    void shouldClearCardNumberFieldWhenEnteringSpace() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueSpace();
        String expectedCardNumber = DataHelper.CommonValues.getValueEmpty();
        cardPage.fillOneField(FieldName.NUMBER_CARD, invalidCardNumber);
        cardPage.checkFieldValue(FieldName.NUMBER_CARD, expectedCardNumber);
    }

    @Test
    @DisplayName("Entering empty in card number should be rejected")
    void shouldFailWithEmptyInCardNumber() {
        String invalidCardNumber = DataHelper.CommonValues.getValueEmpty();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(invalidCardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.NUMBER_CARD, errorTextRequiredField);
    }


    @Test
    @DisplayName("Verifies that the payment form accepts a card with the current month and current year as valid expiration date")
    void shouldAcceptCardWithCurrentMonthAndYear(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String validMonth = DataHelper.Month.validCurrentMonth();
        String year = DataHelper.CardYear.generateYearOffset(0);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, validMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }


    @Test
    @DisplayName("Should accept card with expires month January (01)")
    void shouldAcceptCardWithJanuaryAsExpiresMonth(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String validMonth = DataHelper.Month.validMonth01();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, validMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("Should accept card with expires month December (12)")
    void shouldAcceptCardWithDecemberAsExpiresMonth(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String validMonth = DataHelper.Month.validMonth12();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, validMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("Should reject card with previous month of the current year")
    void shouldRejectCardWithPreviousMonthOfCurrentYear(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.Month.invalidMonthCurrentMonthMinus1();
        String year = DataHelper.CardYear.generateYearOffset(0);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, getErrorTextCardExpired);
    }

    @Test
    @DisplayName("Verifies that the 'Month' field automatically trims the input to two digits when the user enters three digits")
    void shouldTrimMonthFieldToTwoDigits(){
        String invalidMonth = DataHelper.CommonValues.generateDigits(3);
        String expectedMonth = DataHelper.CommonValues.truncateToMaxLength(invalidMonth, 2);
        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);

    }

    @Test
    @DisplayName("Error 'Invalid format' when entering a single digit in the 'Month' field")
    void shouldRejectMonthFieldWithOneDigit() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.generateDigits(1);
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, errorTextIncorrectFormat);

    }

    @Test
    @DisplayName("Show error for month value '00'")
    void shouldShowErrorForMonthValueZeroZero() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.Month.invalidMonth00();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, errorTextExpirationDateIncorrect);
    }

    @Test
    @DisplayName("Show error for month value '13'")
    void shouldShowErrorForMonthValueThirteen() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.Month.invalidMonth13();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, errorTextExpirationDateIncorrect);
    }

    @Test
    @DisplayName("The month field must remain blank after entering the latin")
    void shouldClearMonthFieldWhenEnteringLatin(){
        String invalidMonth = DataHelper.CommonValues.generateLetters(2);
        String expectedMonth = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);
    }


    @Test
    @DisplayName("The month field must remain blank after entering the cyrillic")
    void shouldClearMonthFieldWhenEnteringCyrillic(){
        String invalidMonth = DataHelper.CommonValues.invalidValueCyrillic(2);
        String expectedMonth = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);
    }

    @Test
    @DisplayName("The month field must remain blank after entering the symbols")
    void shouldClearMonthFieldWhenEnteringSymbols(){
        String invalidMonth = DataHelper.CommonValues.invalidValueSymbols(2);
        String expectedMonth = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);
    }

    @Test
    @DisplayName("The month field must remain blank after entering the space")
    void shouldClearMonthFieldWhenEnteringSpace(){
        String invalidMonth = DataHelper.CommonValues.invalidValueSpace();
        String expectedMonth = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.MONTH, invalidMonth);
        cardPage.checkFieldValue(FieldName.MONTH, expectedMonth);
    }

    @Test
    @DisplayName("Entering empty in month should be rejected")
    void shouldFailWithEmptyInMonthField() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String invalidMonth = DataHelper.CommonValues.getValueEmpty();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, invalidMonth, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.MONTH, errorTextRequiredField);
    }




    @Test
    @DisplayName("Verifies that the payment form accepts a card with the current year as valid expiration date")
    void shouldAcceptCardWithCurrentYear(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth12();
        String validYear = DataHelper.CardYear.generateYearOffset(0);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, validYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldAcceptCardWithCurrentYearPlusFour(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String validYear = DataHelper.CardYear.generateYearOffset(4);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, validYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldAcceptCardWithCurrentYearPlusFive(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String validYear = DataHelper.CardYear.generateYearOffset(5);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, validYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldRejectCardWithPreviousYear(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CardYear.generateYearOffset(-1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, invalidYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.YEAR, getErrorTextCardExpired);
    }

    @Test
    @DisplayName("")
    void shouldTrimYearFieldToTwoDigits(){
        String invalidYear = DataHelper.CommonValues.generateDigits(3);
        String expectedYear = DataHelper.CommonValues.truncateToMaxLength(invalidYear, 2);

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);

    }

    @Test
    @DisplayName("")
    void shouldRejectYearFieldWithOneDigit() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.generateDigits(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, invalidYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.YEAR, errorTextIncorrectFormat);

    }

    @Test
    @DisplayName("")
    void shouldShowErrorForYearValueCurrentPlusSix() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CardYear.generateYearOffset(6);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, invalidYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.YEAR, errorTextExpirationDateIncorrect);
    }

    @Test
    @DisplayName("")
    void shouldClearYearFieldWhenEnteringLatin(){
        String invalidYear = DataHelper.CommonValues.generateLetters(2);
        String expectedYear = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);
    }


    @Test
    @DisplayName("")
    void shouldClearYearFieldWhenEnteringCyrillic(){
        String invalidYear = DataHelper.CommonValues.invalidValueCyrillic(2);
        String expectedYear = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);
    }

    @Test
    @DisplayName("")
    void shouldClearYearFieldWhenEnteringSymbols(){
        String invalidYear = DataHelper.CommonValues.invalidValueSymbols(2);
        String expectedYear = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);
    }

    @Test
    @DisplayName("")
    void shouldClearYearFieldWhenEnteringSpace(){
        String invalidYear = DataHelper.CommonValues.invalidValueSpace();
        String expectedYear = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.YEAR, invalidYear);
        cardPage.checkFieldValue(FieldName.YEAR, expectedYear);
    }


    @Test
    @DisplayName("")
    void shouldFailWithEmptyInYearField() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String invalidYear = DataHelper.CommonValues.getValueEmpty();
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, invalidYear, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.YEAR, errorTextRequiredField);
    }



    @Test
    @DisplayName("")
    void shouldAcceptCardWithHyphenatedName(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String validHolder = DataHelper.Holder.validHolderHyphenate();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldAcceptCardWithApostropheName(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String validHolder = DataHelper.Holder.validHolderApostrophe();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldAcceptCardWithMultipleNames(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String validHolder = DataHelper.Holder.validHolderMultipleNames();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldAcceptCardWithHolderNameTwoLettersSeparatedSpace(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String validHolder = DataHelper.Holder.validHolderTwoLettersAndSpace();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldAcceptCardWithHolderNameThreeLettersSeparatedSpace(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String validHolder = DataHelper.Holder.validHolderThreeLettersAndSpace();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldAcceptCardWithHolderNameFiftyLetters(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String validHolder = DataHelper.CommonValues.generateLetters(50);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, validHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldShowErrorForHolderValueOneWord() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String invalidHolder = DataHelper.Holder.invalidHolderOneWord();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, invalidHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.HOLDER, errorTextIncorrectFormat);
    }


    @Test
    @DisplayName("")
    void shouldShowErrorForHolderValueTwoLetters() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String invalidHolder = DataHelper.CommonValues.generateLetters(2);
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, invalidHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.HOLDER, errorTextIncorrectFormat);
    }


    @Test
    @DisplayName("")
    void shouldTrimHolderFieldToFiftyLetters(){
        String invalidHolder = DataHelper.CommonValues.generateLetters(51);
        String expectedHolder = DataHelper.CommonValues.truncateToMaxLength(invalidHolder, 50);

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);

    }


    @Test
    @DisplayName("")
    void shouldClearHolderFieldWhenEnteringCyrillic(){
        String invalidHolder = DataHelper.CommonValues.invalidValueCyrillic(7);
        String expectedHolder = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);
    }

    @Test
    @DisplayName("")
    void shouldClearHolderFieldWhenEnteringDigits(){
        String invalidHolder = DataHelper.CommonValues.generateDigits(5);
        String expectedHolder = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);
    }


    @Test
    @DisplayName("")
    void shouldClearHolderFieldWhenEnteringSymbols(){
        String invalidHolder = DataHelper.CommonValues.invalidValueSymbols(8);
        String expectedHolder = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);
    }

    @Test
    @DisplayName("")
    void shouldClearHolderFieldWhenEnteringSpace(){
        String invalidHolder = DataHelper.CommonValues.invalidValueSpace();
        String expectedHolder = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.HOLDER, invalidHolder);
        cardPage.checkFieldValue(FieldName.HOLDER, expectedHolder);
    }


    @Test
    @DisplayName("")
    void shouldFailWithEmptyInHolderField() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String invalidHolder = DataHelper.CommonValues.getValueEmpty();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(cardNumber, month, year, invalidHolder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.HOLDER, errorTextRequiredField);
    }



    @Test
    @DisplayName("")
    void shouldAcceptCardWithCVVZeroZeroZero(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String validCvc = DataHelper.CVC.validCVC000();

        cardPage.fillCardForm(cardNumber, month, year, holder, validCvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldAcceptCardWithCVVNineNineNine(){
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String validCvc = DataHelper.CVC.validCVC999();

        cardPage.fillCardForm(cardNumber, month, year, holder, validCvc);
        cardPage.clickContinueButton();
        cardPage.sendRequestToBank();
        cardPage.successfulCardOperation();

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals(statusApproved, payment.getStatus());
        Assertions.assertEquals(costTravel, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("")
    void shouldShowErrorForCVCValueTwoDigits() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String invalidCVC = DataHelper.CommonValues.generateDigits(2);

        cardPage.fillCardForm(cardNumber, month, year, holder, invalidCVC);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.CVC, errorTextIncorrectFormat);
    }

    @Test
    @DisplayName("")
    void shouldTrimCVCFieldToThreeDigits(){
        String invalidCvc = DataHelper.CommonValues.generateDigits(4);
        String expectedCvc = DataHelper.CommonValues.truncateToMaxLength(invalidCvc, 3);

        cardPage.fillOneField(FieldName.CVC, invalidCvc);
        cardPage.checkFieldValue(FieldName.CVC, expectedCvc);

    }

    @Test
    @DisplayName("")
    void shouldClearCVCFieldWhenEnteringLatin(){
        String invalidCVC = DataHelper.CommonValues.generateLetters(3);
        String expectedCVC = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.CVC, invalidCVC);
        cardPage.checkFieldValue(FieldName.CVC, expectedCVC);
    }


    @Test
    @DisplayName("")
    void shouldClearCVCFieldWhenEnteringCyrillic(){
        String invalidCVC = DataHelper.CommonValues.invalidValueCyrillic(3);
        String expectedCVC = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.CVC, invalidCVC);
        cardPage.checkFieldValue(FieldName.CVC, expectedCVC);
    }


    @Test
    @DisplayName("")
    void shouldClearCVCFieldWhenEnteringSymbols(){
        String invalidCVC = DataHelper.CommonValues.invalidValueSymbols(3);
        String expectedCVC = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.CVC, invalidCVC);
        cardPage.checkFieldValue(FieldName.CVC, expectedCVC);
    }

    @Test
    @DisplayName("")
    void shouldClearCVCFieldWhenEnteringSpace(){
        String invalidCVC = DataHelper.CommonValues.invalidValueSpace();
        String expectedCVC = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillOneField(FieldName.CVC, invalidCVC);
        cardPage.checkFieldValue(FieldName.CVC, expectedCVC);
    }


    @Test
    @DisplayName("")
    void shouldFailWithEmptyInCVCField() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String invalidCVC = DataHelper.CommonValues.getValueEmpty();

        cardPage.fillCardForm(cardNumber, month, year, holder, invalidCVC);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.CVC, errorTextRequiredField);
    }

    @Test
    @DisplayName("")

    void shouldShowErrorsWhenSubmittingEmptyForm() {
        cardPage.clickContinueButton();

        cardPage.searchError(FieldName.NUMBER_CARD, errorTextRequiredField);
        cardPage.searchError(FieldName.MONTH, errorTextRequiredField);
        cardPage.searchError(FieldName.YEAR, errorTextRequiredField);
        cardPage.searchError(FieldName.HOLDER, errorTextRequiredField);
        cardPage.searchError(FieldName.CVC, errorTextRequiredField);
    }


}



