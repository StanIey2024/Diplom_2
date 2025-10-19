package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientClient {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    @Step("Получить все ингредиенты")
    public Response getIngredients() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(BASE_URL + "/ingredients");
    }

}
