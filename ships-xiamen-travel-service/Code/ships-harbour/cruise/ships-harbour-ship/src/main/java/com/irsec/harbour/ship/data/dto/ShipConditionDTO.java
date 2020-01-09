package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 17:44
 * @Description: 用于船舶的条件查询
 */
@Data
public class ShipConditionDTO {

    private String shipCallSign;
    private String shipType;

    @Override
    public String toString() {
        return "{" +
                "船舶呼号='" + shipCallSign + '\'' +
                ", 船舶类型='" + shipType + '\'' +
                '}';
    }
}
