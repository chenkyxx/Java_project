/**
 * Copyright (C), 2018-2019, 重庆智汇航安智能科技研究院有限公司
 * FileName: User
 * Author:   Original Dream
 * Date:     2019/9/20 15:59
 * Description:
 */
package com.apitestplatform.entity;
import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name = "travel_user")
public class User implements Serializable {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "user_name")
    public String username;

    @Column(name = "user_password")
    public String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
