package com.irsec.harbour.ship.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.irsec.harbour.ship.data.group.ValidatedGroupLog;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 14:08
 * @Description:
 */

@Validated
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneInputWithLogDTO<T> extends BaseInputDTO {
    @Valid
    T data;
    @Valid
    @NotNull(message = "opt数据不能为空",groups = {ValidatedGroupLog.class})
    LogDTO opt;

    @Override
    public String toString() {
        return "OneInputDTO{" +
                "data=" + data +
                "logOpt="+opt+
                '}';
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LogDTO getOpt() {
        return opt;
    }

    public void setOpt(LogDTO opt) {
        this.opt = opt;
    }
}
