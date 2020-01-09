package com.irsec.harbour.ship.data.dto;

import com.irsec.harbour.ship.data.entity.ShipLane;
import lombok.Data;

import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/31 16:25
 * @Description: 行李条查询结构
 */
@Data
public class LuggageQueryDTO {
    private String id;
    private String luggageCode;
    private Integer verifyResult;
    private String passagerId;
    private String checkingTime;
    private String checkDeviceNo;
    private String passengerName;
    private String passportId;
    private String idNumber;
    private String contact;
    private String country;
    private Integer operationType;
    private String uploadTime;
    private String flightId;
    private String shipNo;
    private Integer certificateType;
    private String checkedResult;
    private List<ShipLane> lanes;
    private String shipLaneList;
}
