package com.irsec.harbour.ship.data.dto;

import com.irsec.harbour.ship.data.entity.ShipLane;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 15:38
 * @Description: 靠离泊计划查询结果
 */
@Data
public class FlightPlanQueryDTO {
    private String id;
    private String shipNameZh;
    private String shipNameEn;
    private String shipType;
    private String shipCallSign;
    private String planArriveTime;
    private String planDepartTime;
    private Integer isLead;
    private String berthName;
    private Integer capacity;
    private Integer status;

    private Integer isSelect;

    private String createUser;
    private String createTime;

    private String prePort;
    private String nextPort;
    private String shipId;
    private String berId;
    private String portType;
    private String shipAgent;

    private Integer side;

    private Date planArrivePortTime;

    private List<ShipLane> shipLanes;
}
