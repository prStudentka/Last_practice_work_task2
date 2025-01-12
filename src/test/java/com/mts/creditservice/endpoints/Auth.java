package com.mts.creditservice.endpoints;

import com.mts.creditservice.core.Specification;
import com.mts.creditservice.core.Config;
import com.mts.creditservice.model.PostAuthSchema;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

public class Auth {

    private final String email;
    private final String password;
    private static final String TOKEN_FIELD = "token";


    public Auth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Step("Авторизация")
    @DisplayName("Авторизация")
    @Description("Запрос на получения токен авторизации")
    public void getAuth(){

        JsonPath  bodyResponse = given()
                .spec(Specification.requestSpecification())
                .when()
                .body(new PostAuthSchema(email, password))
                //---> Endpoint для выполнения запроса POST
                .request("POST", Config.AUTH_URL)//---> Endpoint для выполнения запроса POST
                .then()
                .spec(Specification.responseSpecification())
                .assertThat()
                .log().ifStatusCodeMatches(Matchers.greaterThan(HttpStatus.SC_OK)) //---> Проверка статус код
                .body("$", hasKey(TOKEN_FIELD))
                .body(TOKEN_FIELD, Matchers.anything())
                .extract().body().jsonPath();

        Config.getInstance(bodyResponse.get("token"));
    }


}
