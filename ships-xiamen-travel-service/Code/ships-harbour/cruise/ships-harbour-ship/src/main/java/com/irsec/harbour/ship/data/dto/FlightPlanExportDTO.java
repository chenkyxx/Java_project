package com.irsec.harbour.ship.data.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/8/26 11:24
 * @Description: 靠离泊计划的导出类
 */

@Data
public class FlightPlanExportDTO {
    private String shipNameZh;
    private String shipCallSign;
    private String shipType;
    private String planArriveTime;
    private String planDepartTime;
    private String isLead;
    private String berthName ;
    private Integer capacity;
    private String status;
}
