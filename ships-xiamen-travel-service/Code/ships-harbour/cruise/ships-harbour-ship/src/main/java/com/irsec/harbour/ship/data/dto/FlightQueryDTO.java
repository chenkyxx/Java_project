package com.irsec.harbour.ship.data.dto;

import com.irsec.harbour.ship.data.entity.ShipLane;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/31 10:09
 * @Description: 旅客凭证打印查询接口需要返回的航班信息结构
 */

@Data
public class FlightQueryDTO {
    private String carrierEn;
    private String carrierCh;
    private Date sailDate;
    private String shipNameCh;
    private String shipNameEn;
    private String shipNo;
    private String startingPort;
    private String planArriveTime;
    List<ShipLane> routes;
}
