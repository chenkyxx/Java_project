package com.irsec.harbour.ship.data.dto;

import com.irsec.harbour.ship.data.entity.ShipLane;
import com.irsec.harbour.ship.data.entity.ShipFlight;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.UUIDTool;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShipFlightDTO {


    @NotBlank(message = "id不能为空", groups = {ValidatedGroup2.class})
    private String id;

    @NotBlank(message = "承运人中文名称不能为空", groups = {ValidatedGroup1.class,ValidatedGroup2.class})
    private String carrierCh;

    private String carrierEn;
    @NotBlank(message = "邮轮中文名不能为空", groups = {ValidatedGroup1.class,ValidatedGroup2.class})
    private String shipNameCh;

    private String shipNameEn;
    @NotBlank(message = "航班号不能为空", groups = {ValidatedGroup1.class,ValidatedGroup2.class})
    private String shipNo;

    @NotNull(message = "启航日期不能为空", groups = {ValidatedGroup1.class,ValidatedGroup2.class})
    private Date sailDate;

    private String planArriveTime;
    @NotBlank(message = "始发港不能为空", groups = {ValidatedGroup1.class,ValidatedGroup2.class})
    private String startingPort;
    @Valid
    @NotNull(message = "航线不能为空", groups = {ValidatedGroup1.class,ValidatedGroup2.class})
    @Size(min = 1,message = "航线至少需要一个", groups = {ValidatedGroup1.class,ValidatedGroup2.class})
    private List<LaneDTO> routes;

    @NotBlank(message = "靠离泊计划id不能为空", groups = {ValidatedGroup1.class,ValidatedGroup2.class})
    private String flightPlanId;

    @NotNull(message = "是否修改航线的值不能为空", groups = {ValidatedGroup2.class})
    @Min(value = 0,message = "是否修改航线的值不能小于0", groups = {ValidatedGroup3.class})
    @Max(value = 1, message = "是否修改航线的值不能大于1", groups = {ValidatedGroup3.class})
    private Integer isChangedLane;

    private Date checkTime;

    public ShipFlight convertTo() {
        ShipFlight shipFlight = new ShipFlight();
        if(StringUtils.isEmpty(this.id)){
            this.id = UUIDTool.newUUID();
        }
        BeanUtils.copyProperties(this, shipFlight);
        shipFlight.setPlanArriveTime(this.planArriveTime);
        List<ShipLane> list = new ArrayList<>();
        for(LaneDTO laneDTO : this.routes){
            ShipLane shipLane = new ShipLane();
            BeanUtils.copyProperties(laneDTO, shipLane);
            shipLane.setId(UUIDTool.newUUID());
            shipLane.setShipFlightId(this.id);
            shipLane.setArriveTime(DateUtil.strToDate(laneDTO.getPlanArriveTime(),"yyyy-MM-dd HH:mm"));
            list.add(shipLane);
        }
        shipFlight.setShipLaneList(list);
        return shipFlight;
    }

    public ShipFlightDTO convertFrom(ShipFlight shipFlight) {
        BeanUtils.copyProperties(shipFlight, this);
        List<LaneDTO> list = new ArrayList<>();
        for(ShipLane shipLane : shipFlight.getShipLaneList()){
            LaneDTO laneDTO = new LaneDTO();
            BeanUtils.copyProperties(shipLane,laneDTO);
            list.add(laneDTO);
        }
        this.setRoutes(list);
        return this;
    }


    @Override
    public String toString() {
        return "ShipFlightDTO{" +
                "id='" + id + '\'' +
                ", carrierCh='" + carrierCh + '\'' +
                ", carrierEn='" + carrierEn + '\'' +
                ", shipNameCh='" + shipNameCh + '\'' +
                ", shipNameEn='" + shipNameEn + '\'' +
                ", shipNo='" + shipNo + '\'' +
                ", sailDate=" + sailDate +
                ", planArriveTime=" + planArriveTime +
                ", startingPort='" + startingPort + '\'' +
                ", route='" + routes + '\'' +
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

    public List<LaneDTO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<LaneDTO> routes) {
        this.routes = routes;
    }

    public String getFlightPlanId() {
        return flightPlanId;
    }

    public void setFlightPlanId(String flightPlanId) {
        this.flightPlanId = flightPlanId;
    }

    public Integer getIsChangedLane() {
        return isChangedLane;
    }

    public void setIsChangedLane(Integer isChangedLane) {
        this.isChangedLane = isChangedLane;
    }
}
