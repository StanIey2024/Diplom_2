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
import static org.hamcrest.Matchers.notNullValue;

public class OrdersUserTests {

    private UserClient userClient = new UserClient();
    private OrderClient orderClient = new OrderClient();
    private User user;
    private String accessToken;

    @BeforeEach
    public void createUserAndOrder() {
        user = UserGenerator.getRandom();
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.then().extract().path("accessToken");
        orderClient.createOrder(accessToken, new Order(List.of("61c0c5a71d1f82001bdaaa6d")));
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Проверить получение заказов авторизованным пользователем")
    @Description("Проверка, что авторизованный пользователь может получить список заказов")
    public void getOrdersAuthorizedUserTest() {
        Response response = orderClient.getUserOrders(accessToken);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Проверить получение заказов неавторизованным пользователем")
    public void getOrdersUnauthorizedUserTest() {
        Response response = orderClient.getUserOrders(null);
        response.then().statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
}
