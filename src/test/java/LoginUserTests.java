import client.UserClient;
import io.restassured.response.Response;
import model.AuthorizationUser;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.UserGenerator;

import static org.hamcrest.Matchers.equalTo;

public class LoginUserTests {

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
    @DisplayName("Логин под существующим пользователем")
    public void loginExistingUserTest() {
        Response response = userClient.loginUser(new AuthorizationUser(user.getEmail(), user.getPassword()));
        response.then().statusCode(200).body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин с неверными данными")
    public void loginWithWrongCredentialsTest() {
        Response response = userClient.loginUser(new AuthorizationUser("wrong@email.com", "wrongPass"));
        response.then().statusCode(401).body("message", equalTo("email or password are incorrect"));
    }
}
