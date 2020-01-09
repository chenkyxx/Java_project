package com.irsec.harbour.ship.data.dto;

import com.irsec.harbour.ship.data.group.ValidatedGroupLog;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @Auther: Jethro
 * @Date: 2019/8/19 10:25
 * @Description: 用户操作日志
 */
@Data
public class LogDTO {
    @NotBlank(message = "用户id不能为空。",groups = {ValidatedGroupLog.class})
    private String optUserId;
    @NotBlank(message = "用户名不能为空。",groups = {ValidatedGroupLog.class})
    private String optUserName;
}
