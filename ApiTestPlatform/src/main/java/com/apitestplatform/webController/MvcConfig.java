/**
 * Copyright (C), 2018-2019, 重庆智汇航安智能科技研究院有限公司
 * FileName: MvcConfig
 * Author:   Original Dream
 * Date:     2019/9/20 15:17
 * Description:
 */
package com.apitestplatform.webController;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //addResourceLocations指的是资源文件放置的目录
        //addResourceHandler是对外暴露的访问路径
        registry.addResourceHandler("/static/**").addResourceLocations(
                "classpath:/static/");

    }

}
