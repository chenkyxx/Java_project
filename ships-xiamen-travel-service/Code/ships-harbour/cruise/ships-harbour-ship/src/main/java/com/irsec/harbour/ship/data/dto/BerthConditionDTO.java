package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 14:19
 * @Description:
 */
@Data
public class BerthConditionDTO {

    private String berthName;
    private String berthWeight;

    @Override
    public String toString() {
        return "{" +
                "泊位名称='" + berthName + '\'' +
                ", 泊位吨级='" + berthWeight + '\'' +
                '}';
    }
}
