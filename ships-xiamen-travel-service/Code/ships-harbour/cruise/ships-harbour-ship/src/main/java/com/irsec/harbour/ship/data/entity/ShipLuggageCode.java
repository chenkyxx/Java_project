package com.irsec.harbour.ship.data.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/8/30 16:01
 * @Description: 行李条二维码表。新增旅客时生成三条行李条形码
 */
@Entity
@Table(name = "ship_luggage_code")
@Data
public class ShipLuggageCode {


    @Id
    @Column
    private String id;
    @Column(name = "passenger_id",nullable = true)
    private String passengerId;
    @Column(name = "luggage_code",nullable = true)
    private String luggageCode;
    @Column(name = "print_time",nullable = true)
    private Date printTime;
    @Column(name = "is_print",nullable = true)
    private Integer isPrint=0;// 0：未打印，1：已打印


    @Column(name = "create_time",nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time",nullable = true)
    @org.hibernate.annotations.UpdateTimestamp
    private Date updateTime;
}
