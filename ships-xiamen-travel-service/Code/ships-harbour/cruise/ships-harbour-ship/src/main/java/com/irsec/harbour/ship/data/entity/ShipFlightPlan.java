package com.irsec.harbour.ship.data.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static javax.persistence.ConstraintMode.NO_CONSTRAINT;

/**
 * @Auther: Jethro
 * @Date: 2019/8/16 17:53
 * @Description:数据库：靠离泊计划表
 */

@Entity
@Table(name = "ship_flight_plan" )
@Data
@DynamicUpdate  //动态更新，只更新已有数据的字段
@EntityListeners(AuditingEntityListener.class)
public class ShipFlightPlan {
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

    @Column(name = "ber_id",length =32, nullable = true)
    private String berId;
    @Column(name = "ship_id",length =32, nullable = true)
    private String shipId;
    @Column(name = "port_type",length =32,nullable = true)
    private String portType;
    @Column(name = "create_user",length =32, nullable = true)
    private String createUser;
    @Column(name = "ship_agent",length =32,nullable = true)
    private String shipAgent;
    @Column(name = "is_lead",nullable = true)
    private Integer isLead;
    @Column(name = "plan_arrive_time",nullable = true)
    private Date planArriveTime;
    @Column(name = "plan_depart_time",nullable = true)
    private Date planDepartTime;
    @Column(name = "capacity",nullable = true)
    private Integer capacity;

    /**
     * 0 : 已申请
     * 1 : 待作业/待计划
     * 2 : 已驳回
     * 3 : 待反馈
     * 4 : 已反馈
     */
    @Column(name = "plan_status",nullable = true)
    private Integer planStatus;
    @Column(name = "pre_port",length =32, nullable = true)
    private String prePort;
    @Column(name = "next_port",length =32, nullable = true)
    private String nextPort;
    @Column(name = "plan_pass_time",nullable = true)
    private Date planPassTime;
    @Column(name = "plan_close_time",nullable = true)
    private Date planCloseTime;

    /**
     * 船舷（0：左舷，1：右舷）
     */
    @Column(name = "side",nullable = true)
    private Integer side;

    //计划抵港时间
    @Column(name = "plan_arrive_port_time",nullable = true)
    private Date planArrivePortTime;

/*    @Column(name = "plan_pilotage_time",nullable = true)
    private Date planPilotageTime;
    @Column(name = "pilotage_hour",nullable = true)
    private Float pilotageHour;*/


    @Column(name = "actual_arrive_time",nullable = true)
    private Date actualArriveTime;
    @Column(name = "actual_depart_time",nullable = true)
    private Date actualDepartTime;
    @Column(name = "actual_pass_time",nullable = true)
    private Date actualPassTime;
    @Column(name = "actual_close_time",nullable = true)
    private Date actualCloseTime;
/*    @Column(name = "pass_number",nullable = true)
    private Integer passNumber;
    @Column(name = "luggage_number",nullable = true)
    private Integer luggageNumber;*/


    @Column(name = "inbound_number",nullable = true)
    private Integer inboundNumber;
    @Column(name = "outbound_number",nullable = true)
    private Integer outboundNumber;
    @Column(name = "inbound_luggage",nullable = true)
    private Integer inboundLuggage;
    @Column(name = "outbound_luggage",nullable = true)
    private Integer outboundLuggage;



    //离境通关时间
    @Column
    private Date leavePassTime;

    //离境截关时间
    @Column
    private Date leaveCloseTime;

    //垃圾（立方米）
    @Column
    private Float garbageNum;

    //加水（吨)
    @Column
    private Float addWaterNum;


    //吊车（辆）
    @Column
    private Integer craneNum;

    //叉车（辆）
    @Column
    private Integer forkliftNum;


    //辅工（人)
    @Column
    private Integer helpWorkerNum;


    //船员（人）
    @Column
    private Integer sailorNum;



    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="flight_id",referencedColumnName="id",insertable = false, updatable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
    @NotFound(action=NotFoundAction.IGNORE)
    private List<ShipLane> shipLaneList;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ship_id",referencedColumnName = "id",insertable = false, updatable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
    @NotFound(action=NotFoundAction.IGNORE)
    private ShipBoat shipBoat;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ber_id",referencedColumnName = "id",insertable = false, updatable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
    @NotFound(action=NotFoundAction.IGNORE)
    private ShipBerth shipBerth;
}
