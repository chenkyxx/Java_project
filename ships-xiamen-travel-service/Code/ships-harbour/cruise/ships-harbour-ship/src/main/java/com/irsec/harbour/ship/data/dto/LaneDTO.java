package com.irsec.harbour.ship.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 09:50
 * @Description: 航线的相关参数
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY )
public class LaneDTO {

    @NotBlank(message = "港口类型不能为空",groups = {ValidatedGroupFlightPlan.class})
    private String portType;


    private String placeCode;

    @NotBlank(message = "航线地点名称不能为空",groups = {ValidatedGroup1.class, ValidatedGroup2.class})
    private String place;
    @NotBlank(message = "航线到达时间不能为空",groups = {ValidatedGroupFlight.class})
    @Pattern(regexp =  "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}",message = "航线到达时间格式为：yyyy-MM-dd HH:mm", groups = {ValidatedGroupFlight.class,ValidatedGroup3.class})
    private String planArriveTime;

    @Min(value = 0,message = "航线顺序号不能小于0",groups = {ValidatedGroup1.class, ValidatedGroup2.class})
    @NotNull(message = "航线顺序号不能为空",groups = {ValidatedGroup1.class, ValidatedGroup2.class})
    private Integer order;


    public interface  ValidatedGroupFlight{}
    public interface  ValidatedGroupFlightPlan{}
}
