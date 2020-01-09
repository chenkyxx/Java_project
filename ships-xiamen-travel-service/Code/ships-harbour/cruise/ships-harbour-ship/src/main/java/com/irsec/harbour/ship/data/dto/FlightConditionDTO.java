package com.irsec.harbour.ship.data.dto;

import java.util.Date;

public class FlightConditionDTO {
    private String carrier;
    private String shipName;
    private String shipNo;
    private Date sailDateSt;
    private Date sailDateEnd;

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShipNo() {
        return shipNo;
    }

    public void setShipNo(String shipNo) {
        this.shipNo = shipNo;
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
}
