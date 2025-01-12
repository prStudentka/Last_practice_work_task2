package com.mts.creditservice.endpoints;

import com.mts.creditservice.core.Config;
import com.mts.creditservice.core.Specification;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class Tariffs {

    private final RequestSpecification reqSpecification;
    private final ValidatableResponse response;

    public Tariffs(RequestSpecification reqSpecification) throws IOException {
        this.reqSpecification = reqSpecification;
        response = checkTariffs();
    }

    @Step("Запуск теста запроса GET списка тарифов")
    @Description("Проверка запроса и status_code 200 списка тарифов")
    private ValidatableResponse checkTariffs() throws IOException {
        return given()
                .log().all()
                .spec(this.reqSpecification)
                .when()
                //---> Endpoint для выполнения запроса GET
                .request("GET", Config.GET_TARIFFS_URL) //---> Endpoint для выполнения запроса GET
                .then();
    }

    @Step("Тестирование ответа статус кода 200 списка тарифов")
    @Description("Проверка ответа списка тарифов кода 200")
    public void hasStatusCodeOkCheck() throws IOException {
        response
                .spec(Specification.responseSpecification())
                .log().ifValidationFails()
                .assertThat()
                .log().ifStatusCodeIsEqualTo(HttpStatus.SC_OK) //---> Проверка статус код
                .log().ifStatusCodeMatches(greaterThan(HttpStatus.SC_OK));
    }

    @Step("Тестирование тело ответа на пустоту списка тарифов")
    @Description("Проверка пустое ли тело ответа")
    public void hasNotEmptyCheck() throws IOException {
        response
                .log().body()
                .assertThat()
                .body("$", not(empty()));
    }

    @Step("Тестирование размера тело ответа списка тарифов")
    @Description("Проверка размера данных ответа больше 0")
    public void hasSizeCheck() throws IOException {
        response
                .assertThat()
                .body("data.tariffs", hasSize(greaterThan(0)));
    }

    @Step("Тестирование тело ответа на ключи")
    @Description("Проверка наличия ключей в теле ответа")
    public void hasKeyCheck() throws IOException {
        response
                .assertThat()
                .body("$", hasKey("data"))
                .body("data", hasKey("tariffs"))
                .log().ifValidationFails();
    }

    @Step("Тестирование ответа вхождения конкретного ключа со значением")
    @Description("Проверка наличия значения у ключа в теле ответа")
    public void hasEntryCheck() throws IOException {
        response
                .log().body()
                .assertThat()
                .body("data.tariffs", hasItems(hasEntry("id", 1)));
    }

    @Step("Тестирование ответа вхождения ключа со значениями")
    @Description("Проверка наличия значений у ключа в теле ответа")
    public void hasItemsTypeCheck() throws IOException {
        response
                .log().body()
                .assertThat()
                .body("data.tariffs.type", hasItems("CONSUMER", "SALE"));
    }

    @Step("Тестирование ответа вхождения запрашиваемого ключа со значением")
    @Description("Проверка наличия запрашиваемого значения у ключа в теле ответа")
    public void hasItemTypeCheck(String type) throws IOException {
        response
                .assertThat()
                .body("data.tariffs.type", hasItem(type))
                .log().ifValidationFails();
    }

    @Step("Тестирование ответа списка тарифов и возвращение id")
    @Description("Проверка и поиск запрашиваемого тарифа и возвращение id")
    public long getIdByItemTypeCheck(String type) throws IOException {
        return response
                .assertThat()
                .log().ifValidationFails()
                .extract().body()
                .jsonPath()
                .getLong("data.tariffs.find {it.type == '" + type + "'}.id");
    }
}
