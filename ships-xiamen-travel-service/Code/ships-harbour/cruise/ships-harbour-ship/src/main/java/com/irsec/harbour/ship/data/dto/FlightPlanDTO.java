package com.irsec.harbour.ship.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 09:46
 * @Description: 靠离泊计划相关接口的参数
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightPlanDTO {

    @NotBlank(message = "靠离泊计划id不能为空", groups = {ValidatedGroup2.class})
    private String id;

    @NotBlank(message = "船舶id不能为空", groups = {ValidatedGroup1.class})
    private String shipId;
    @NotBlank(message = "港口类型不能为空", groups = {ValidatedGroup1.class})
    private String portType;
    @NotBlank(message = "船舶代理不能为空", groups = {ValidatedGroup1.class})
    private String shipAgentType;
    @NotNull(message = "是否引航的值不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "是否引航的值有误", groups = {ValidatedGroup3.class})
    @Max(value = 1, message = "是否引航的值有误", groups = {ValidatedGroup3.class})
    private Integer isLead;
    @NotBlank(message = "计划靠泊时间不能为空", groups = {ValidatedGroup1.class})
    private String planArriveTime;
    @NotBlank(message = "计划离泊时间不能为空", groups = {ValidatedGroup1.class})
    private String planDepartTime;
    @NotBlank(message = "泊位id不能为空", groups = {ValidatedGroup1.class})
    private String berthId;
    @NotNull(message = "载客人数不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "载客人数不能小于0", groups = {ValidatedGroup3.class})
    private Integer capacity;
    @NotBlank(message = "上一港不能为空", groups = {ValidatedGroup1.class})
    private String prePort;
    @NotBlank(message = "下一港不能为空", groups = {ValidatedGroup1.class})
    private String nextPort;

    @Valid
    private List<LaneDTO> lane;

    private Integer jobStatus;
    private Integer laneChanged = 1;


    @NotBlank(message = "计划通关时间不能为空")
    private String planPassTime;
    @NotBlank(message = "计划截关时间不能为空")
    private String planCloseTime;
/*    @NotBlank(message = "计划引水时间不能为空", groups = {ValidatedSubmitPlan.class})
    private String planPilotageTime;
    @NotNull(message = "引水时长不能为空", groups = {ValidatedSubmitPlan.class})
    @Min(value = 0, groups = {ValidatedSubmitPlan.class})
    private Float pilotageHour;*/


    //作业反馈(作业完成)新增加新增加 8字段

    //离境通关时间 yyyyMMddHHmmss
    private String leavePassTime;

    //离境截关时间 yyyyMMddHHmmss
    private String leaveCloseTime;

    //垃圾（立方米）
    private Float garbageNum;
    //加水（吨)
    private Float addWaterNum;
    //吊车（辆）
    private Integer craneNum;
    //叉车（辆）
    private Integer forkliftNum;
    //辅工（人)
    private Integer helpWorkerNum;
    //船员（人）
    private Integer sailorNum;
    //作业反馈新增加 8字段

    @NotBlank(message = "实际靠泊时间不能为空")
    private String actualArriveTime;
    @NotBlank(message = "实际离泊时间不能为空")
    private String actualDepartTime;
    @NotBlank(message = "实际通关时间不能为空")
    private String actualPassTime;
    @NotBlank(message = "实际截关时间不能为空")
    private String actualCloseTime;

    @NotNull(message = "入境人数不能为空")
    @Min(value = 0, groups = ValidatedGroup3.class, message = "入境人数不能小于0")
    private Integer inboundNumber;
    @NotNull(message = "出境人数不能为空")
    @Min(value = 0, groups = ValidatedGroup3.class, message = "出境人数不能小于0")
    private Integer outboundNumber;
    @NotNull(message = "入境行李数不能为空")
    @Min(value = 0, groups = ValidatedGroup3.class, message = "入境行李数不能小于0")
    private Integer inboundLuggage;
    @NotNull(message = "出境行李数不能为空")
    @Min(value = 0, groups = ValidatedGroup3.class, message = "出境行李数不能小于0")
    private Integer outboundLuggage;

    /**
     * 船舷（0：左舷，1：右舷）
     */
    @NotNull(message = "船舷的值不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, groups = ValidatedGroup3.class, message = "船舷的值有误")
    @Max(value = 1, groups = ValidatedGroup3.class, message = "船舷的值有误")
    private Integer side;

    //计划抵港时间
    @NotBlank(message = "计划抵港时间不能为空", groups = {ValidatedGroup1.class})
    private String planArrivePortTime;

    public interface ValidatedSubmitPlan {
    }

    public interface ValidatedFeedBack {
    }
}
