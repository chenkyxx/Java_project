/**
 * Copyright (C), 2018-2019, 重庆智汇航安智能科技研究院有限公司
 * FileName: UserServiceImpl
 * Author:   Original Dream
 * Date:     2019/9/20 16:35
 * Description:
 */
package com.apitestplatform.service;

import com.apitestplatform.dao.UserRespository;
import com.apitestplatform.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRespository userRespository;
    @Override
    public User checkUser(String name, String password) {
        User user = this.userRespository.findUserByALL(name, password);
        return user;
    }
}
