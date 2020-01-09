package com.irsec.harbour.ship.data.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther: Jethro
 * @Date: 2019/8/30 16:04
 * @Description: 游客类型
 */
public enum TouristTypeEnum {
    TOURIST_TYPE_SINGLE(0,"个人"),
    TOURIST_TYPE_GROUP(1,"团体");

    @Getter
    @Setter
    private int typeInt;

    @Getter
    @Setter
    private String typeZh;

    private TouristTypeEnum(int typeInt,String typeZh){
        this.typeInt = typeInt;
        this.typeZh = typeZh;
    }


    public static int getTouristTypeInt(String typezh){
        for(TouristTypeEnum c : TouristTypeEnum.values()){
            if(c.typeZh.compareTo(typezh) == 0){
                return c.typeInt;
            }
        }
        return -1;
    }


    public static String getTouristTypeZh(int typeInt){
        for(TouristTypeEnum c : TouristTypeEnum.values()){
            if(c.typeInt == typeInt){
                return c.typeZh;
            }
        }
        return null;
    }
}
