package com.mts.creditservice.core;

public class Config {
    public static final String BASE_URL = "http://localhost:8080";
    public static final String AUTH_URL = "/auth/authenticate";
    public static final String USER_URL = "/auth/register";
    public static final String MAP_URL = "/loan-service";

    public static final String GET_TARIFFS_URL = MAP_URL + "/getTariffs";
    public static final String POST_TARIFF_URL = MAP_URL + "/addTariff";
    public static final String DELETE_TARIFF_URL = MAP_URL + "/deleteTariff";
    public static final String POST_ORDER_URL = MAP_URL + "/order";
    public static final String GET_STATUS_ORDER_URL = MAP_URL + "/getStatusOrder";
    public static final String DELETE_ORDER_URL = MAP_URL + "/deleteOrder";


    private static Config instance;
    private final String token_auth;

    public static Config getInstance(String value) {
        if (instance == null) {
            instance = new Config(value);
        }
        return instance;
    }

    public static Config getInstance() {
        return instance;
    }

    private Config(String token_auth){
        this.token_auth = token_auth;
    }

    public String getToken_auth() {
        return token_auth;
    }
}
