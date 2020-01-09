package com.irsec.harbour.ship.data.dto;

public class TicketCheckingOutputDTO extends RowOutputDTO {

    private int verifyResult;

    private int verifyTotal;

    public int getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(int verifyResult) {
        this.verifyResult = verifyResult;
    }

    public int getVerifyTotal() {
        return verifyTotal;
    }

    public void setVerifyTotal(int verifyTotal) {
        this.verifyTotal = verifyTotal;
    }
}
