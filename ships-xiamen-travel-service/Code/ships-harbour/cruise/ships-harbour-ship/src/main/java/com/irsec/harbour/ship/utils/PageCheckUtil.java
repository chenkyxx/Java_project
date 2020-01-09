package com.irsec.harbour.ship.utils;

import com.irsec.harbour.ship.data.dto.PageInputDTO;

public class PageCheckUtil {
    public static String checkPageQueryParams(PageInputDTO pageRequestParams) {

        if (pageRequestParams.getPageSize() > 100) {
            return "pageSize不能大于100";
        }

        if (pageRequestParams.getPageSize() <= 0) {
            return "pageSize不能小于等于0";
        }

        if (pageRequestParams.getPageIndex() < 0) {
            return "pageIndex不能小于0";
        }

        return null;

    }
}
