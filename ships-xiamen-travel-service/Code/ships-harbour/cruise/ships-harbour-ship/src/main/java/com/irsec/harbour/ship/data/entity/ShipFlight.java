package com.irsec.harbour.ship.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ship_flight")
public class ShipFlight {

    @Id
    @Column(length = 32)
    private String id;

    @Column(length = 64,nullable = false)
    private String carrierCh;

    @Column(length = 64)
    private String carrierEn;

    @Column(length = 64)
    private String shipNameCh;

    @Column(length = 64)
    private String shipNameEn;

    @Column(length = 64)
    private String shipNo;

    @Column(nullable = false)
    private Date sailDate;

    @Column
    private String planArriveTime;

    @Column(length = 64)
    private String startingPort;

    @Column
    private Date checkTime;

    public String getFlightPlanId() {
        return flightPlanId;
    }

    public void setFlightPlanId(String flightPlanId) {
        this.flightPlanId = flightPlanId;
    }

    @Column
    private String flightPlanId;


    public List<ShipLane> getShipLaneList() {
        return shipLaneList;
    }

    public void setShipLaneList(List<ShipLane> shipLaneList) {
        this.shipLaneList = shipLaneList;
    }

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="ship_flight_id",referencedColumnName="id",insertable = false, updatable = false)
    @NotFound(action=NotFoundAction.IGNORE)
    private List<ShipLane> shipLaneList;

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
        return "ShipFlight{" +
                "id='" + id + '\'' +
                ", carrierCh='" + carrierCh + '\'' +
                ", carrierEn='" + carrierEn + '\'' +
                ", shipNameCh='" + shipNameCh + '\'' +
                ", shipNameEn='" + shipNameEn + '\'' +
                ", shipNo='" + shipNo + '\'' +
                ", sailDate=" + sailDate +
                ", planArriveTime=" + planArriveTime +
                ", startingPort='" + startingPort + '\'' +
                ", checkTime=" + checkTime +
                '}';
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
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
}
