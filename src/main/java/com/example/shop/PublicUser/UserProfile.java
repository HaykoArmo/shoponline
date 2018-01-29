package com.example.shop.PublicUser;

import com.example.shop.delivery.Address;
import com.sun.jndi.cosnaming.IiopUrl;

public class UserProfile {
    private String firstName;
    private String lastName;
    private String email;
    private String password; //will hash later
    private Address address;

    public UserProfile(String firstName, String lastName, String email, String password, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLasttName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword(){
        return password;
    }

    public Address getAddress() {
        return address;
    }
}
