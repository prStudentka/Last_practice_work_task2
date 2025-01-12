package com.mts.creditservice.model;

public class PostTariffSchema {
    private String type;
    private String interest_rate;

    public PostTariffSchema(String type, String interest_rate) {
        this.type = type;
        this.interest_rate = interest_rate;

    }

    public String getType() {
        return type;
    }

    public String getInterest_rate() {
        return interest_rate;
    }
}
