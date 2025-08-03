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
     * Заполнение одного поля.
     * @param field - название поля, которое нужно заполнить.
     * @param value - значение, которое нужно внести в поле.
     */
    public void fillOneField(String field, String value) {
        inputsByLabel.get(field).setValue(value);
    }



    /**
     * Клик по кнопке "Продолжить".
     */
    public void clickContinueButton() {
        continueButton.click();
    }



    /**
     * Проверка, что после клика по кнопке "Продолжить" вместо "Продолжить" отображается "Отправляем запрос в Банк...".
     * Реализовано в отдельном методе, т.к. текст "Отправляем запрос в Банк..." отображается не во всех случаях.
     * Например, если какое-либо поле будет заполнено с ошибкой, запрос в банк не уйдет.
     */
    public void sendRequestToBank() {
        requestBank.shouldBe(Condition.visible);
        requestBank.shouldBe(Condition.disabled);
    }



    /**
     * Поиск сообщения об ошибке под полем.
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
     * Проверка, что поле не принимает символы, пробелы, латиницу и кириллицу
     * @param field - название поля, для которого выполняется проверка.
     */
    public void checkFieldEmptyAfterInvalidInput(String field) {
        inputsByLabel.get(field)
                .shouldHave(Condition.value(""));
    }



    /**
     * Проверка, что поле не принимает больше символов, чем положено.
     * Например, при вводе 3х цифр в поле "Месяц", последняя цифры обрезается.
     * @param field          - название поля, для которого выполняется проверка.
     * @param expectedValue - ожидаемое значение после того, как лишнее обрежется.
     */
    public void checkInputLength(String field, String expectedValue) {
        inputsByLabel.get(field)
                .shouldHave(Condition.value(expectedValue));
    }




    /**
     * Заполнение всех полей формы оплаты по карте.
     * @param number - номер карты.
     * @param month - месяц.
     * @param year - год.
     * @param holder - имя владельца.
     * @param cvc - CVC/CVV.
     */
    public void fillCardForm(String number, String month, String year, String holder, String cvc){
        fillOneField("Номер карты", number);
        fillOneField("Месяц", month);
        fillOneField("Год", year);
        fillOneField("Владелец", holder);
        fillOneField("CVC/CVV", cvc);
    }



    /**
     * Успешная операция оплаты по карте APPROVED.
     */
    public void successfulCardOperation(String number, String month, String year, String holder, String cvc) {
        fillCardForm(number, month, year, holder, cvc);
        clickContinueButton();
        sendRequestToBank();
        successNotification.shouldBe(Condition.visible, Duration.ofSeconds(15));
        errorNotification.shouldNotBe(Condition.visible);
    }



    /**
     * Отклоненная операция по карте DECLINED.
     */

    public void unsuccessfulCardOperation(String number, String month, String year, String holder, String cvc) {
        fillCardForm(number, month, year, holder, cvc);
        clickContinueButton();
        sendRequestToBank();
        errorNotification.shouldBe(Condition.visible, Duration.ofSeconds(15));
        successNotification.shouldNotBe(Condition.visible);

    }



    /**
     * Метод-обертка: заполняет одно поле и проверяет под этиим полем сообщение об ошибке.
     * @param field - название поля.
     * @param value - значение, которое нужно внести в поле.
     * @param textError - ожидаемый текст сообщения об ошибке.
     */
    public void fillOneFieldAndSearchError(String field, String value, String textError) {
        fillOneField(field, value);
        clickContinueButton();
        searchError(field, textError);
    }



    /**
     * Метод-обертка: заполняет одно поле невалидными значениями (символы, пробелы, латиница, кириллица) и проверяет, что поле остается пустым.
     * @param field - название поля.
     * @param value - значение, которое нужно внести в поле.
     */
    public void fillFieldAndCheckEmptyAfterInvalidInput(String field, String value) {
     fillOneField(field, value);
     checkFieldEmptyAfterInvalidInput(field);
    }


    /**
     * Метод-обертка: заполняет одно поле и проверяет, что символы свыше допостимого количества обрезаются..
     * @param field - название поля.
     * @param value - значение, которое нужно внести в поле.
     * @param expectedValue - ожидаемое значение после того, как лишнее обрежется..
     */
    public void fillFieldAndCheckInputLength(String field, String value, String expectedValue) {
     fillOneField(field, value);
     checkInputLength(field, expectedValue);
    }
}
