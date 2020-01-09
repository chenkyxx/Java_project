package com.irsec.harbour.ship.data.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.irsec.harbour.ship.data.dto.PassengerQueryDTO;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ship_passenger",
        indexes = {
                @Index(name = "index_ship_passenger_1", columnList = "userBarcode"),
                @Index(name = "index_ship_passenger_2", columnList = "userId"),
                @Index(name = "index_ship_passenger_3", columnList = "passportId"),
                @Index(name = "index_ship_passenger_4", columnList = "idNumber"),
                @Index(name = "index_ship_passenger_5", columnList = "flightId,checkDeviceNo")
        })
public class ShipPassenger {

    @Id
    @Column(length = 32)
    private String id;

    @Column(length = 64, unique = true)
    private String userId;

    @Column(length = 128)
    private String userBarcode;

    @Column(length = 64)
    private String carrierCh;

    @Column(length = 64)
    private String carrierEn;

    @Column(length = 64)
    private String shipNameCh;

    @Column(length = 64)
    private String shipNameEn;

    @Column(length = 64)
    private String shipNo;

    @Column
    private Date sailDate;

    @Column(length = 32)
    private String groupNo;

    @Column
    private String planArriveTime;

    @Column(length = 64)
    private String startingPort;

    @Column(length = 256)
    private String route;

    @Column(length = 256)
    private String passengerNameEn;

    @Column(length = 256)
    private String passengerNameCh;

    @Column(length = 2)
    private String sex;

    @Column(length = 32)
    private String country;

    @Column
    private Date birthDay;

    @Column
    private Integer certificateType;

    @Column
    private Integer touristType;
    @Column(length = 64)
    private String passportId;

    @Column
    private Date passportValidity;

    @Column(length = 32)
    private String idNumber;

    @Column(length = 128)
    private String contact;

    @Column(length = 64)
    private String reserveNo;

    @Column(length = 16)
    private String floor;

    @Column(length = 16)
    private String roomNo;

    @Column(length = 16)
    private String memberLevel;

    @Column(length = 16)
    private String ticketType;

    @Column(length = 32)
    private String escapeArea;

    @Column
    private int isCheckingTacket;

    @Column
    private Date checkingTime;

    @Column(length = 32)
    private String checkDeviceNo;

    @Column
    private int isPrint;

    @Column
    private Date printTime;

    @Column(length = 32)
    private String flightId;

    @Column(length = 32)
    private String location;

    @Column
    private String remarks;
//    @Column
//    private String leader;

