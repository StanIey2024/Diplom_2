package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    @Step("Создать заказ (token={token})")
    public Response createOrder(String token, Object order) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token == null ? "" : token)
                .body(order)
                .when()
                .post(BASE_URL + "/orders");
    }

    @Step("Получить заказы конкретного пользователя (token={token})")
    public Response getUserOrders(String token) {
        return given()
                .header("Authorization", token == null ? "" : token)
                .when()
                .get(BASE_URL + "/orders");
    }
}
