package com.irsec.harbour.ship.data.dto;

import java.util.Date;

public class TicketCheckingConditionDTO {

    private String passengerName;
    private String passportId;
    private Integer certificateType;
    private String idNumber;
    private Date checkingTimeSt;
    private Date checkingTimeEnd;
    private Integer verifyResult;
    private Integer operationType;
    private String shipNo;


    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

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

    public Date getCheckingTimeSt() {
        return checkingTimeSt;
    }

    public void setCheckingTimeSt(Date checkingTimeSt) {
        this.checkingTimeSt = checkingTimeSt;
    }

    public Date getCheckingTimeEnd() {
        return checkingTimeEnd;
    }

    public void setCheckingTimeEnd(Date checkingTimeEnd) {
        this.checkingTimeEnd = checkingTimeEnd;
    }

    public Integer getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(Integer verifyResult) {
        this.verifyResult = verifyResult;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public Integer getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(Integer certificateType) {
        this.certificateType = certificateType;
    }

    public String getShipNo() {
        return shipNo;
    }

    public void setShipNo(String shipNo) {
        this.shipNo = shipNo;
    }
}
