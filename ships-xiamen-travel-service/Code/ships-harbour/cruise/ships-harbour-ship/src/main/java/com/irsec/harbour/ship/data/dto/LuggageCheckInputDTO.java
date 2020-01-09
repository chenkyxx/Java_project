package com.irsec.harbour.ship.data.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Auther: Jethro
 * @Date: 2019/8/29 16:51
 * @Description:
 */
@Data
public class LuggageCheckInputDTO {

    @NotBlank(message = "checkId 不能为空")
    private String checkinId;
    @NotBlank(message = "luggageCode 不能为空")
    private String luggageCode;
    @NotNull(message = "checkingTime 不能为空")
    private Date checkingTime;
    @NotBlank(message = "checkDeviceNo 不能为空")
    private String checkDeviceNo;
}
