package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/27 16:18
 * @Description:
 */

@Data
public class LogConditionDTO {
    private String optUser;
    private String ipaddress;
    private Integer optType;
    private String startTime;
    private String endTime;
}
