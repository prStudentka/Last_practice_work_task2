package com.mts.creditservice.endpoints;

import com.mts.creditservice.core.Config;
import com.mts.creditservice.core.Specification;
import io.qameta.allure.Description;
import io.qameta.allure.Step;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import io.qameta.allure.restassured.AllureRestAssured;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class StatusOrder {

    private final RequestSpecification reqSpecification;
    private final String orderId;
    private final String paramName = "orderId";
    private ValidatableResponse response;

    public StatusOrder(RequestSpecification reqSpecification, String orderId) throws IOException {
        this.reqSpecification = reqSpecification;
        this.orderId = orderId;
        this.response = checkStatusOrder();
    }

    @Step("Запуск теста запроса GET статуса заявки")
    @Description("Проверка запроса с параметрами и status_code 200 статуса заявки")
    private ValidatableResponse checkStatusOrder() throws IOException {
        return given()
                .log().all()
                .filter(new AllureRestAssured())
                .spec(this.reqSpecification)
                .params(paramName, this.orderId)
                .when()
                //---> Endpoint для выполнения запроса GET
                .request("GET", Config.GET_STATUS_ORDER_URL) //---> Endpoint для выполнения запроса GET
                .then()
                .log().body()
                .log().ifValidationFails()
                .assertThat();

    }

    @Step("Тестирование ответа статус кода 200 статуса заявки")
    @Description("Проверка ответа статус кода 200")
    public void hasStatusCodeOkCheck() throws IOException {
        response
                .spec(Specification.responseSpecification())
                .log().ifStatusCodeIsEqualTo(HttpStatus.SC_OK)
                .log().ifStatusCodeMatches(greaterThan(HttpStatus.SC_OK)); //---> Проверка статус код;
    }

    @Step("Тестирование тело ответа на пустоту статуса заявки")
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
                .body("data", hasKey("orderStatus"));
    }

    @Step("Тестирование ответа вхождения ключа со значениями")
    @Description("Проверка наличия значений у ключа в теле ответа")
    public void hasItemsStatusCheck() throws IOException {
        response
                .body("data.orderStatus", Matchers.is(anyOf(is("IN_PROGRESS"), is("APPROVED"), is("REFUSED"))));
    }

    @Step("Тестирование ответа статус кода 400")
    @Description("Проверка ответа status_code 400 статуса заявки")
    public void hasStatusCodeBadCheck() throws IOException {
        response
                .spec(Specification.responseSpecification(HttpStatus.SC_BAD_REQUEST))
                .log().ifStatusCodeIsEqualTo(HttpStatus.SC_BAD_REQUEST);
    }

    @Step("Тестирование ответа статуса заявки на значение APPROVED")
    @Description("Проверка тела сообщения на значение APPROVED и возвращает boolean")
    public boolean hasStatusApprovedCheck() throws IOException {
        String result = response
                .extract().body().jsonPath().get("data.orderStatus");
        return result.equals("APPROVED");
    }

    @Step("Тестирование ответа статуса заявки на значение REFUSED")
    @Description("Проверка тела сообщения на значение REFUSED и возвращает boolean")
    public boolean hasStatusRefusedCheck() throws IOException {
        String result = response
                .extract().body().jsonPath().get("data.orderStatus");
        return result.equals("REFUSED");
    }

    @Step("Тестирование ответа статуса заявки на значение статуса {status}")
    @Description("Проверка тела сообщения на запрашиваемое значение и возвращает boolean")
    public boolean hasStatusOrderCheck(String status) throws IOException {
        String result = response
                .extract().body().jsonPath().get("data.orderStatus");
        return result.equals(status);
    }

    @Step("Тестирование ответа статуса заявки на значение ошибки {code}")
    @Description("Проверка тела сообщения на запрашиваемое значение ошибки и возвращает boolean")
    public boolean hasBodyErrorCodeCheck(String code) throws IOException {
        String result = response
                .extract().body().jsonPath().get("error.code");
        return result.equals(code);
    }
}
