package com.irsec.harbour.ship.data.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther: Jethro
 * @Date: 2019/9/3 11:34
 * @Description:
 */
public enum FlightPlanStatusEnum {

    STATUS_APPLY(0,"已计划"),
    STATUS_WAIT_TO_PLAN(1,"待作业"),
    STATUS_REJECT(2,"已驳回"),
    STATUS_WAIT_TO_FEEDBACK(3,"待反馈"),
    STATUS_FEEDBACK(4,"已反馈");

    @Getter
    @Setter
    private int statusInt;

    @Getter
    @Setter
    private String statusZh;

    FlightPlanStatusEnum(int statusInt, String statusZh){
        this.statusInt = statusInt;
        this.statusZh = statusZh;
    }



    public static String getStatusZh(int statusInt){
        for(FlightPlanStatusEnum e : FlightPlanStatusEnum.values()){
            if(e.statusInt == statusInt){
                return e.statusZh;
            }
        }
        return null;
    }

}
