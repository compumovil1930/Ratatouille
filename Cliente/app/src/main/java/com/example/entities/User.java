package com.example.entities;

import java.io.Serializable;

public class User implements Serializable {
    private String fullName;
    private int age;
    private String email;
    private Address address;
    private String type;

    public User(){

    }

    public User(String fullName, int age, String email, Address address) {
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
