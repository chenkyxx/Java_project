package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/21 10:48
 * @Description: 船舶模糊搜索返回结构
 */
@Data
public class ShipSearchDTO {
    private String id;
    private String shipNameZh;

    public ShipSearchDTO(String id,String shipNameZh){
        this.id = id;
        this.shipNameZh = shipNameZh;
    }
}
