package com.irsec.harbour.ship.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.irsec.harbour.ship.data.entity.ShipLane;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 16:58
 * @Description: 生产作业计划查询/生产作业历史的返回结构
 * JsonInclude.Include.NON_EMPTY 该注解将不会序列化值为空或者为null的字段
 */
@Data
public class JobQueryDto {
    private String id;
    private String shipNameZh;
    private String shipCallSign;
    private String berthName;
    private String shipAgent;
    private String portType;
    private String planArriveTime;
    private String planDepartTime;
    private String planPassTime;
    private String planCloseTime;
    private String actualArriveTime;
    private String actualDepartTime;
    private String actualPassTime;
    private String actualCloseTime;
    private Long parkingHour;
    private Integer capacity;
    private Integer passNumber;
    private Integer luggageNumber;
    private Integer isLead;
    private String createTime;
    private String createUser;
    private String prePort;
    private String nextPort;
    List<ShipLane> lanes;

    private String planPilotageTime;
    //引水时长
    private Float pilotageHour;
    //状态 ( 1：待计划 3：待反馈 4：已反馈
    private Integer status;

    //离境通关时间
    private String leavePassTime;

    //离境截关时间
    private String leaveCloseTime;

    //垃圾（立方米）
    private Float garbageNum;

    //加水（吨)
    private Float addWaterNum;


    //吊车（辆）
    private Integer craneNum;

    //叉车（辆）
    private Integer forkliftNum;


    //辅工（人)
    private Integer helpWorkerNum;


    //船员（人）
    private Integer sailorNum;

}
