package com.irsec.harbour.ship.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.irsec.harbour.ship.data.entity.ShipLane;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import lombok.Data;
import org.springframework.data.domain.PageRequest;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/29 15:24
 * @Description: 旅客查询接口返回结构
 */
@Data
public class PassengerQueryDTO {
    @NotBlank(message = "id不能为空",groups = {ValidatedGroup2.class})
    private String id;
    private String passengerNameCh;
    private String sex;
    private String country;
    private String birthDay;
    private String passportId;
    private String idNumber;
    private String passportValidity;
    private String contact;
    private String carrierCh;
    private String shipNameCh;
    private String shipNo;
    private String sailDate;
    private String startingPort;
    private String memberLevel;
    private String roomNo;
    private String floor;
    private String reserveNo;
    private Integer certificateType;

    private Integer isChecked;
    private Integer verifyResult;

    private List<ShipLane> route;
    private String arrivetime;
}
