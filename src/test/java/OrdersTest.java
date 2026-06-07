import ingredients.IngredientsClient;
import io.restassured.response.Response;
import order.Order;
import order.OrderClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.User;
import user.UserClient;
import user.UserCredentials;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrdersTest {

    private final OrderClient clientOrder = new OrderClient();

    private final UserClient clientUser = new UserClient();

    private final IngredientsClient clientIngr = new IngredientsClient();

    private Order order;

    private User user;

    private String token;
    private UserCredentials creds;

    @DisplayName("Создание заказа без авторизации с несколькими ингредиентами")
    @Test
    public void createOrderWithIngredientsReturn200() {
        Response listIngredients = clientIngr.get();
        List<String> idsIngredients = listIngredients.jsonPath().getList("data._id");

        // выбираем 5 случайных элементов
        Random random = new Random();
        List<String> randomIngredients = IntStream.range(0, 5)
                .mapToObj(i -> idsIngredients.get(random.nextInt(idsIngredients.size())))
                .collect(Collectors.toList());

        order = new Order(randomIngredients);
        Response createResponse = clientOrder.create(order);

        assertEquals(200, createResponse.statusCode());
        assertTrue(createResponse.jsonPath().getBoolean("success"));
        assertNotNull(createResponse.jsonPath().getString("order.number"), "Номер заказа должен быть заполнен");

        // логика формирования названия отсутствует в документации, поэтому проверим только что заполнено
        assertNotNull(createResponse.jsonPath().getString("name"), "Название должно быть заполнено");
        assertFalse(createResponse.jsonPath().getString("name").isEmpty(), "Номер заказа не должен быть пустым");

    }

    @DisplayName("Создание заказа без авторизации без ингредиентов")
    @Test
    public void createOrderWithoutIngredientsReturn400() {
        order = new Order();
        Response createResponse = clientOrder.create(order);

        assertEquals(400, createResponse.statusCode());
        assertFalse(createResponse.jsonPath().getBoolean("success"));
        assertEquals(createResponse.jsonPath().getString("message"), "Ingredient ids must be provided");

    }

    @DisplayName("Создание заказа без авторизации с неверным хэшем")
    @Test
    public void createOrderIncorrectHashReturn500() {
        List<String> idsIngredients = List.of("0101");
        order = new Order();
        order.setIngredient(idsIngredients);
        Response createResponse = clientOrder.create(order);

        assertEquals(500, createResponse.statusCode());
    }

    @DisplayName("Создание заказа с авторизацией с несколькими ингредиентами")
    @Test
    public void createOrderWithIngredientsAuthReturn200() {
        // логин
        user = User.random();
        clientUser.create(user);
        creds = UserCredentials.fromUser(user);
        token = clientUser.loginAndGetAccessToken(creds);

        // получаем список ингридиентов авторизованным пользователем
        Response listIngredients = clientIngr.get(token);
        List<String> idsIngredients = listIngredients.jsonPath().getList("data._id");

        // выбираем 5 случайных элементов
        Random random = new Random();
        List<String> randomIngredients = IntStream.range(0, 5)
                .mapToObj(i -> idsIngredients.get(random.nextInt(idsIngredients.size())))
                .collect(Collectors.toList());

        order = new Order(randomIngredients);
        Response createResponse = clientOrder.create(order, token);

        assertEquals(200, createResponse.statusCode());
        assertTrue(createResponse.jsonPath().getBoolean("success"));
        assertNotNull(createResponse.jsonPath().getString("name"), "Название должно быть заполнено");

        assertEquals(5, createResponse.jsonPath().getList("order.ingredients").size(), "Список ингредиентов должен содержать 5 элементов");
        assertNotNull(createResponse.jsonPath().getString("order._id"), "_id должно быть заполнено");
        assertEquals(user.getName(), createResponse.jsonPath().getString("order.owner.name"));
        assertEquals(user.getEmail(), createResponse.jsonPath().getString("order.owner.email"));
        assertNotNull(createResponse.jsonPath().getString("order.owner.createdAt"), "order.owner.createdAt должно быть заполнено");
        assertNotNull(createResponse.jsonPath().getString("order.owner.updatedAt"), "order.owner.updatedAt должно быть заполнено");

        assertEquals(createResponse.jsonPath().getString("order.status"), "done");
        assertNotNull(createResponse.jsonPath().getString("order.name"), "Название должно быть заполнено");

        assertNotNull(createResponse.jsonPath().getString("order.createdAt"), "order.createdAt должно быть заполнено");
        assertNotNull(createResponse.jsonPath().getString("order.updatedAt"), "order.updatedAt должно быть заполнено");
        assertNotNull(createResponse.jsonPath().getString("order.number"), "order.number должно быть заполнено");
        assertNotNull(createResponse.jsonPath().getString("order.price"), "order.price должно быть заполнено");
    }

    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    @Test
    public void createOrderWithoutIngredientsAuthReturn400() {
        // логин
        user = User.random();
        clientUser.create(user);
        creds = UserCredentials.fromUser(user);
        token = clientUser.loginAndGetAccessToken(creds);

        order = new Order();
        Response createResponse = clientOrder.create(order, token);

        assertEquals(400, createResponse.statusCode());
        assertFalse(createResponse.jsonPath().getBoolean("success"));
        assertEquals(createResponse.jsonPath().getString("message"), "Ingredient ids must be provided");
    }

    @DisplayName("Создание заказа с авторизацией с неверным хэшем")
    @Test
    public void createOrderIncorrectHashAuthReturn500() {
        // логин
        user = User.random();
        clientUser.create(user);
        creds = UserCredentials.fromUser(user);
        token = clientUser.loginAndGetAccessToken(creds);

        List<String> idsIngredients = List.of("0101");
        order = new Order();
        order.setIngredient(idsIngredients);
        Response createResponse = clientOrder.create(order, token);

        assertEquals(500, createResponse.statusCode());
    }

    @AfterEach
    public void deleteUser() {
        if (token != null) {
            user.setName(null);
            user.setPassword(null);
            clientUser.delete(token);
        }
    }

}
