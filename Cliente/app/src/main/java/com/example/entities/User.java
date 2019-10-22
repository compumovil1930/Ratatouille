package com.example.entities;

public class User {
    private String fullName;
    private Double age;
    private String email;
    private String address;
    private String type;

    public User() {

    }

    public User(String fullName, Double age, String email, String address) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.address = address;
        this.type = "Client";
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
