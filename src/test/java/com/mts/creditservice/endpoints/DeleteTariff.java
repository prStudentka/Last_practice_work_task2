package com.mts.creditservice.endpoints;

import com.mts.creditservice.core.Config;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;

public class DeleteTariff {
    private final Response response;

    public DeleteTariff(RequestSpecification reqSpecification, long tariffId) throws IOException {
        this.response = checkDeleteTariff(reqSpecification, tariffId);
    }

    @Step("Запуск теста проверки Delete тарифа {tariffId}")
    @Description("Проверка запроса и status_code 200 Delete тарифа")
    private static Response checkDeleteTariff(RequestSpecification reqSpecification, long tariffId) throws IOException {
        return given()
                .log().all()
                .filter(new AllureRestAssured())
                .spec(reqSpecification)
                .params("id", tariffId)
                .when()
                //---> Endpoint для выполнения запроса POST
                .request("DELETE", Config.DELETE_TARIFF_URL)//---> Endpoint для выполнения запроса DELETE
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
}
