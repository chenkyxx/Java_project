package com.irsec.harbour.ship.data.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ManyInputDTO<T> extends BaseInputDTO {
    @Valid
    @NotNull(message = "datas 不能为null")
    private List<T> datas;


    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }
}
