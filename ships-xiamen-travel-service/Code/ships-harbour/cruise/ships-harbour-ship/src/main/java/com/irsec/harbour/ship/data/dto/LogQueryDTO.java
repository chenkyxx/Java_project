package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/23 15:22
 * @Description: 日志查询的DTO
 */
@Data
public class LogQueryDTO{

    private String id;
    private String createTime;
    private String device;
    private String optType;
    private String optUser;
    private String ipaddress;
    private String optDetail;
}
