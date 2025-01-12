package com.mts.creditservice.model;

public class DeleteOrderSchema {
    private final long userId;
    private final String orderId;

    public DeleteOrderSchema(long userId, String orderId) {
        this.userId = userId;
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }
}
