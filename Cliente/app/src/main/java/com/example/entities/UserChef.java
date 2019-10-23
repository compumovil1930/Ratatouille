package com.example.entities;

import java.io.Serializable;

public class UserChef implements Serializable {

    private String email;

    private String password;

    private String fullName;

    private Address address;

    private String biography;

    private int age;

    private int yearsOfExperience;

    private String type;

    private String uri;

    public UserChef(){

    }

    public UserChef(String email, String password, String fullName, Address address, String biography, int age, int yearsOfExperience, String uri) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.biography = biography;
        this.age = age;
        this.yearsOfExperience = yearsOfExperience;
        this.type = "Chef";
        this.uri = uri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
