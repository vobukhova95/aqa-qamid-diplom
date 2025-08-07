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

public class CardPaymentTests {


    private PaymentMethodPage mainPage;
    private CardPaymentPage cardPage;
    private String statusApproved;
    private String statusDeclined;
    private int costTravel;
    private String errorTextRequiredField;
    private String errorTextIncorrectFormat;

    @BeforeEach
    void setup() {
        open("http://localhost:8080/");

        mainPage = new PaymentMethodPage();
        cardPage = mainPage.selectCardPayment();
        statusApproved = DataHelper.CommonValues.getStatusApproved();
        costTravel = DataHelper.CommonValues.getCostTravel();
        statusDeclined = DataHelper.CommonValues.getStatusDeclined();
        errorTextRequiredField = DataHelper.CommonValues.getErrorTextRequiredField();
        errorTextIncorrectFormat = DataHelper.CommonValues.getErrorTextIncorrectFormat();
    }

    @AfterEach
    void deleteData() {
        SQLHelper.cleanDatabase();}


    @Test
    @DisplayName("Should submit form successfully with approved card number")
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
    @DisplayName("Should submit form successfully with declined card number")
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
        cardPage.checkInputLength(FieldName.NUMBER_CARD, expectedCardNumber);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the symbols")
    void shouldClearCardNumberFieldWhenEnteringSymbols() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueSymbols(16);
        cardPage.fillOneField(FieldName.NUMBER_CARD, invalidCardNumber);
        cardPage.checkFieldEmptyAfterInvalidInput(FieldName.NUMBER_CARD);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the cyrillic")
    void shouldClearCardNumberFieldWhenEnteringCyrillic() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueCyrillic(16);
        cardPage.fillOneField(FieldName.NUMBER_CARD, invalidCardNumber);
        cardPage.checkFieldEmptyAfterInvalidInput(FieldName.NUMBER_CARD);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the latin")
    void shouldClearCardNumberFieldWhenEnteringLatin() {
        String invalidCardNumber = DataHelper.CommonValues.generateLetters(16);
        cardPage.fillOneField(FieldName.NUMBER_CARD, invalidCardNumber);
        cardPage.checkFieldEmptyAfterInvalidInput(FieldName.NUMBER_CARD);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the space")
    void shouldClearCardNumberFieldWhenEnteringSpace() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueSpace();
        cardPage.fillOneField(FieldName.NUMBER_CARD, invalidCardNumber);
        cardPage.checkFieldEmptyAfterInvalidInput(FieldName.NUMBER_CARD);
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
    @DisplayName("Entering empty in card number should be rejected")
    void shouldFailWithEmptyInCardNumber() {
        String invalidCardNumber = DataHelper.CommonValues.invalidValueEmpty();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        cardPage.fillCardForm(invalidCardNumber, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError(FieldName.NUMBER_CARD, errorTextRequiredField);
    }

}



