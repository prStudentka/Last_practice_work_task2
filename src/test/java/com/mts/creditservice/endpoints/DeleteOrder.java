package com.mts.creditservice.endpoints;

import com.mts.creditservice.core.Config;

import com.mts.creditservice.model.DeleteOrderSchema;

import io.qameta.allure.Description;
import io.qameta.allure.Step;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;

import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class DeleteOrder {
    private final Response response;

    public DeleteOrder(Response response) {
        this.response = response;
    }

    @Step("Запуск теста проверки Delete заявки на кредит")
    @Description("Проверка Delete заявки, схемы и status_code запроса 200")
    public static Response checkDeleteOrder(RequestSpecification reqSpecification, long userId, String orderId) throws IOException {
        return given()
                .log().all()
                .filter(new AllureRestAssured())
                .spec(reqSpecification)
                .when()
                .body(new DeleteOrderSchema(userId, orderId))
                //---> Endpoint для выполнения запроса POST
                .request("DELETE", Config.DELETE_ORDER_URL)//---> Endpoint для выполнения запроса DELETE
                .andReturn();
    }

    @Step("Тестирование ответа статус кода 200")
    @Description("Проверка ответа status_code 200 Delete заявки")
    public void hasStatusCodeOkCheck() throws IOException {
        response
                .then()
                .log().body()
                .assertThat()
                .log().ifStatusCodeIsEqualTo(HttpStatus.SC_OK)
                .log().ifStatusCodeMatches(greaterThan(HttpStatus.SC_OK)); //---> Проверка статус код;
    }

    @Step("Тестирование ответа статус кода 400")
    @Description("Проверка ответа status_code 400 Delete заявки")
    public void hasStatusCodeBadCheck() throws IOException {
        response
                .then()
                .log().body()
                .assertThat()
                .log().ifValidationFails()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .log().ifStatusCodeMatches(greaterThan(HttpStatus.SC_OK)); //---> Проверка статус код;
    }
}
