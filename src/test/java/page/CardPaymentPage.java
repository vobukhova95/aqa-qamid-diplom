package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


public class CardPaymentPage {


    private final ElementsCollection headers = $$("h3");

    private final SelenideElement numberCard = $("[placeholder ='0000 0000 0000 0000']");
    private final SelenideElement month = $("[placeholder = '08']");
    private final SelenideElement year = $("[placeholder = '22']");
    private final SelenideElement holder = $$(".input__inner")
            .findBy(Condition.text("Владелец"))
            .$("input");
    private final SelenideElement cvc = $("[placeholder = '999']");


    private final SelenideElement continueButton = $$("button")
            .findBy(Condition.exactText("Продолжить"));

    private final SelenideElement requestBank = $$("button")
            .findBy(Condition.exactText("Отправляем запрос в Банк..."));

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


    public CardPaymentPage() {
        headers.findBy(Condition.exactText("Оплата по карте"))
                .shouldBe(Condition.visible);
    }

    /**
     * Вспомогательный метод, возвращает элемент поля ввода по его названию.
     *
     * Метод используется для получения соответствующего элемента формы (input)
     * на основании значения перечисления.
     * @param field - поле, для которого требуется получить элемент
     */
    private SelenideElement getFieldElement(FieldName field) {
        switch (field) {
            case NUMBER_CARD:
                return numberCard;
            case MONTH:
                return month;
            case YEAR:
                return year;
            case HOLDER:
                return holder;
            case CVC:
                return cvc;
            default:
                throw new IllegalArgumentException("Неизвестное поле: " + field);
        }
    }

    /**
     * Заполнение одного поля.
     *
     * @param field - название поля, которое нужно заполнить.
     * @param value - значение, которое нужно внести в поле.
     */
    public void fillOneField(FieldName field, String value) {
        getFieldElement(field).setValue(value);
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
     *
     * @param field     - название поля, под которым должно быть сообщение об ошибке.
     * @param textError - ожидаемый текст ошибки.
     */
    public void searchError(FieldName field, String textError) {
        getFieldElement(field).closest(".input__inner")
                .$(".input__sub")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(textError));
    }


    /**
     * Проверка, что поле не принимает символы, пробелы, латиницу и кириллицу
     *
     * @param field - название поля, для которого выполняется проверка.
     */
    public void checkFieldEmptyAfterInvalidInput(FieldName field) {
       getFieldElement(field).shouldHave(Condition.value(""));
    }


    /**
     * Проверка, что поле не принимает больше символов, чем положено.
     * Например, при вводе 3х цифр в поле "Месяц", последняя цифры обрезается.
     *
     * @param field         - название поля, для которого выполняется проверка.
     * @param expectedValue - ожидаемое значение после того, как лишнее обрежется.
     */
    public void checkInputLength(FieldName field, String expectedValue) {
        getFieldElement(field).shouldHave(Condition.value(expectedValue));
    }


    /**
     * Заполнение всех полей формы оплаты по карте.
     *
     * @param number - номер карты.
     * @param month  - месяц.
     * @param year   - год.
     * @param holder - имя владельца.
     * @param cvc    - CVC/CVV.
     */
    public void fillCardForm(String number, String month, String year, String holder, String cvc) {
        fillOneField(FieldName.NUMBER_CARD, number);
        fillOneField(FieldName.MONTH, month);
        fillOneField(FieldName.YEAR, year);
        fillOneField(FieldName.HOLDER, holder);
        fillOneField(FieldName.CVC, cvc);
    }


    /**
     * Успешная операция оплаты по карте APPROVED.
     */
    public void successfulCardOperation() {
        successNotification.shouldBe(Condition.visible, Duration.ofSeconds(15));
        errorNotification.shouldNotBe(Condition.visible);
    }


    /**
     * Отклоненная операция по карте DECLINED.
     */
    public void unsuccessfulCardOperation() {
        errorNotification.shouldBe(Condition.visible, Duration.ofSeconds(15));
        successNotification.shouldNotBe(Condition.visible);

    }
}