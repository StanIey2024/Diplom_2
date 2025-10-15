package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    @Step("Создать пользователя")
    public Response createUser(Object user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + "/auth/register");
    }

    @Step("Авторизовать пользователя")
    public Response loginUser(Object credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post(BASE_URL + "/auth/login");
    }

    @Step("Изменить данные пользователя с токеном: {token}")
    public Response updateUser(String token, Object user) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(BASE_URL + "/auth/user");
    }

    @Step("Удалить пользователя")
    public void deleteUser(String token) {
        given()
                .header("Authorization", token)
                .when()
                .delete(BASE_URL + "/auth/user")
                .then()
                .statusCode(202);
    }
}
