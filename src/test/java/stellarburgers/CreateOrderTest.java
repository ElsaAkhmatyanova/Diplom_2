package stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {
    private User user;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    String ingredient = "{\n" + "\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\"]\n" + "}\n";
    String withoutIngredient = "{\"ingredients\": []}";
    String incorrectIngredient = "{\n" + "\"ingredients\": [\"incor5a7100082001incor6d\",\"incor5a71d1f820011000a6f\"]\n" + "}\n";

    @Before
    public void setUp() {
        user = User.getUser();
        userClient = new UserClient();
        orderClient = new OrderClient();
        ValidatableResponse createUserResponse = userClient.createUser(user);
        accessToken = createUserResponse.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(user, accessToken.substring(7));
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентом")
    @Description("Проверка успешного создания заказа с указанием токена и ингредиента. Возвращает непустой ответ и statusCode=200")
    public void createOrderWithAuthTest() {
        ValidatableResponse orderResponse = orderClient.createOrderWithToken(accessToken.substring(7), ingredient).statusCode(200);
        orderResponse.assertThat().body("order.number", notNullValue());
        orderResponse.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка успешного создания заказа с указанием ингредиента, без токена. Возвращает непустой ответ и statusCode=200")
    public void createOrderWithoutAuthTest() {
        ValidatableResponse orderResponse = orderClient.createOrderWithoutToken(ingredient).statusCode(200);
        orderResponse.assertThat().body("order.number", notNullValue());
        orderResponse.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиента")
    @Description("Проверка неуспешного создания заказа без указанием ингредиента. Возвращает ответ 'Ingredient ids must be provided' и statusCode=400")
    public void createOrderWithoutIngredientTest() {
        ValidatableResponse orderResponse = orderClient.createOrderWithToken(accessToken.substring(7), withoutIngredient).statusCode(400);
        orderResponse.assertThat().body("success", equalTo(false));
        orderResponse.assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа c некорректным хэшем ингредиента")
    @Description("Проверка неуспешного создания заказа с указанием некорректного хэша ингредиента. Возвращает ответ statusCode=500")
    public void createOrderWithIncorrectIngredientTest() {
        ValidatableResponse orderIncorrectIngredientResponse = orderClient.createOrderWithToken(accessToken.substring(7), incorrectIngredient).statusCode(500);
    }


}