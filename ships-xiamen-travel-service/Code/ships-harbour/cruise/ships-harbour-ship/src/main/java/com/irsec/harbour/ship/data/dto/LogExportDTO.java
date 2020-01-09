package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/28 14:38
 * @Description: 日志的导出结构
 */
@Data
public class LogExportDTO {
    private String createTime;
    private String device;
    private String optType;
    private String optUser;
    private String ipaddress;
    private String optDetail;
}
