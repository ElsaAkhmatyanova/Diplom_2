package stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends StellarBurgerRestClient {

    @Step("Создание заказа с отправкой токена")
    public ValidatableResponse createOrderWithToken(String token, String ingredient) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token)
                .body(ingredient)
                .when()
                .post(StellarBurgerEndPoints.ORDER_PATH)
                .then();
    }

    @Step("Создание заказа без отправки токена")
    public ValidatableResponse createOrderWithoutToken(String ingredient) {
        return given()
                .spec(getBaseSpec())
                .body(ingredient)
                .when()
                .post(StellarBurgerEndPoints.ORDER_PATH)
                .then();
    }

    @Step("Получение заказов конкретного пользователя с отправкой токена")
    public ValidatableResponse getOrdersWithToken(String token) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token)
                .when()
                .get(StellarBurgerEndPoints.ORDER_PATH)
                .then();
    }

    @Step("Получение заказов конкретного пользователя без отправки токена")
    public ValidatableResponse getOrdersWithoutToken() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(StellarBurgerEndPoints.ORDER_PATH)
                .then();
    }
}