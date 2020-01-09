package com.irsec.harbour.ship.data.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.StringUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ship_manualCheckIn",
        indexes = {
                @Index(name = "index_ship_manualCheckIn_1", columnList = "flightId,checkDeviceNo"),
                @Index(name = "index_ship_manualCheckIn_2", columnList = "passengerId"),
        })
public class ShipManualCheckin {

    @Id
    @Column(length = 32)
    private String id;

    @Column(length = 32)
    private String passengerId;

    @Column
    private String barcode;

    @Column
    private int verifyResult;

    @Column
    private Date checkingTime;

    @Column(length = 32)
    private String checkDeviceNo;

    @Column(length = 64)
    private String passengerName;

    @Column(length = 64)
    private String passportId;

    @Column(length = 32)
    private String idNumber;

    @Column(length = 128)
    private String contact;

    @Column(length = 32)
    private String country;

    @Column
    private int operationType;

    @Column
    private Date uploadTime;

    @Column(length = 64)
    private String shipNo;

    @Column(length = 32)
    private String flightId;

    @Column
    private String livePhoto;
    @Column(name = "certificate_photo")
    private String certificatePhoto;
    @Column
    private Integer manualPass;

    //前端的比对结果
    @Column(name = "compare_result", length = 3)
    private Integer compareResult;
    /**
     * 0-身份证,
     * 其他参考边检证件类型
     */
    @Column(name = "certificate_type",length = 3)
    private Integer certificateType;

    private Float score;

    /**
     * 核销
     * 0：未核销
     * 1：已核销
     */
    private Integer isCancel = 0;

    /**
     * ticketId
     */
    private String ticketId;

    //性别
    @Column(length = 2)
    private String sex;
    //生日
    @Column
    private Date birthDay;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "passengerId", insertable = false, updatable = false)
    private ShipPassenger passenger;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flightId", insertable = false, updatable = false)
    private ShipFlight flight;

    private String shipLaneList;


    @Column(name = "create_time",nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.CreationTimestamp
    @JsonIgnore
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time",nullable = true)
    @org.hibernate.annotations.UpdateTimestamp
    @JsonIgnore
    private Date updateTime;


    @Override
    public String toString() {
        return "ShipManualCheckin{" +
                "id='" + id + '\'' +
                ", passengerId='" + passengerId + '\'' +
                ", barcode='" + barcode + '\'' +
                ", verifyResult=" + verifyResult +
                ", checkingTime=" + checkingTime +
                ", checkDeviceNo='" + checkDeviceNo + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", passportId='" + passportId + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", contact='" + contact + '\'' +
                ", country='" + country + '\'' +
                ", operationType=" + operationType +
                ", uploadTime=" + uploadTime +
                ", shipNo='" + shipNo + '\'' +
                ", flightId='" + flightId + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getShipNo() {
        return shipNo;
    }

    public void setShipNo(String shipNo) {
        this.shipNo = shipNo;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public int getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(int verifyResult) {
        this.verifyResult = verifyResult;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ShipPassenger getPassenger() {
        return passenger;
    }

    public void setPassenger(ShipPassenger passenger) {
        this.passenger = passenger;
    }

    public ShipFlight getFlight() {
        return flight;
    }

    public void setFlight(ShipFlight flight) {
        this.flight = flight;
    }


    public String getLivePhoto() {
        return livePhoto;
    }



    public void setLivePhoto(String livePhoto) {
        this.livePhoto = livePhoto;
    }

    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = 13 * hashno + (passportId == null ? 0 : passportId.hashCode());
        return hashno;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        final ShipManualCheckin shipManualCheckin = (ShipManualCheckin) obj;
        if (this == shipManualCheckin) {
            return true;
        } else {
            if(!org.springframework.util.StringUtils.isEmpty(this.getPassportId()) && !org.springframework.util.StringUtils.isEmpty(shipManualCheckin.getPassportId())){
                if(this.getPassportId().compareTo(shipManualCheckin.getPassportId()) == 0 && DateUtil.dateEqual(this.getCheckingTime(),shipManualCheckin.getCheckingTime())){
                    return true;
                }else{
                    return false;
                }

            }else{
                return false;
            }

        }
    }

    public String getShipLaneList() {
        return shipLaneList;
    }

    public void setShipLaneList(String shipLaneList) {
        this.shipLaneList = shipLaneList;
    }


    public Integer getManualPass() {
        return manualPass;
    }

    public Integer getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(Integer certificateType) {
        this.certificateType = certificateType;
    }

    public void setManualPass(Integer manualPass) {
        this.manualPass = manualPass;
    }

    public Integer getCompareResult() {
        return compareResult;
    }

    public void setCompareResult(Integer compareResult) {
        this.compareResult = compareResult;
    }

    public String getCertificatePhoto() {
        return certificatePhoto;
    }

    public void setCertificatePhoto(String certificatePhoto) {
        this.certificatePhoto = certificatePhoto;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
