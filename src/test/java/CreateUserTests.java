import client.UserClient;
import io.restassured.response.Response;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.UserGenerator;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTests {

    private final UserClient userClient = new UserClient();
    private User user;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        user = UserGenerator.getRandom();
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Проверить создание уникального пользователя")
    public void createUniqueUserTest() {
        Response response = userClient.createUser(user);
        response.then().statusCode(200).body("success", equalTo(true));
        accessToken = response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверить создание пользователя, который уже зарегистрирован")
    public void createDuplicateUserTest() {
        userClient.createUser(user);
        Response duplicate = userClient.createUser(user);
        duplicate.then().statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Проверить создание пользователя без обязательного поля (email)")
    public void createUserWithoutEmailTest() {
        User noEmail = new User(null, user.getPassword(), user.getName());
        Response response = userClient.createUser(noEmail);
        response.then().statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверить создание пользователя без обязательного поля (password)")
    public void createUserWithoutPasswordTest() {
        User noPassword = new User(user.getEmail(), null, user.getName());
        Response response = userClient.createUser(noPassword);
        response.then().statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверить создание пользователя без обязательного поля (name)")
    public void createUserWithoutNameTest() {
        User noName = new User(user.getEmail(), user.getPassword(), null);
        Response response = userClient.createUser(noName);
        response.then().statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
