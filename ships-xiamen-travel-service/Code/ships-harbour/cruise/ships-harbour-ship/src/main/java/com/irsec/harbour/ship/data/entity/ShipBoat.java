package com.irsec.harbour.ship.data.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/8/16 17:46
 * @Description: 数据库船舶表
 */

@Entity
@Table(name = "ship_boat" )
@Data
@DynamicUpdate  //动态更新，只更新已有数据的字段
@EntityListeners(AuditingEntityListener.class)
public class ShipBoat {
    @Id
    @Column(name = "id",length = 32)
    private String id;
    @Column(name = "create_time",nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time",nullable = true)
    @org.hibernate.annotations.UpdateTimestamp
    private Date updateTime;

    @Column(name = "create_user",length =32, nullable = true)
    private String createUser;
    @Column(name = "ship_zh_name",length = 32, nullable = true)
    private String shipZhName;
    @Column(name = "ship_en_name",length = 32, nullable = true)
    private String shipEnName;
    @Column(name = "ship_type", length = 32,nullable = true)
    private String shipType;
    @Column(name = "ship_call_sign",length = 32,nullable = true)
    private String shipCallSign;
    @Column(name = "ship_length",nullable = true)
    private Float shipLength;
    @Column(name = "ship_height",nullable = true)
    private Float shipHeight;
    @Column(name = "ship_width",nullable = true)
    private Float shipWidth;
    @Column(name = "ship_grt",nullable = true)
    private Float shipGrt;
    @Column(name = "ship_nrt",nullable = true)
    private Float shipNrt;
    @Column(name = "ship_extreme_draft",nullable = true)
    private Float shipExtremeDraft;
    @Column(name = "ship_dwt", nullable = true)
    private Float shipDwt;
    @Column(name = "ship_comment",length = 128, nullable = true)
    private String shipComment;

    @Column(name = "carrier_ch",length = 32, nullable = true)
    private String carrierCH;
    @Column(name = "carrier_id",length = 32, nullable = true)
    private String carrierId;
    @Column(name = "ship_imo",length = 32, nullable = true)
    private String shipIMO;
}
