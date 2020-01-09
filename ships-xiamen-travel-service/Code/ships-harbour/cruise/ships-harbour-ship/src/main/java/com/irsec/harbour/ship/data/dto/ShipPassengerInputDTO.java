package com.irsec.harbour.ship.data.dto;

import java.util.Date;

public class ShipPassengerInputDTO {
    private String id;

    private String userId;

    private String userBarcode;


    private String passengerName;

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

    private String flightId;



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

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
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

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }
}
