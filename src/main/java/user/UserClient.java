package user;

import config.EnvConfig;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

public class UserClient {
    @Step("Создание пользователя: емайл = {user.email}, имя = {user.name}, пароль = {user.password}")
    public Response create(User user) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(EnvConfig.API_AUTH_REGISTER)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Логин пользователя = {userCredentials.email}")
    public Response login(UserCredentials userCredentials) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(userCredentials)
                .when()
                .post(EnvConfig.API_AUTH_LOGIN)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Логин пользователя с получением токена")
    public String loginAndGetAccessToken(UserCredentials userCredentials) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(userCredentials)
                .when()
                .post(EnvConfig.API_AUTH_LOGIN)
                .then()
                .log().all()
                .extract().response()
                .path("accessToken");
    }

    @Step("Обновление данных пользователя с токеном")
    public Response patch(User user, String accessToken) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(EnvConfig.API_AUTH_USER)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Обновление данных неавторизованного пользователя")
    public Response patch(User user) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .patch(EnvConfig.API_AUTH_USER)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Удаление пользователя")
    public Response delete(String accessToken) {
        return given()
                .log().all()
                .baseUri(EnvConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .delete(EnvConfig.API_AUTH_USER)
                .then()
                .log().all()
                .extract().response();
    }

}
