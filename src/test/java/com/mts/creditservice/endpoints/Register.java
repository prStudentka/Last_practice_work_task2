package com.mts.creditservice.endpoints;

import com.mts.creditservice.core.Config;
import com.mts.creditservice.core.Specification;
import com.mts.creditservice.model.PostRegistrSchema;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

public class Register {
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String password;
    private static final String TOKEN_FIELD = "token";
    private final Response response;

    public Register(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.response = getRegistr();
    }

    @Step("Регистрация нового пользователя")
    @Description("Запрос на регистрацию пользователя")
    private Response getRegistr() {

        return given()
                .log().all()
                .filter(new AllureRestAssured())
                .spec(Specification.requestSpecification())
                .when()
                .body(new PostRegistrSchema(firstname, lastname, email, password))
                //---> Endpoint для выполнения запроса POST
                .request("POST", Config.USER_URL)//---> Endpoint для выполнения запроса POST
                .andReturn();
    }

    @Step("Тестирование ответа регистрации статус кода 200")
    @Description("Проверка ответа регистрации пользователя спецификацией")
    public void hasStatusCodeOkCheck() throws IOException {
        response
                .then()
                .spec(Specification.responseSpecification())
                .log().body()
                .log().everything()
                .assertThat()
                .log().ifStatusCodeMatches(Matchers.greaterThan(HttpStatus.SC_OK)); //---> Проверка статус код
    }

    @Step("Тестирование теле ответа на наличие токена")
    @Description("Проверка поля и токена в теле ответа")
    public void hasTokenCheck() throws IOException {
        response
                .then()
                .log().body()
                .assertThat()
                .body("$", hasKey(TOKEN_FIELD))
                .body(TOKEN_FIELD, Matchers.anything())
                .log().ifValidationFails();
    }
}
