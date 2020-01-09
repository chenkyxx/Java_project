package com.irsec.harbour.ship.data.dto;

import java.util.List;

public class RowsOutputDTO extends BaseOutputDTO {

    private List rows;

    private int total;

    private int subtotal;


    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }
}
