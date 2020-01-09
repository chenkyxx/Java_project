package com.irsec.harbour.ship.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/31 10:51
 * @Description:
 */
@Data
public class PassengerPrintQueryDto {


    private String userId;
    private String passengerNameEn;
    private String passengerNameCh;
    private String groupNo;
    private String certificateType;
    private String sex;
    private String country;
    private Date birthDay;
    private String passportId;
    private Date passportValidity;
    private String idNumber;
    private String contact;
    private String reserveNo;
    private String floor;
    private String roomNo;
    private String memberLevel;
    private String ticketType;
    private String escapeArea;
    private String location;
    private Integer isPrint;
    private String officeCode;
    private Integer touristType;
    private String userBarcode;
    private String leader;

    //@JsonIgnore
    private List<LuggageCodeDTO> luggages;
    //@JsonIgnore
    private String id;

}
