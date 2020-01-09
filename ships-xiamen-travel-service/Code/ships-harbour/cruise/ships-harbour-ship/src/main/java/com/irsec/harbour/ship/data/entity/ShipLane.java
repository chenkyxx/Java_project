package com.irsec.harbour.ship.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.irsec.harbour.ship.data.dto.LaneDTO;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/8/16 17:10
 * @Description: 数据库：航线表
 */

@Entity
@Table(name = "ship_lane" )
@Data
@DynamicUpdate  //动态更新，只更新已有数据的字段
@EntityListeners(AuditingEntityListener.class)
public class ShipLane {
    @Id
    @Column(name = "id",length = 32)
    @JsonIgnore
    private String id;
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
    //靠离泊计划id
    @Column(name = "flight_id",length =32, nullable = true)
    @JsonIgnore
    private String flightId;
    //航班id
    @Column(name = "ship_flight_id",length =32, nullable = true)
    @JsonIgnore
    private String shipFlightId;

    @Column(name = "port_type", nullable = true)
    private String portType;
    @Column(name = "place",length =16, nullable = true)
    private String place;
    @Column(name = "arrive_time",nullable = true)
    private Date arriveTime;

    @Column(name = "orders",nullable = true)
    private Integer order;

    @Column(name = "place_code",nullable = true)
    private String placeCode;

//
//    public LaneDTO coverToLaneDTO(){
//        LaneDTO laneDTO = new LaneDTO();
//    }
}
