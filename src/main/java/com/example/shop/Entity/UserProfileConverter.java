package com.example.shop.Entity;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import com.example.shop.PublicUser.UserProfile;
import com.example.shop.Entity.User;
import com.example.shop.delivery.Address;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import com.example.shop.Entity.User;


public class UserProfileConverter {
    public static User convertToUser(UserProfile userProfile){
        User user = new User();
        user.setFirstName(userProfile.getFirstName());
        user.setLastName(userProfile.getLasttName());
        user.setEmail(userProfile.getEmail());
        user.setPassword(userProfile.getPassword());
        user.setLatitude(userProfile.getAddress().getLatitude());
        user.setLongitude(userProfile.getAddress().getLongitude());
        //not set in public user
        user.setBalance(BigDecimal.ZERO);
        user.setLastLogin(Timestamp.from(Instant.now()));
        return user;
    }
}
