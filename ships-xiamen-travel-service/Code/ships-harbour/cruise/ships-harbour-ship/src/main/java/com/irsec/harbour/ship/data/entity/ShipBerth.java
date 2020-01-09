package com.irsec.harbour.ship.data.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ship_berth" )
@Data
@DynamicUpdate  //动态更新，只更新已有数据的字段
@EntityListeners(AuditingEntityListener.class)
public class ShipBerth {
    @Id
    @Column(name = "id",length = 32)
    private String id;

    @Column(name = "create_user",length =32, nullable = true)
    private String createUser;
    @Column(name = "berth_name",length = 16,nullable = true)
    private String berthName;
    @Column(name = "berth_weight",length =32,nullable = true)
    private String berthWeight;
    @Column(name = "berth_length",nullable = true)
    private Float berthLength;
    @Column(name = "berth_comment",length = 128,nullable = true)
    private String berthComment;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time",nullable = true)
    @org.hibernate.annotations.UpdateTimestamp
    private Date updateTime;

    public ShipBerth(){
        super();
    }

    public ShipBerth(String id, String berthName){
        this.id = id;
        this.berthName = berthName;
    }


}
