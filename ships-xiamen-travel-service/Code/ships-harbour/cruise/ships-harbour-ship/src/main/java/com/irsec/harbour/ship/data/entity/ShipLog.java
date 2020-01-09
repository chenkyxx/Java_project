package com.irsec.harbour.ship.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 10:17
 * @Description: 邮轮出行平台：日志表
 */
@Entity
@Table(name = "ship_log" )
@Data
public class ShipLog {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "create_time",nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time",nullable = true)
    @org.hibernate.annotations.UpdateTimestamp
    private Date updateTime;

    @Column(name = "opt_user_id",length =32, nullable = true)
    private String optUserId;
    @Column(name = "opt_user_name",length =32, nullable = true)
    private String optUserName;
    @Column(name = "opt_type", nullable = true)
    private Integer optType;
    @Column(name = "device",length =16, nullable = true)
    private String device;
    @Column(name = "ip_address",length =16, nullable = true)
    private String ipAddress;
    @Column(name = "opt_content", nullable = true)
    private String optContent;
}
