package com.irsec.harbour.ship.data.entity;

import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/8/29 09:55
 * @Description:  行李条验证记录表，记录每次行李条的验证情况
 */
@Entity
@Table(name = "ship_luggageCheckin")
@Data
public class ShipLuggageCheckin {
    @Id
    @Column(length = 32)
    private String id;

    @Column(name = "passenger_id", length = 32, nullable = true)
    private String passengerId;

    @Column(name = "luggage_id",length = 32, nullable = true)
    private String luggageId;

    @Column(name = "luggage_code", nullable = true)
    private String  luggageCode;

    //0表示验证通过，1表示验证失败
    @Column(name = "verify_result", nullable = true)
    private Integer verifyResult;

    @Column(name = "verify_time")
    private Date verifyTime;

    @Column(name = "device_no", nullable = true)
    private String deviceNo;

    @Column(name = "flight_id" , nullable = true)
    private String flightId;

    //0表示在线，1表示离线
    @Column(name = "operation_type", nullable = true)
    private Integer operationType;
    /**
     * 核销
     * 0：未核销
     * 1：已核销
     */
    @Column(name = "is_cancel", nullable = true)
    private Integer isCancel = 0;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    @org.hibernate.annotations.UpdateTimestamp
    private Date updateTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="passenger_id",insertable = false, updatable = false)
    @NotFound(action=NotFoundAction.IGNORE)
    private ShipPassenger shipPassenger;
}
