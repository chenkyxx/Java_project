package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/9/2 15:24
 * @Description: 旅客查询接口的条件
 */
@Data
public class PassengerQueryConditionDTO {
    private String idNumber;
    private String passengerName;
    private Integer type;
    private String idCard;
    private String shipNo;
    private String groupNo;

    private String phone;
}
