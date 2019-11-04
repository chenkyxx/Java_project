/**
 * Copyright (C), 2018-2019, 重庆智汇航安智能科技研究院有限公司
 * FileName: UserController
 * Author:   Original Dream
 * Date:     2019/9/20 15:53
 * Description:
 */
package com.apitestplatform.webController;

import com.apitestplatform.entity.User;
import com.apitestplatform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    private static Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired UserService userService;

    @RequestMapping(value = "/login")
    public String getLogin(@RequestParam(value = "name") String name,
                               @RequestParam(value = "password") String password,
                               HttpSession session,
                               RedirectAttributes attributes,
                           HttpServletRequest httpServletRequest){
        try {
            log.info("开始进入方法");
            User user =  this.userService.checkUser(name, password);

            String validateResult = "index.html";
            String main = "main.html";
            if (user!=null){
                if (user.getUsername().equals(name) && user.getPassword().equals(password)) {
                    log.info("查询到该人员信息");
                    return main;
                }else {
                    log.error("该人员信息不存在");
                    return validateResult;
                }
            }else {
                log.error("返回的人员信息为null");
                httpServletRequest.setAttribute("LoginResult", "用户名密码错误，请检查");
                return "index1.html";
            }
        }catch (Exception e){
            System.err.println(e.toString()+"!!!!!!!!!!!");
            return "index1.html";
        }

    }
}
