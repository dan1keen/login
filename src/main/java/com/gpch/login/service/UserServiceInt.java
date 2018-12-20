package com.gpch.login.service;

import com.gpch.login.model.User;

public interface UserServiceInt {

    User findUserByEmail(String email);
    User saveUser(User user);
}
