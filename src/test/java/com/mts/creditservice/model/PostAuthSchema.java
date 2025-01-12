package com.mts.creditservice.model;

public class PostAuthSchema {
    private String email;
    private String password;

    public PostAuthSchema(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
