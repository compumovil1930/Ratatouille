package com.example.entities;

public class User {
    private String fullName;
    private int age;
    private String phone;
    private String email;
    private String address;
    private String type;

    public User() {

    }

    public User(String fullName, int age, String email, String address, String phone) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.type = "Client";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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
