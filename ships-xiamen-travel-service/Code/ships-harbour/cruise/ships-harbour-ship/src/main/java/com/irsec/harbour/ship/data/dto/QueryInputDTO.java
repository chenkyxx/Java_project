package com.irsec.harbour.ship.data.dto;


public class QueryInputDTO<T> extends PageInputDTO {

    public T getCondition() {
        return condition;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }

    private T condition;
}
