import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.User;
import user.UserClient;
import user.UserCredentials;

import static org.junit.jupiter.api.Assertions.*;

public class UserCreationTest {

    private final UserClient client = new UserClient();

    private User user;

    private String token;

    private UserCredentials creds;

    @BeforeEach
    public void setUp()   {
        user = User.random();
    }

    @DisplayName("Создание уникального пользователя")
    @Test
    public void createUserReturn200() {
        Response createResponse = client.create(user);
        assertEquals(200, createResponse.statusCode());
        assertTrue(createResponse.jsonPath().getBoolean("success"));
        assertEquals(user.getEmail(), createResponse.jsonPath().getString("user.email"));
        assertEquals(user.getName(), createResponse.jsonPath().getString("user.name"));
        assertNotNull(createResponse.jsonPath().getString("accessToken"), "accessToken должен быть заполнен");
        assertNotNull(createResponse.jsonPath().getString("refreshToken"), "refreshToken должен быть заполнен");

        // сохраняем токен для удаления
        creds = UserCredentials.fromUser(user);
        token = client.loginAndGetAccessToken(creds);
    }

    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Test
    public void createNotUniqueUserReturn403() {
        // Создание уникального пользователя
        Response createResponse = client.create(user);
        assertEquals(200, createResponse.statusCode());

        // Попытка повторного создания пользователя
        Response createResponse2 = client.create(user);
        assertEquals(403, createResponse2.statusCode());

        assertFalse(createResponse2.jsonPath().getBoolean("success"));
        assertEquals(createResponse2.jsonPath().getString("message"), "User already exists");

        // сохраняем токен для удаления
        creds = UserCredentials.fromUser(user);
        token = client.loginAndGetAccessToken(creds);

    }

    @DisplayName("Создание пользователя, у которого не заполнено поле email")
    @Test
    public void createUserWithoutEmailReturn403() {
        user.setEmail(null);
        Response createResponse = client.create(user);
        assertEquals(403, createResponse.statusCode());
        assertFalse(createResponse.jsonPath().getBoolean("success"));
        assertEquals(createResponse.jsonPath().getString("message"), "Email, password and name are required fields");
    }

    @DisplayName("Создание пользователя, у которого не заполнено поле password")
    @Test
    public void createUserWithoutPasswordReturn403() {
        user.setPassword(null);
        Response createResponse = client.create(user);
        assertEquals(403, createResponse.statusCode());
        assertFalse(createResponse.jsonPath().getBoolean("success"));
        assertEquals(createResponse.jsonPath().getString("message"), "Email, password and name are required fields");
    }

    @DisplayName("Создание пользователя, у которого не заполнено поле name")
    @Test
    public void createUserWithoutNameReturn403() {
        user.setName(null);
        Response createResponse = client.create(user);
        assertEquals(403, createResponse.statusCode());
        assertFalse(createResponse.jsonPath().getBoolean("success"));
        assertEquals(createResponse.jsonPath().getString("message"), "Email, password and name are required fields");

    }

    @AfterEach
    public void deleteUser() {
        if (token != null) {
            user.setName(null);
            user.setPassword(null);
            client.delete(token);
        }
    }
}
