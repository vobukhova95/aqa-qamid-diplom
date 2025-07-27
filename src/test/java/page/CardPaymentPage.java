package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$$;


public class CardPaymentPage {


    private final ElementsCollection headers = $$("h3");

    private final SelenideElement continueButton = $$("button")
            .findBy(Condition.matchText("Продолжить"));

    private final SelenideElement requestBank = $$("button")
            .findBy(Condition.matchText("Отправляем запрос в Банк..."));

    private final SelenideElement successNotification = $$(".notification").findBy(
            Condition.and(
                    "title and content",
                    Condition.text("Успешно"),
                    Condition.text("Операция одобрена Банком.")
            ));

    private final SelenideElement errorNotification = $$(".notification").findBy(
            Condition.and(
                    "title and content",
                    Condition.text("Ошибка"),
                    Condition.text("Ошибка! Банк отказал в проведении операции.")
            ));

    private final ElementsCollection fields = $$(".input__inner");
    private final Map<String, SelenideElement> inputsByLabel;


    public CardPaymentPage() {
        headers.findBy(Condition.exactText("Оплата по карте"))
                .shouldBe(Condition.visible);

        inputsByLabel = fields.stream()
                .collect(Collectors.toMap(
                        field -> field.$(".input__top").getText(),
                        field -> field.$("input")
                ));
    }

    /**
     * Заполнение формы без клика на кнопку "Продолжить".
     * Нужен для тестов, где проверяется, что в полях не отображаются невалидные значения после ввода.
     */
    public void fillCardForm(String number, String month, String year, String holder, String cvc) {
        inputsByLabel.get("Номер карты").setValue(number);
        inputsByLabel.get("Месяц").setValue(month);
        inputsByLabel.get("Год").setValue(year);
        inputsByLabel.get("Владелец").setValue(holder);
        inputsByLabel.get("CVC/CVV").setValue(cvc);
    }

    //Заполнение формы плюс клик по кнопке "Продолжить".
    public void sendingCardForm(String number, String month, String year, String holder, String cvc) {
        fillCardForm(number, month, year, holder, cvc);
        continueButton.click();
        requestBank.shouldBe(Condition.visible);
        requestBank.shouldBe(Condition.disabled);
    }

    //Успешная операция по карте APPROVED.
    public void successfulCardOperation(String number, String month, String year, String holder, String cvc) {
        sendingCardForm(number, month, year, holder, cvc);
        successNotification.shouldBe(Condition.visible, Duration.ofSeconds(15));
        errorNotification.shouldNotBe(Condition.visible);
    }

    //Отклоненная операция по карте DECLINED.
    public void unsuccessfulCardOperation(String number, String month, String year, String holder, String cvc) {
        sendingCardForm(number, month, year, holder, cvc);
        errorNotification.shouldBe(Condition.visible, Duration.ofSeconds(15));
        successNotification.shouldNotBe(Condition.visible);

    }

    /**
     * Поиск сообщения об ошибке под полем.
     *
     * @param field     - название поля, под которым должно быть сообщение об ошибке.
     * @param textError - ожидаемый текст ошибки.
     */
    public void searchError(String field, String textError) {
        inputsByLabel.get(field)
                .closest(".input__inner")
                .$(".input__sub")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(textError));
    }

    /**
     * Проверка, что поле не принимает невалидные значения.
     * Например, поле "Месяц" не принмиает символы.
     *
     * @param field - название поля, для которого выполняется проверка.
     */
    public void checkFieldEmptyAfterInvalidInput(String field) {
        inputsByLabel.get(field)
                .shouldHave(Condition.value(""));
    }

    /**
     * Проверка, что поле не принимает больше символов, чем положено.
     * Например, при вводе 3х цифр в поле "Месяц", последняя цифры обрезается.
     *
     * @param field          - название поля, для которого выполняется проверка.
     * @param expectedLength - ожидаемая длина строки.
     */
    public void checkInputLength(String field, int expectedLength) {
        inputsByLabel.get(field)
                .shouldHave(Condition.matchText("^.{" + expectedLength + "}$"));
    }
}
