package order;

import config.EnvConfig;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step("Создание заказа")
    public Response create(Order order) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(EnvConfig.API_ORDERS)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Создание заказа с авторизацией")
    public Response create(Order order, String accessToken) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(EnvConfig.API_ORDERS)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Получение заказов конкретного пользователя с авторизацией")
    public Response get(String accessToken) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .get(EnvConfig.API_ORDERS)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Получение заказов конкретного пользователя без авторизации")
    public Response get() {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .when()
                .get(EnvConfig.API_ORDERS)
                .then()
                .log().all()
                .extract().response();
    }
}
