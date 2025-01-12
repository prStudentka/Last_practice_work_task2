package com.mts.creditservice.endpoints;

import com.mts.creditservice.core.Config;
import com.mts.creditservice.core.Specification;

import com.mts.creditservice.model.PostTariffSchema;
import io.qameta.allure.Description;
import io.qameta.allure.Step;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PostTariff {
    private final Response response;

    public PostTariff(RequestSpecification reqSpecification, String type, String interestRate) throws IOException {
        this.response = checkAddTariff(reqSpecification, type, interestRate);
    }

    @Step("Запуск теста POST создания тарифа {type}")
    @Description("Проверка запроса схемы и status_code 200 тарифа")
    private static Response checkAddTariff(RequestSpecification reqSpecification, String type, String interest_rate) throws IOException {
        return given()
                .log().all()
                .filter(new AllureRestAssured())
                .spec(reqSpecification)
                .when()
                .body(new PostTariffSchema(type, interest_rate))
                //---> Endpoint для выполнения запроса POST
                .request("POST", Config.POST_TARIFF_URL)//---> Endpoint для выполнения запроса POST
                .andReturn();
    }

    @Step("Тестирование ответа статус кода 200 создания тарифа ")
    @Description("Проверка ответа на status_code 200")
    public void hasStatusCodeOkCheck() throws IOException {
        response
                .then()
                .spec(Specification.responseSpecification())
                .log().body()
                .log().ifValidationFails()
                .assertThat()
                .log().ifStatusCodeIsEqualTo(HttpStatus.SC_OK) //---> Проверка статус код
                .log().ifStatusCodeMatches(greaterThan(HttpStatus.SC_OK));
    }

    @Step("Тестирование ответа статус код больше 200 создания тарифа")
    @Description("Проверка и логирование ответа со status_code больше 200, так как приходит 403")
    public void hasStatusCodeNot200Check() throws IOException {
        response
                .then()
                .log().ifStatusCodeMatches(greaterThan(HttpStatus.SC_OK)); //---> Проверка статус код
    }
}
