package com.coral.community.util;

import com.coral.community.entity.User;
import org.springframework.stereotype.Component;
/*
*  hold/store the user information
*  replace the session Object
* */
@Component
public class HostHolder {
    // ThreadLocal<User>: for current thread, user is isolated
    // users : store every thread's user
    private ThreadLocal<User> users = new ThreadLocal<>();

    // save
    public void setUser(User user){
        users.set(user);
    }
    // get
    public User getUser(){
        return users.get();
    }
    public void remove(){
        users.remove();
    }

}
