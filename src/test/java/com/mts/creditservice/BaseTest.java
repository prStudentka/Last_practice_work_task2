package com.mts.creditservice;

import com.mts.creditservice.core.Config;
import com.mts.creditservice.core.Specification;
import com.mts.creditservice.endpoints.*;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static com.mts.creditservice.core.Config.BASE_URL;
import java.io.IOException;

public class BaseTest {
    protected static RequestSpecification requestAuthSpecification;

    @BeforeAll
    public static void setAuth() throws IOException {
        RestAssured.baseURI = BASE_URL;
        Auth client = new Auth("ivanov@mail.ru","1234");
        client.getAuth();
        requestAuthSpecification = Specification.requestSpecification(Config.getInstance().getToken_auth());
    }



    @AfterAll
    public static void tearDown() {
        RestAssured.reset();
        requestAuthSpecification = null;
    }
}
