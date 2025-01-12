package com.mts.creditservice.model;

public class PostRegistrSchema {
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    public PostRegistrSchema(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;

    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
