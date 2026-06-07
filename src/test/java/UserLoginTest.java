import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import user.User;
import user.UserClient;
import user.UserCredentials;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserLoginTest {

    private final UserClient client = new UserClient();

    private User user;

    private String token;

    private UserCredentials creds;

    @BeforeEach
    public void setUp()   {
        user = User.random();
    }


    @DisplayName("Логин под существующим пользователем")
    @Test
    public void loginUserReturn200() {
        client.create(user);

        creds = UserCredentials.fromUser(user);
        Response loginResponse = client.login(creds);
        assertEquals(200, loginResponse.statusCode());
        assertTrue(loginResponse.jsonPath().getBoolean("success"));
        assertEquals(user.getEmail(), loginResponse.jsonPath().getString("user.email"));
        assertEquals(user.getName(), loginResponse.jsonPath().getString("user.name"));
        assertNotNull(loginResponse.jsonPath().getString("accessToken"), "accessToken должен быть заполнен");
        assertNotNull(loginResponse.jsonPath().getString("refreshToken"), "refreshToken должен быть заполнен");

        // удаляем пользователя
        user.setName(null);
        user.setPassword(null);
        token = client.loginAndGetAccessToken(creds);
        System.out.println(token);
        client.delete(token);

    }

    @DisplayName("Логин под пользователем с неверным email")
    @Test
    public void loginNotExistingEmailUserReturn401() {
        client.create(user);

        creds = UserCredentials.fromUser(user);

        creds.setEmail(creds.getEmail() + "123");
        Response loginResponse = client.login(creds);
        assertEquals(401, loginResponse.statusCode());
        assertFalse(loginResponse.jsonPath().getBoolean("success"));
        assertEquals(loginResponse.jsonPath().getString("message"), "email or password are incorrect");

    }

    @DisplayName("Логин под пользователем с неверным password")
    @Test
    public void loginNotExistingPasswordUserReturn401() {
        client.create(user);

        creds = UserCredentials.fromUser(user);

        creds.setPassword(creds.getPassword() + "123");
        Response loginResponse = client.login(creds);
        assertEquals(401, loginResponse.statusCode());
        assertFalse(loginResponse.jsonPath().getBoolean("success"));
        assertEquals(loginResponse.jsonPath().getString("message"), "email or password are incorrect");
    }

    @DisplayName("Логин под пользователем без поля email")
    @Test
    public void logindUserWithoutEmailReturn401() {
        client.create(user);

        creds = UserCredentials.fromUser(user);

        creds.setEmail(null);
        Response loginResponse = client.login(creds);
        assertEquals(401, loginResponse.statusCode());
        assertFalse(loginResponse.jsonPath().getBoolean("success"));
        assertEquals(loginResponse.jsonPath().getString("message"), "email or password are incorrect");
    }

    @DisplayName("Логин под пользователем без поля password")
    @Test
    public void logindUserWithoutPasswordReturn401() {
        client.create(user);

        creds = UserCredentials.fromUser(user);

        creds.setPassword(null);
        Response loginResponse = client.login(creds);
        assertEquals(401, loginResponse.statusCode());
        assertFalse(loginResponse.jsonPath().getBoolean("success"));
        assertEquals(loginResponse.jsonPath().getString("message"), "email or password are incorrect");
    }

}
