import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.User;
import user.UserClient;
import user.UserCredentials;

import static org.junit.jupiter.api.Assertions.*;

public class UserPatchTest {

    private final UserClient client = new UserClient();

    private User user;

    private String token;

    private UserCredentials creds;

    @BeforeEach
    public void setUp()   {
        user = User.random();
    }


    @DisplayName("Изменение данных авторизованного пользователя: обновление email")
    @Test
    public void updateUserWithEmailReturn200() {
        client.create(user);

        creds = UserCredentials.fromUser(user);
        token = client.loginAndGetAccessToken(creds);

        user.setPassword(null);
        user.setEmail("new-" + user.getEmail());
        Response patchResponse = client.patch(user, token);
        assertEquals(200, patchResponse.statusCode());
        assertTrue(patchResponse.jsonPath().getBoolean("success"));
        assertEquals(user.getEmail(), patchResponse.jsonPath().getString("user.email"));
        assertEquals(user.getName(), patchResponse.jsonPath().getString("user.name"));
    }

    @DisplayName("Изменение данных авторизованного пользователя: обновление name")
    @Test
    public void updateUserWithNameReturn200() {
        client.create(user);

        creds = UserCredentials.fromUser(user);
        token = client.loginAndGetAccessToken(creds);

        user.setPassword(null);
        user.setName(user.getName() + "-new");
        Response patchResponse = client.patch(user, token);
        assertEquals(200, patchResponse.statusCode());
        assertTrue(patchResponse.jsonPath().getBoolean("success"));
        assertEquals(user.getEmail(), patchResponse.jsonPath().getString("user.email"));
        assertEquals(user.getName(), patchResponse.jsonPath().getString("user.name"));
    }

    @DisplayName("Изменение данных авторизованного пользователя: обновление email и name")
    @Test
    public void updateUserWithEmailAndNameReturn200() {
        client.create(user);

        creds = UserCredentials.fromUser(user);
        token = client.loginAndGetAccessToken(creds);

        user.setPassword(null);
        user.setEmail("new-" + user.getEmail());
        user.setName(user.getName() + "-new");
        Response patchResponse = client.patch(user, token);
        assertEquals(200, patchResponse.statusCode());
        assertTrue(patchResponse.jsonPath().getBoolean("success"));
        assertEquals(user.getEmail(), patchResponse.jsonPath().getString("user.email"));
        assertEquals(user.getName(), patchResponse.jsonPath().getString("user.name"));
    }

    @DisplayName("Изменение данных авторизованного пользователя: обновление email на существующий")
    @Test
    public void updateUserWithExistingEmailReturn403() {
        User user2 = User.random();

        client.create(user);
        client.create(user2);

        creds = UserCredentials.fromUser(user);
        token = client.loginAndGetAccessToken(creds);

        user.setPassword(null);
        user.setName(null);
        user.setEmail(user2.getEmail());
        Response patchResponse = client.patch(user, token);
        assertEquals(403, patchResponse.statusCode());
        assertFalse(patchResponse.jsonPath().getBoolean("success"));
        assertEquals(patchResponse.jsonPath().getString("message"), "User with such email already exists");
    }

    @DisplayName("Изменение данных неавторизованного пользователя: ошибка 401")
    @Test
    public void updateUnauthorizedUserReturn200() {
        client.create(user);

        user.setPassword(null);

        Response patchResponse = client.patch(user);
        assertEquals(401, patchResponse.statusCode());
        assertFalse(patchResponse.jsonPath().getBoolean("success"));
        assertEquals(patchResponse.jsonPath().getString("message"), "You should be authorised");
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
