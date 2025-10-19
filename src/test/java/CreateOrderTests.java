import client.IngredientClient;
import client.OrderClient;
import client.UserClient;
import io.restassured.response.Response;
import model.Order;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import utils.IngredientGenerator;
import utils.UserGenerator;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTests {

    private UserClient userClient = new UserClient();
    private OrderClient orderClient = new OrderClient();
    private IngredientClient ingredientClient = new IngredientClient();
    private User user;
    private String accessToken;

    @BeforeEach
    public void createUser() {
        user = UserGenerator.getRandom();
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.then().extract().path("accessToken");
    }


    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    static Stream<List<String>> ingredientCombinations() {
        return Stream.of(
                IngredientGenerator.getRandomIngredientIds(1),
                IngredientGenerator.getRandomIngredientIds(2),
                IngredientGenerator.getRandomIngredientIds(3)
        );
    }

    @ParameterizedTest
    @MethodSource("ingredientCombinations")
    @DisplayName("Создание заказа с разными комбинациями ингредиентов (авторизованный пользователь)")
    public void createOrderWithVariousIngredientsTest(List<String> ingredientIds) {
        Order order = new Order(ingredientIds);
        Response response = orderClient.createOrder(accessToken, order);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("order.owner", notNullValue());
    }

    @ParameterizedTest
    @MethodSource("ingredientCombinations")
    @DisplayName("Создание заказа с разными комбинациями ингредиентов (неавторизованный пользователь)")
    public void createOrderWithoutAuthTest(List<String> ingredientIds) {
        Order order = new Order(ingredientIds);
        Response response = orderClient.createOrder(null, order);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Проверить создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order emptyOrder = new Order(List.of());
        Response response = orderClient.createOrder(accessToken, emptyOrder);
        response.then().statusCode(400).body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Проверить создание заказа с неверным хешем ингредиента")
    public void createOrderWithInvalidIngredientsTest() {
        Order order = new Order(List.of("invalid_hash_123"));
        Response response = orderClient.createOrder(accessToken, order);
        response.then().statusCode(500);
    }
}
