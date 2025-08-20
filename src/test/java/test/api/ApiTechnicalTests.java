package test.api;

import data.api.ApiDataHelper;
import data.api.ApiEndpoints;
import data.api.ApiHelper;
import data.api.ApiStatus;
import data.ui.DataHelper;
import io.qameta.allure.*;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ApiTechnicalTests {

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment API")
    @Story("Request Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("API")
    @DisplayName("Should return 404 when sending request to wrong endpoint")
    void shouldReturn404ForWrongEndpoint() {
        Response response = ApiHelper.sendRequest(ApiEndpoints.WRONG, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.NOT_FOUND);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment API")
    @Story("Request Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("API")
    @DisplayName("Should return 405 when using GET instead of POST")
    void shouldReturn405WhenUsingGet() {
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, Method.GET);
        ApiHelper.assertResponse(response, ApiStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment API")
    @Story("Request Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("API")
    @DisplayName("Should return 405 when using PUT instead of POST")
    void shouldReturn405WhenUsingPut() {
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, Method.PUT);
        ApiHelper.assertResponse(response, ApiStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment API")
    @Story("Request Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.NORMAL)
    @Tag("API")
    @DisplayName("Should return 405 when using DELETE instead of POST")
    void shouldReturn405WhenUsingDelete() {
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, Method.DELETE);
        ApiHelper.assertResponse(response, ApiStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment API")
    @Story("Request Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("API")
    @DisplayName("Should return Bad Request when sending empty body {}")
    void shouldReturnBadRequestForEmptyBody() {
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, "{}", Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment API")
    @Story("Request Validation")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("API")
    @DisplayName("Should return Bad Request when sending null body")
    void shouldReturnBadRequestForNullBody() {
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment API")
    @Story("Security - SQL Injection")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("API")
    @Tag("Security")
    @DisplayName("Should reject card holder name with SQL injection attempt")
    void shouldRejectSqlInjectionInHolder() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = ApiDataHelper.getSQLInjection();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);
    }

    @Test
    @Epic("Payment Processing")
    @Feature("Card Payment API")
    @Story("Security - XSS Injection")
    @Owner("Veronika Obukhova")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("API")
    @Tag("Security")
    @DisplayName("Should reject card holder name with XSS injection attempt")
    void shouldRejectSXssInjectionInHolder() {
        String cardNumber = DataHelper.CardNumber.approvedCardNumber();
        String month = DataHelper.Month.validMonth();
        String year = DataHelper.CardYear.generateYearOffset(2);
        String holder = ApiDataHelper.getXSSInjection();
        String cvc = DataHelper.CommonValues.generateDigits(3);

        ApiDataHelper.CardInfo card = ApiDataHelper.getCard(cardNumber, month, year, holder, cvc);
        Response response = ApiHelper.sendRequest(ApiEndpoints.PAY, card, Method.POST);
        ApiHelper.assertResponse(response, ApiStatus.BAD_REQUEST);
    }
}
