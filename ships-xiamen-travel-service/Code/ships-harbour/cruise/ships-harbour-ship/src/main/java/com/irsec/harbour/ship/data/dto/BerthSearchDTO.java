package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/21 11:12
 * @Description:
 */
@Data
public class BerthSearchDTO {
    private String id;
    private String berthName;
    private Integer status;
}
