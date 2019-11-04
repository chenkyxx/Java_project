package com.apitestplatform.service;

import com.apitestplatform.entity.User;


public interface UserService {
    User checkUser(String name, String password);
}
