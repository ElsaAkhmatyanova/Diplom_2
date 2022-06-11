package stellarburgers;


import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserInfoTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = User.getUser();
        userClient = new UserClient();
        ValidatableResponse responseCreate = userClient.createUser(user);
        accessToken = responseCreate.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(user, accessToken.substring(7));
        }
    }

    @Test
    @DisplayName("Изменение всех данных пользователя с авторизацией")
    @Description("Успешное изменение всех данных пользователя при указании токена. Проверка statusCode=200, email и name в ответе на запрос, пароля при повторной авторизации")
    public void updateAllUserDataWithAuthTest() {
        user.setEmail("test" + user.getEmail());
        user.setPassword("test" + user.getPassword());
        user.setName("test" + user.getName());

        ValidatableResponse updAllResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(200);
        updAllResponse.assertThat().body("success", equalTo(true));
        updAllResponse.assertThat().body("user." + "email", equalTo(user.getEmail().toLowerCase(Locale.ROOT)));
        updAllResponse.assertThat().body("user." + "name", equalTo(user.getName()));
        userClient.loginUser(user).statusCode(200);
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    @Description("Успешное изменение email пользователя при указании токена. Проверка statusCode=200, email в ответе на запрос")
    public void updateEmailWithAuthTest() {
        user.setEmail("test" + user.getEmail());

        ValidatableResponse updEmailResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(200);
        updEmailResponse.assertThat().body("success", equalTo(true));
        updEmailResponse.assertThat().body("user." + "email", equalTo(user.getEmail().toLowerCase(Locale.ROOT)));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией")
    @Description("Успешное изменение password пользователя при указании токена. Проверка statusCode=200, пароля при повторной авторизации")
    public void updatePasswordWithAuthTest() {
        user.setPassword("test" + user.getPassword());

        ValidatableResponse updPasswordResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(200);
        updPasswordResponse.assertThat().body("success", equalTo(true));
        userClient.loginUser(user).statusCode(200);
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    @Description("Успешное изменение name пользователя при указании токена. Проверка statusCode=200, name в ответе на запрос")
    public void updateNameWithAuthTest() {
        user.setName("test" + user.getName());

        ValidatableResponse updEmailResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(200);
        updEmailResponse.assertThat().body("success", equalTo(true));
        updEmailResponse.assertThat().body("user." + "name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Изменение email пользователя на email другого пользователя")
    @Description("Неуспешное изменение email пользователя при указании уже существующего email, принадлежащего другому пользователю. Проверка statusCode=403, message")
    public void updateExistEmailTest() {
        User userExistingEmail = User.getUser();
        userClient.createUser(userExistingEmail).statusCode(200);
        user.setEmail(userExistingEmail.getEmail());

        ValidatableResponse updEmailResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(403);
        updEmailResponse.assertThat().body("success", equalTo(false));
        updEmailResponse.assertThat().body("message", equalTo("User with such email already exists"));
    }

    @Test
    @DisplayName("Изменение всех данных пользователя без авторизации")
    @Description("Неуспешное изменение всех данных пользователя без указании токена. Проверка statusCode=401, message")
    public void updateAllUserDataWithoutAuthTest() {
        user.setEmail("test" + user.getEmail());
        user.setPassword("test" + user.getPassword());
        user.setName("test" + user.getName());


        ValidatableResponse updAllWithoutAuthResponse = userClient.changeUserDataWithoutAuth(user).statusCode(401);
        updAllWithoutAuthResponse.assertThat().body("success", equalTo(false));
        updAllWithoutAuthResponse.assertThat().body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение email пользователя без авторизации")
    @Description("Неуспешное изменение email пользователя без указании токена. Проверка statusCode=401, message")
    public void updateEmailWithoutAuthTest() {
        user.setEmail("test" + user.getEmail());

        ValidatableResponse updEmailWithoutAuthResponse = userClient.changeUserDataWithoutAuth(user).statusCode(401);
        updEmailWithoutAuthResponse.assertThat().body("success", equalTo(false));
        updEmailWithoutAuthResponse.assertThat().body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение пароля пользователя без авторизации")
    @Description("Неуспешное изменение password пользователя без указании токена. Проверка statusCode=401, message")
    public void updatePasswordWithoutAuthTest() {
        user.setPassword("test" + user.getPassword());

        ValidatableResponse updPasswordWithoutAuthResponse = userClient.changeUserDataWithoutAuth(user).statusCode(401);
        updPasswordWithoutAuthResponse.assertThat().body("success", equalTo(false));
        updPasswordWithoutAuthResponse.assertThat().body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    @Description("Неуспешное изменение name пользователя без указании токена. Проверка statusCode=401, message")
    public void updateNameWithoutAuthTest() {
        user.setName("test" + user.getName());

        ValidatableResponse updNameWithoutAuthResponse = userClient.changeUserDataWithoutAuth(user).statusCode(401);
        updNameWithoutAuthResponse.assertThat().body("success", equalTo(false));
        updNameWithoutAuthResponse.assertThat().body("message", equalTo("You should be authorised"));
    }

}
