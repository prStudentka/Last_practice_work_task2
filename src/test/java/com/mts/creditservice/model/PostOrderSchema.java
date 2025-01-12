package com.mts.creditservice.model;

public class PostOrderSchema {
    private final long userId;
    private final long tariffId;

    public PostOrderSchema(long userId, long tariffId) {
        this.userId = userId;
        this.tariffId = tariffId;
    }

    public long getUserId() {
        return userId;
    }

    public long getTariffId() {
        return tariffId;
    }
}
