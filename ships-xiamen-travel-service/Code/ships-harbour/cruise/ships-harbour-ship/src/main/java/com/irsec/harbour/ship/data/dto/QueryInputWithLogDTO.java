package com.irsec.harbour.ship.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.irsec.harbour.ship.data.group.ValidatedGroupLog;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 14:17
 * @Description:带有额外的日志数据，用于条件查询
 */
@Validated
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryInputWithLogDTO<T> extends PageInputDTO {
    public T getCondition() {
        return condition;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }
    @Valid
    @NotNull(message = "condition字段不能为空", groups = {ValidatedGroupLog.class})
    private T condition;
    @Valid
    LogDTO opt;

    public LogDTO getOpt() {
        return opt;
    }

    public void setOpt(LogDTO opt) {
        this.opt = opt;
    }
}
