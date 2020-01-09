package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/27 14:11
 * @Description:
 */
@Data
public class JobHistoryExportDTO {
    private String shipNameZh;
    private String shipCallSign;
    private String actualArriveTime;
    private String actualDepartTime;
    private String actualPassTime;
    private String actualCloseTime;
    private String parkingHour;
    private Integer passNumber;
    private Integer luggageNumber;
}
