package com.example.simpleloginapp;

public class Person {

    // private variables
    private String firstname;
    private String lastname;
    private String gender;
    private String birthdate;
    private String email;

    // default constructor
    public Person(){}

    // constructor
    public Person(String firstname, String lastname, String gender, String birthdate, String email){
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.email = email;
    }

    // setters and getters (mutators and accessors)
    public void setFirstname (String firstname) { this.firstname=firstname; }

    public String getFirstname() { return firstname; }

    public void setLastname (String lastname) { this.lastname=lastname; }

    public String getLastname() { return lastname; }

    public void setGender(String gender) { this.gender = gender; }

    public String getGender() { return gender; }

    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getBirthdate() { return birthdate; }

    public void setEmail(String email) { this.email = email; }

    public String getEmail() { return email; }

}