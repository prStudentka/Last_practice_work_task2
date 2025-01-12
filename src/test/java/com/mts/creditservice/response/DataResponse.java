package com.mts.creditservice.response;

import io.restassured.response.ValidatableResponse;

public class DataResponse {
    private ValidatableResponse response;

    public DataResponse(ValidatableResponse response) {
        this.response = response;
    }

    public ValidatableResponse getResponse() {
        return response;
    }
}
