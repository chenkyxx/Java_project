package com.irsec.harbour.ship.data.dto;

import com.irsec.harbour.ship.utils.DateUtil;
import lombok.Data;

import java.sql.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 15:23
 * @Description: 靠离泊计划的条件查询
 */
@Data
public class FlightPlanConditionDTO {
    private String shipNameZh;
    private String portType;
    private String berthId;
    private Integer queryType;
    private Date startTime;
    private Date endTime;


    @Override
    public String toString() {
        return "{" +
                "船舶名称='" + shipNameZh + '\'' +
                ", 港口类型='" + portType + '\'' +
                ", 泊位id='" + berthId + '\'' +
                ", 开始时间='" + DateUtil.dateToStr(startTime,"yyyy-MM-dd HH:mm:ss") + '\'' +
                ", 结束时间='" + DateUtil.dateToStr(endTime,"yyyy-MM-dd HH:mm:ss") + '\'' +
                '}';
    }
}
