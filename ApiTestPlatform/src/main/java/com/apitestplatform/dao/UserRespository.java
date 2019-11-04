package com.apitestplatform.dao;

import com.apitestplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRespository extends JpaRepository<User,Long> {
    @Query("from User where user_name=?1 and user_password=?2")
    User findUserByALL(String name, String password);
}
