package com.mts.creditservice.core;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static com.mts.creditservice.core.Config.BASE_URL;

public class Specification {
    private static final String HEADER_NAME = "Authorization";
    private static final String AUTH_TYPE = "Bearer";

    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setRelaxedHTTPSValidation()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public static RequestSpecification requestSpecification(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .addHeader(HEADER_NAME, AUTH_TYPE + " " + token)
                .setRelaxedHTTPSValidation()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    public static ResponseSpecification responseSpecification() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK) // ---> Проверка статус код
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpecification(int status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status) // ---> Проверка статус код
                .expectContentType(ContentType.JSON)
                .build();
    }
}
