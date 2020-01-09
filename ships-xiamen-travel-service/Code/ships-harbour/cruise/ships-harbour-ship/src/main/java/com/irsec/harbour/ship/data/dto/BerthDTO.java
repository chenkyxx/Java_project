package com.irsec.harbour.ship.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 09:28
 * @Description: 用于泊位的增加修改和查看
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BerthDTO {

    @NotBlank(message = "泊位ID不能为空" ,groups = {ValidatedGroup2.class})
    private String id;
    @NotBlank(message = "泊位名称不能为空" ,groups = {ValidatedGroup1.class})
    private String berthName;
    @NotBlank(message = "泊位吨级不能为空" ,groups = {ValidatedGroup1.class})
    private String berthWeight;


    @NotNull(message = "泊位长度不能为空" ,groups = {ValidatedGroup1.class})
    @Min(value = 0, message = "泊位长度必须大于0",groups = {ValidatedGroup3.class})
    private Float berthLength;

    private String comment;

    private String createUser;

    private String createTime;
}
