package com.irsec.harbour.ship.data.dto;


import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public class BaseInputDTO {

    @NotBlank(message = "reqId 不能为空")
    private String reqId;


    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }
}
