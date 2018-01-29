package com.example.shop.PublicUser;

import com.example.shop.DAO.UserDAO;
import com.sun.jndi.cosnaming.IiopUrl;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.shop.delivery.Address;
public class UserInfoForm {
    private UserProfile userProfile;
    @Autowired
    private UserDAO userDAO;

    public void createUser(String firstName, String lastName, String email, String password, Address address){
        this.userProfile = new UserProfile(firstName, lastName, email, password, address);
    }

    public UserProfile getUserProfile(){
        return userProfile;
    }
}
