package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$;

public class CreditCardPaymentPage {

    private final ElementsCollection headers = $$("h3");


    public CreditCardPaymentPage() {
        headers.findBy(Condition.exactText("Кредит по данным карты"))
                .shouldBe(Condition.visible);
    }
}
