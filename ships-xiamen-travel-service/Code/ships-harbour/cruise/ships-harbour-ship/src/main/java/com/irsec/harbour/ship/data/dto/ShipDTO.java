package com.irsec.harbour.ship.data.dto;

import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用于船舶的增加和修改
 */
@Data
public class ShipDTO {

    @NotBlank(message = "船舶记录id不能为空", groups = {ValidatedGroup2.class})
    private String id;


    @NotBlank(message = "船舶中文名不能为空", groups = {ValidatedGroup1.class})
    private String shipNameZh;
    @NotBlank(message = "船舶英文名不能为空", groups = {ValidatedGroup1.class})
    private String shipNameEn;
    @NotBlank(message = "船舶类型不能为空", groups = {ValidatedGroup1.class})
    private String shipType;
    @NotBlank(message = "船舶呼号不能为空", groups = {ValidatedGroup1.class})
    private String shipCallSign;
    @NotNull(message = "船舶长度不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "船舶长度不能小于0", groups = {ValidatedGroup3.class})
    private Float shipLength;
    @NotNull(message = "船舶宽度不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "船舶宽度不能小于0", groups = {ValidatedGroup3.class})
    private Float shipWidth;
    @NotNull(message = "船舶高度不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "船舶高度不能小于0", groups = {ValidatedGroup3.class})
    private Float shipHeight;
    @NotNull(message = "船总吨不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "船总吨不能小于0", groups = {ValidatedGroup3.class})
    private Float shipGRT;
    @NotNull(message = "船净吨不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "船净吨不能小于0", groups = {ValidatedGroup3.class})
    private Float shipNRT;
    @NotNull(message = "船载重吨不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "船载重吨不能小于0", groups = {ValidatedGroup3.class})
    private Float shipDWT;
    @NotNull(message = "船最大吃水不能为空", groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "船最大吃水不能小于0", groups = {ValidatedGroup3.class} )
    private Float shipExtremeDraft;

    private String comment;

    private String createUser;
    private String createTime;

    @NotBlank(message = "承运人id不能为空", groups = {ValidatedGroup1.class})
    private String carrierId;
    @NotBlank(message = "承运人名称不能为空", groups = {ValidatedGroup1.class})
    private String carrierCH;
    @NotBlank(message = "IMO号不能为空", groups = {ValidatedGroup1.class})
    private String shipIMO;
}
