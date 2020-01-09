package com.irsec.harbour.ship.data.dto;

import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/27 14:04
 * @Description:
 */
@Data
public class JobExportDTO  {

    private String shipNameZh;
    private String shipCallSign;
    private String planArriveTime;
    private String planDepartTime;
    private String planPassTime;
    private String planCloseTime;
    private String berthName;
    private String status;
}
