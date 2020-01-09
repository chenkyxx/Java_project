package com.irsec.harbour.ship.data.dto;


import com.irsec.harbour.ship.data.group.ValidatedGroup1;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class TicketCheckingInputDTO {

    @NotBlank(message = "checkinId 不能为空", groups = {ValidatedGroup1.class})
    private String checkinId;

    @NotBlank(message = "userBarcode 不能为空", groups = {ValidatedGroup1.class})
    private String userBarcode;

    @NotNull(message = "checkingTime 不能为空", groups = {ValidatedGroup1.class})
    private Date checkingTime;

    @NotBlank(message = "checkDeviceNo 不能为空", groups = {ValidatedGroup1.class})
    private String checkDeviceNo;

    //@NotBlank(message = "现场照不能为空", groups = {FaceCheckDTO.ValidateGroupManual.class})
    private String livePhoto;

    @NotNull(message = "人工放行字段不能为空",groups = {FaceCheckDTO.ValidateGroupManual.class})
    @Min(value = 0, message = "人工放行字段的值有误", groups = {FaceCheckDTO.ValidateGroupManual.class})
    @Max(value = 1, message = "人工放行字段的值有误", groups = {FaceCheckDTO.ValidateGroupManual.class})
    private Integer manualPass;


    public String getUserBarcode() {
        return userBarcode;
    }

    public void setUserBarcode(String userBarcode) {
        this.userBarcode = userBarcode;
    }



    public Date getCheckingTime() {
        return checkingTime;
    }

    public void setCheckingTime(Date checkingTime) {
        this.checkingTime = checkingTime;
    }

    public String getCheckDeviceNo() {
        return checkDeviceNo;
    }

    public void setCheckDeviceNo(String checkDeviceNo) {
        this.checkDeviceNo = checkDeviceNo;
    }

    public String getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(String checkinId) {
        this.checkinId = checkinId;
    }

    public String getLivePhoto() {
        return livePhoto;
    }

    public void setLivePhoto(String livePhoto) {
        this.livePhoto = livePhoto;
    }

    public Integer getManualPass() {
        return manualPass;
    }

    public void setManualPass(Integer manualPass) {
        this.manualPass = manualPass;
    }
}
