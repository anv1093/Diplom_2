package ingredients;

import config.EnvConfig;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientsClient {

    @Step("Получение данных об ингредиентах неавторизованным пользователем")
    public Response get() {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .when()
                .get(EnvConfig.API_INGREDIENTS)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Получение данных об ингредиентах с авторизацией")
    public Response get(String accessToken) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .get(EnvConfig.API_INGREDIENTS)
                .then()
                .log().all()
                .extract().response();
    }
}
