import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.Order;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.UserGenerator;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTests {

    private UserClient userClient = new UserClient();
    private OrderClient orderClient = new OrderClient();
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

    @Test
    @DisplayName("Проверить создание заказа с авторизацией и ингредиентами")
    @Description("Проверка успешного создания заказа для авторизованного пользователя")
    public void createOrderWithAuthAndIngredientsTest() {
        Order order = new Order(List.of("61c0c5a71d1f82001bdaaa6d")); // пример валидного ингредиента
        Response response = orderClient.createOrder(accessToken, order);
        response.then().statusCode(200).body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверить создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        Order order = new Order(List.of("61c0c5a71d1f82001bdaaa6d"));
        Response response = orderClient.createOrder(null, order);
        response.then().statusCode(200).body("success", equalTo(true)); // разрешено без токена
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