    @Column
    private String touristIdentity; // 旅客类型：领队，乘客，员工

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="passenger_id",referencedColumnName = "id",insertable = false, updatable = false)
    @NotFound(action=NotFoundAction.IGNORE)
    private List<ShipLuggageCode> shipLuggageCodes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="flightId",referencedColumnName = "id",insertable = false, updatable = false)
    @NotFound(action=NotFoundAction.IGNORE)
    private ShipFlight shipFlight;

    @Column(name = "create_time")
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
        return "ShipPassenger{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", userBarcode='" + userBarcode + '\'' +
                ", carrierCh='" + carrierCh + '\'' +
                ", carrierEn='" + carrierEn + '\'' +
                ", shipNameCh='" + shipNameCh + '\'' +
                ", shipNameEn='" + shipNameEn + '\'' +
                ", shipNo='" + shipNo + '\'' +
                ", sailDate=" + sailDate +
                ", groupNo='" + groupNo + '\'' +
                ", planArriveTime=" + planArriveTime +
                ", startingPort='" + startingPort + '\'' +
                ", route='" + route + '\'' +
                ", passengerNameEn='" + passengerNameEn + '\'' +
                ", passengerNameCh='" + passengerNameCh + '\'' +
                ", sex='" + sex + '\'' +
                ", country='" + country + '\'' +
                ", birthDay=" + birthDay +
                ", passportId='" + passportId + '\'' +
                ", passportValidity=" + passportValidity +
                ", idNumber='" + idNumber + '\'' +
                ", contact='" + contact + '\'' +
                ", reserveNo='" + reserveNo + '\'' +
                ", floor='" + floor + '\'' +
                ", roomNo='" + roomNo + '\'' +
                ", memberLevel='" + memberLevel + '\'' +
                ", ticketType='" + ticketType + '\'' +
                ", escapeArea='" + escapeArea + '\'' +
                ", isCheckingTacket=" + isCheckingTacket +
                ", checkingTime=" + checkingTime +
                ", checkDeviceNo='" + checkDeviceNo + '\'' +
                ", isPrint=" + isPrint +
                ", printTime=" + printTime +
                ", flightId='" + flightId + '\'' +
                ", location='" + location + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserBarcode() {
        return userBarcode;
    }

    public void setUserBarcode(String userBarcode) {
        this.userBarcode = userBarcode;
    }

    public String getCarrierCh() {
        return carrierCh;
    }

    public void setCarrierCh(String carrierCh) {
        this.carrierCh = carrierCh;
    }

    public String getCarrierEn() {
        return carrierEn;
    }

    public void setCarrierEn(String carrierEn) {
        this.carrierEn = carrierEn;
    }

    public String getShipNameCh() {
        return shipNameCh;
    }

    public void setShipNameCh(String shipNameCh) {
        this.shipNameCh = shipNameCh;
    }

    public String getShipNameEn() {
        return shipNameEn;
    }

    public void setShipNameEn(String shipNameEn) {
        this.shipNameEn = shipNameEn;
    }

    public String getShipNo() {
        return shipNo;
    }

    public void setShipNo(String shipNo) {
        this.shipNo = shipNo;
    }

    public Date getSailDate() {
        return sailDate;
    }

    public void setSailDate(Date sailDate) {
        this.sailDate = sailDate;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getPlanArriveTime() {
        return planArriveTime;
    }

    public void setPlanArriveTime(String planArriveTime) {
        this.planArriveTime = planArriveTime;
    }

    public String getStartingPort() {
        return startingPort;
    }

    public void setStartingPort(String startingPort) {
        this.startingPort = startingPort;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public Date getPassportValidity() {
        return passportValidity;
    }

    public void setPassportValidity(Date passportValidity) {
        this.passportValidity = passportValidity;
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

    public String getReserveNo() {
        return reserveNo;
    }

    public void setReserveNo(String reserveNo) {
        this.reserveNo = reserveNo;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getEscapeArea() {
        return escapeArea;
    }

    public void setEscapeArea(String escapeArea) {
        this.escapeArea = escapeArea;
    }

    public int getIsCheckingTacket() {
        return isCheckingTacket;
    }

    public void setIsCheckingTacket(int isCheckingTacket) {
        this.isCheckingTacket = isCheckingTacket;
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

    public int getIsPrint() {
        return isPrint;
    }

    public void setIsPrint(int isPrint) {
        this.isPrint = isPrint;
    }

    public Date getPrintTime() {
        return printTime;
    }

    public void setPrintTime(Date printTime) {
        this.printTime = printTime;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getPassengerNameEn() {
        return passengerNameEn;
    }

    public void setPassengerNameEn(String passengerNameEn) {
        this.passengerNameEn = passengerNameEn;
    }

    public String getPassengerNameCh() {
        return passengerNameCh;
    }

    public void setPassengerNameCh(String passengerNameCh) {
        this.passengerNameCh = passengerNameCh;
    }

    public Integer getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(Integer certificateType) {
        this.certificateType = certificateType;
    }

    public Integer getTouristType() {
        return touristType;
    }

    public void setTouristType(Integer touristType) {
        this.touristType = touristType;
    }

    public List<ShipLuggageCode> getShipLuggageCodes() {
        return shipLuggageCodes;
    }

    public void setShipLuggageCodes(List<ShipLuggageCode> shipLuggageCodes) {
        this.shipLuggageCodes = shipLuggageCodes;
    }

    public ShipFlight getShipFlight() {
        return shipFlight;
    }

    public void setShipFlight(ShipFlight shipFlight) {
        this.shipFlight = shipFlight;
    }

    public String getTouristIdentity() {
        return touristIdentity;
    }

    public void setTouristIdentity(String touristIdentity) {
        this.touristIdentity = touristIdentity;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
