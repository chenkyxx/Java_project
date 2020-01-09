package com.irsec.harbour.ship.data.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 15:25
 * @Description: 生产作业计划和生产作业历史的条件查询
 */
@Data
public class JobConditionDTO {
    private String shipNameZh;
    private String berthId;
    private Integer status;
    private Date startTime;
    private Date endTime;

    @Override
    public String toString() {
        return "{" +
                "船舶名称='" + shipNameZh + '\'' +
                ", 泊位id='" + berthId + '\'' +
                '}';
    }
}
