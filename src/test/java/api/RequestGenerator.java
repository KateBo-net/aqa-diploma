package api;

import data.CardInfo;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RequestGenerator {

    public RequestGenerator() {
    }

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(System.getProperty("sut.url"))
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static String postPaymentByCard(CardInfo card) {
        return given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post("/api/v1/pay")
                .then()
                .log().body()
                .statusCode(200)
                .extract().jsonPath().getString("status");
    }

    public static String postPaymentByCredit(CardInfo card) {
        return given()
                .spec(requestSpec)
                .body(card)
                .when()
                .post("/api/v1/credit")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("status");
    }
}
