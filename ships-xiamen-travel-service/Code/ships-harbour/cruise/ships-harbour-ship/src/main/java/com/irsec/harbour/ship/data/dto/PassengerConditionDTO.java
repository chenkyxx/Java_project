package com.irsec.harbour.ship.data.dto;

import java.util.Date;

public class PassengerConditionDTO {

    private Integer certificateType;
    private String passportId;
    private String idNumber;
    private Date sailDateSt;
    private Date sailDateEnd;


    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Date getSailDateSt() {
        return sailDateSt;
    }

    public void setSailDateSt(Date sailDateSt) {
        this.sailDateSt = sailDateSt;
    }

    public Date getSailDateEnd() {
        return sailDateEnd;
    }

    public void setSailDateEnd(Date sailDateEnd) {
        this.sailDateEnd = sailDateEnd;
    }

    public Integer getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(Integer certificateType) {
        this.certificateType = certificateType;
    }
}
