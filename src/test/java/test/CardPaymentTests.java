package test;

import data.DataHelper;
import data.SQLHelper;
import org.junit.jupiter.api.*;
import page.CardPaymentPage;
import page.PaymentMethodPage;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.getOrderEntity;
import static data.SQLHelper.getPaymentEntity;

public class CardPaymentTests {


    private String validNumberCard;
    private String month;
    private String year;
    private String holder;
    private String cvc;
    private PaymentMethodPage mainPage;
    private CardPaymentPage cardPage;

    @BeforeEach
    void setup() {
        open("http://localhost:8080/");
        validNumberCard = DataHelper.CardNumber.numberApprovedCard();
        month = DataHelper.Month.validMonth();
        year = DataHelper.CardYear.generateYearOffset(1);
        holder = DataHelper.Holder.validHolder();
        cvc = DataHelper.CommonValues.generateDigits(3);

        mainPage = new PaymentMethodPage();
        cardPage = mainPage.selectCardPayment();
    }

    @AfterAll
    static void deleteData() {
        SQLHelper.cleanDatabase();
    }


    @Test
    @DisplayName("Should submit form successfully with approved card number")
    void shouldSubmitFormSuccessfullyWithApprovedCardNumber() {

        cardPage.successfulCardOperation(validNumberCard, month, year, holder, cvc);

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals("APPROVED", payment.getStatus());
        Assertions.assertEquals(4_500_000, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());

    }

    @Test
    @DisplayName("Should submit form successfully with declined card number")
    void shouldSubmitFormSuccessfullyWithDeclinedCardNumber() {
        String declinedNumberCard = DataHelper.CardNumber.numberDeclinedCard();
        cardPage.unsuccessfulCardOperation(declinedNumberCard, month, year, holder, cvc);

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals("DECLINED", payment.getStatus());
        Assertions.assertEquals(4_500_000, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }

    @Test
    @DisplayName("Entering 15-digit card number should be rejected")
    void shouldFailWith15DigitCardNumber() {
        String invalidNumberCard = DataHelper.CommonValues.generateDigits(15);
        cardPage.fillCardForm(invalidNumberCard, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError("Номер карты", "Неверный формат");
    }

    @Test
    @DisplayName("Should trim the 17th digit of the card number")
    void shouldTrim17DigitCardNumber() {
        String invalidNumberCard = DataHelper.CommonValues.generateDigits(17);
        StringBuilder sb = new StringBuilder();
        sb.append(invalidNumberCard, 0, 4).append(" ");
        sb.append(invalidNumberCard, 4, 8).append(" ");
        sb.append(invalidNumberCard, 8, 12).append(" ");
        sb.append(invalidNumberCard, 12, 16);
        String expectedNumberCard = sb.toString();
        cardPage.fillFieldAndCheckInputLength("Номер карты", invalidNumberCard, expectedNumberCard);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the symbols")
    void shouldClearCardNumberFieldWhenEnteringSymbols() {
        String invalidNumberCard = DataHelper.CommonValues.invalidValueSymbols(3);
        cardPage.fillFieldAndCheckEmptyAfterInvalidInput("Номер карты", invalidNumberCard);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the cyrillic")
    void shouldClearCardNumberFieldWhenEnteringCyrillic() {
        String invalidNumberCard = DataHelper.CommonValues.invalidValueCyrillic(4);
        cardPage.fillFieldAndCheckEmptyAfterInvalidInput("Номер карты", invalidNumberCard);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the latin")
    void shouldClearCardNumberFieldWhenEnteringLatin() {
        String invalidNumberCard = DataHelper.CommonValues.generateLetters(4);
        cardPage.fillFieldAndCheckEmptyAfterInvalidInput("Номер карты", invalidNumberCard);
    }

    @Test
    @DisplayName("The card number must remain blank after entering the space")
    void shouldClearCardNumberFieldWhenEnteringSpace() {
        String invalidNumberCard = DataHelper.CommonValues.invalidValueSpace();
        cardPage.fillFieldAndCheckEmptyAfterInvalidInput("Номер карты", invalidNumberCard);
    }

    @Test
    @DisplayName("Entering all zeros in card number should be rejected")
    void shouldFailWithAllZerosInCardNumber() {
        String invalidNumberCard = DataHelper.CardNumber.invalidNumberCardAllZeros();
        cardPage.fillCardForm(invalidNumberCard, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError("Номер карты", "Неверный формат");
    }

    @Test
    @DisplayName("Entering empty in card number should be rejected")
    void shouldFailWithEmptyInCardNumber() {
        String invalidNumberCard = DataHelper.CommonValues.invalidValueEmpty();
        cardPage.fillCardForm(invalidNumberCard, month, year, holder, cvc);
        cardPage.clickContinueButton();
        cardPage.searchError("Номер карты", "Поле обязательно для заполнения");
    }









}



