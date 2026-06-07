import ingredients.IngredientsClient;
import io.restassured.response.Response;
import order.Order;
import order.OrderClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.User;
import user.UserClient;
import user.UserCredentials;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrdersGetTest {

    private final OrderClient clientOrder = new OrderClient();

    private final UserClient clientUser = new UserClient();


    @DisplayName("Получение заказов конкретного пользователя c авторизацией")
    @Test
    public void getOrdersAuthReturn200() {
        // логин
        User user = User.random();
        clientUser.create(user);
        var creds = UserCredentials.fromUser(user);
        String token = clientUser.loginAndGetAccessToken(creds);

        // получаем список ингридиентов авторизованным пользователем
        Response listIngredients = clientOrder.get(token);

        assertEquals(200, listIngredients.statusCode());
        assertTrue(listIngredients.jsonPath().getBoolean("success"));
        assertNotNull(listIngredients.jsonPath().getString("orders"));
        assertTrue(listIngredients.jsonPath().get("total") instanceof Integer);
        assertTrue(listIngredients.jsonPath().get("totalToday") instanceof Integer);


        // удаляем пользователя
        user.setName(null);
        user.setPassword(null);
        token = clientUser.loginAndGetAccessToken(creds);
        System.out.println(token);
        clientUser.delete(token);
    }

    @DisplayName("Получение заказов конкретного пользователя без авторизации")
    @Test
    public void getOrdersReturn200() {

        Response listIngredients = clientOrder.get();

        assertEquals(401, listIngredients.statusCode());

        assertFalse(listIngredients.jsonPath().getBoolean("success"));
        assertEquals(listIngredients.jsonPath().getString("message"), "You should be authorised");
    }
}
