package com.irsec.harbour.ship.data.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Auther: Jethro
 * @Date: 2019/9/9 09:44
 * @Description:
 */
@Data
public class PrintStatusDTO {
    @NotBlank(message = "条码不能为空",groups = {ValidatedGroupPrintStatus.class})
    private String code;
    @NotNull(message = "打印类型不能为空" ,groups = {ValidatedGroupPrintStatus.class})
    @Min(value = 0,message = "打印类型的值有误",groups = {ValidatedGroupPrintStatus.class})
    @Max(value = 1,message = "打印类型的值有误",groups = {ValidatedGroupPrintStatus.class})
    private Integer printType;
    @NotBlank(message = "乘客id不能为空",groups = {ValidatedGroupPrintStatus.class})
    private String passengerId;
    public interface ValidatedGroupPrintStatus{}
}
