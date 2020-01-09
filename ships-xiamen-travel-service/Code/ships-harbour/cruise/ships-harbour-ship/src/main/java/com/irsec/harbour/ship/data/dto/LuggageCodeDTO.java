package com.irsec.harbour.ship.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @Auther: Jethro
 * @Date: 2019/8/31 10:12
 * @Description: 旅客凭证打印查询接口需要返回的行李条信息结构
 */
@Data
public class LuggageCodeDTO {

    private String id;
    private String luggageCode;
    private int isPrint;
}
