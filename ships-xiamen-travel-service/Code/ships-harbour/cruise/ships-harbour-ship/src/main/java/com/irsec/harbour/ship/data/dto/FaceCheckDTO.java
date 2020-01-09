package com.irsec.harbour.ship.data.dto;

import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/9/23 09:49
 * @Description: 用于人脸信息验证的数据结构
 */
@Data
public class FaceCheckDTO {

    @NotBlank(message = "现场照不能为空",groups = {ValidatedGroup1.class})
    private String livePhoto;
    @NotBlank(message = "证件照不能为空",groups = {ValidatedGroup1.class})
    private String certificatePhoto;
    @NotNull(message = "证件类型不能为空",groups = {ValidatedGroup1.class})
    private Integer certificateType;


    private String certificateName;
    private String certificateNameEN;
    private String certificateGender;
    private String certificateBirth;
    private String certificateNation;

    @NotBlank(message = "证件号不能为空",groups = {ValidatedGroup1.class})
    private String certificateId;
    @NotBlank(message = "设备编号不能为空",groups = {ValidatedGroup1.class})
    private String deviceNo;
    @NotNull(message = "验证时间不能为空",groups = {ValidatedGroup1.class})
    private Date checkTime;
    @NotNull(message = "比对结果不能为空",groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "比对结果的值有误", groups = {ValidatedGroup3.class})
    @Max(value = 1, message = "比对结果的值有误", groups = {ValidatedGroup3.class})
    private Integer verifyResult;


    @NotNull(message = "人工放行字段不能为空",groups = {ValidateGroupManual.class})
    @Min(value = 0, message = "人工放行字段的值有误", groups = {ValidateGroupManual.class})
    @Max(value = 1, message = "人工放行字段的值有误", groups = {ValidateGroupManual.class})
    private Integer manualPass;

    private Float score;
    @NotBlank(message = "记录id不能为空",groups = {ValidateGroupCancel.class})
    private String Id;
    @NotNull(message = "type不能为空",groups = {ValidateGroupCancel.class})
    @Min(value = 0, message = "type字段的值有误", groups = {ValidateGroupCancel.class})
    @Max(value = 1, message = "type字段的值有误", groups = {ValidateGroupCancel.class})
    private Integer type;
    public interface ValidateGroupManual{}
    public interface ValidateGroupCancel{}
}
