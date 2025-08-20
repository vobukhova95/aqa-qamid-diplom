package data.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiHelper {
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(8080)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static Response sendRequest(ApiEndpoints endpoint, Method httpMethod) {
        return given()
                .spec(requestSpec)
                .when()
                .request(httpMethod, endpoint.getEndpoint())
                .then()
                .log().all()
                .extract().response();
    }

    public static Response sendRequest(ApiEndpoints endpoint, Object body, Method httpMethod) {
        return given()
                .spec(requestSpec)
                .body(body)
                .when()
                .request(httpMethod, endpoint.getEndpoint())
                .then()
                .log().all()
                .extract().response();
    }

    public static void assertResponse(Response response, ApiStatus status) {
        response.then()
                .statusCode(status.getCode());

        if (status == ApiStatus.APPROVED || status == ApiStatus.DECLINED) {
            response.then()
                    .body("status", equalTo(status.getMessage()));
        } else {
            response.then()
                    .body("status", equalTo(status.getCode()))
                    .body("error", equalTo(status.getMessage()));
        }
    }

    public static void assertResponseHeaders(Response response){
        response.then()
                .header("Content-type", equalTo("application/json"))
                .header("Date", notNullValue())
                .header("Transfer-encoding", notNullValue())
                .header("Vary", notNullValue());
    }

}
