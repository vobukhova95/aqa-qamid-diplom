package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$;

public class PaymentMethodPage {

    private final ElementsCollection buttons = $$("button");

    // Метод выбора оплаты картой
    public CardPaymentPage selectCardPayment() {
        buttons.findBy(Condition.exactText("Купить")).click();
        return new CardPaymentPage();
    }

    // Метод выбора оплаты в кредит
    public CreditCardPaymentPage selectCreditPayment() {
        buttons.findBy(Condition.exactText("Купить в кредит")).click();
        return new CreditCardPaymentPage();
    }


}
