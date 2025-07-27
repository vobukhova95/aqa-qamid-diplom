package test;

import com.codeborne.selenide.Selenide;
import data.DataHelper;
import data.SQLHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.CardPaymentPage;
import page.PaymentMethodPage;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.getOrderEntity;
import static data.SQLHelper.getPaymentEntity;

public class CardPaymentTests {

    @BeforeEach
    void setup() {
        open("http://localhost:8080/");
    }

    @AfterAll
    static void deleteData() {
        SQLHelper.cleanDatabase();
    }


    @Test
    void testSuccessOperation() {
        String numberCard = DataHelper.CardNumber.numberApprovedCard();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        PaymentMethodPage mainPage = new PaymentMethodPage();
        CardPaymentPage cardPage = mainPage.selectCardPayment();

        cardPage.successfulCardOperation(numberCard, month, year, holder, cvc);

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals("APPROVED", payment.getStatus());
        Assertions.assertEquals(4_500_000, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());

    }

    @Test
    void testErrorOperation() {
        String numberCard = DataHelper.CardNumber.numberDeclinedCard();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(1);
        String holder = DataHelper.Holder.validHolder();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        PaymentMethodPage mainPage = new PaymentMethodPage();
        CardPaymentPage cardPage = mainPage.selectCardPayment();

        cardPage.unsuccessfulCardOperation(numberCard, month, year, holder, cvc);

        SQLHelper.PaymentEntity payment = getPaymentEntity();
        SQLHelper.OrderEntity order = getOrderEntity();

        Assertions.assertEquals("DECLINED", payment.getStatus());
        Assertions.assertEquals(4_500_000, payment.getAmount());
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId());
    }
}



