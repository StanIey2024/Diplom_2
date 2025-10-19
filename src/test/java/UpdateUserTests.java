import client.UserClient;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.UserGenerator;

import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTests {

    private UserClient userClient = new UserClient();
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
    @DisplayName("Проверить изменение данных пользователя с авторизацией")
    @Description("Проверка успешного обновления имени пользователя при наличии токена")
    public void updateUserWithAuthTest() {
        User updatedUser = new User(user.getEmail(), user.getPassword(), "NewName");
        Response response = userClient.updateUser(accessToken, updatedUser);
        response.then().statusCode(200).body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверить изменение данных без авторизации")
    @Description("Проверка, что без токена обновление невозможно")
    public void updateUserWithoutAuthTest() {
        User updatedUser = new User(user.getEmail(), user.getPassword(), "NoAuthName");
        Response response = userClient.updateUser("", updatedUser);
        response.then().statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
}
