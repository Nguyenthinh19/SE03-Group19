package com.example.orderfoodsapp.Model;

public class User {
    private String name;
    private String password;
    private String phone;
    private String secureCode;


    public User() {

    }

    public User(String name, String password, String secureCode) {
        this.name = name;
        this.password = password;
        this.secureCode = secureCode;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
