package com.irsec.harbour.ship.data.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneInputDTO<T> extends BaseInputDTO {
    @Valid
    @NotNull(message = "data 不能为空")
    T data;


    @Override
    public String toString() {
        return "OneInputDTO{" +
                "data=" + data +
                '}';
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
