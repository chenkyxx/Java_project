package com.irsec.harbour.ship.data.dto;

public class PageInputDTO extends BaseInputDTO {

    private int pageSize = 10;
    private int pageIndex = 0;


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

}
