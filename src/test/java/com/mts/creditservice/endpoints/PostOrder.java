package com.mts.creditservice.endpoints;

import com.mts.creditservice.core.Config;
import com.mts.creditservice.core.Specification;

import com.mts.creditservice.core.UserOrdersData;
import com.mts.creditservice.model.PostOrderSchema;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import org.hamcrest.Matchers;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PostOrder {
    private final ValidatableResponse response;

    public PostOrder(ValidatableResponse response) {
        this.response = response;
    }

    @Step("Запуск теста POST заявки на кредит ({userId}, {tariffId})")
    @Description("Проверка запроса POST заявки, схемы и status_code 200")
    public static ValidatableResponse checkOrder(RequestSpecification reqSpecification, long userId, long tariffId) throws IOException {
        return given()
                .log().all()
                .filter(new AllureRestAssured())
                .spec(reqSpecification)
                .when()
                .body(new PostOrderSchema(userId, tariffId))
                //---> Endpoint для выполнения запроса POST
                .request("POST", Config.POST_ORDER_URL)//---> Endpoint для выполнения запроса POST
                .then()
                .log().body()
                .log().ifValidationFails()
                .assertThat()
                .log().ifStatusCodeMatches(greaterThan(HttpStatus.SC_OK)); //---> Проверка статус код
    }

    @Step("Тестирование ответа статус кода 200")
    @Description("Проверка ответа status_code 200 заявки")
    public void hasStatusCodeOkCheck() throws IOException {
        response
                .spec(Specification.responseSpecification())
                .statusCode(HttpStatus.SC_OK);
    }

    @Step("Тестирование тело ответа на пустоту")
    @Description("Проверка пустое ли тело ответа")
    public void hasNotEmptyCheck() throws IOException {
        response
                .body("$", not(empty()));
    }

    @Step("Тестирование тело ответа на ключи")
    @Description("Проверка наличия ключей в теле ответа")
    public void hasKeyCheck() throws IOException {
        response
                .body("$", hasKey("data"))
                .body("data", hasKey("orderId"));
    }

    @Step("Тестирование ответа вхождения ключа со значением")
    @Description("Проверка наличия значения у ключа в теле ответа")
    public void hasEntryCheck() throws IOException {
        response
                .body("data.orderId", Matchers.anything());
    }

    @Step("Тестирование ответа статус кода 400")
    @Description("Проверка негативного ответа status_code 400 заявки")
    public void hasStatusCodeBadCheck() throws IOException {
        response
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Step("Тестирование ответа на сообщение об ошибке")
    @Description("Проверка тела сообщения на значение {code}")
    public boolean hasBodyErrorCodeCheck(String code) throws IOException {
        String result = response
                .extract().body().jsonPath().get("error.code");
        return result.equals(code);
    }

    @Step("Сохранение из тела ответа orderId")
    @Description("Проверка тела сообщения на status_code 200, на наличие тела для извлечения и сохранения значения id {userId}")
    public UserOrdersData saveOrderId(long userId) throws IOException {
        JsonPath result = response
                .statusCode(is(HttpStatus.SC_OK))
                .body(Matchers.anything())
                .extract().body().jsonPath();
        return new UserOrdersData(userId, result.get("data.orderId"));
    }
}
