package com.mts.creditservice.core;

public class UserOrdersData {
    private final long userId;
    private final String orderId;

    public UserOrdersData(long userId, String orderId) {
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
