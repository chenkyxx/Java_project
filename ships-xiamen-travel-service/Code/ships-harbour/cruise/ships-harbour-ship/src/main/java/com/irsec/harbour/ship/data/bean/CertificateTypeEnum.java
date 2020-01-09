package com.irsec.harbour.ship.data.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * @Auther: Jethro
 * @Date: 2019/8/30 16:12
 * @Description: 证件类型
 */

/**
 * 代码	名称
 * 11	外交护照
 * 12	公务护照
 * 13	因公普通护照
 * 14	普通护照
 * 15	中华人民共和国旅行证
 * 16	台湾居民来往大陆通行证
 * 17	海员证
 * 18	机组人员证
 * 19	铁路员工证
 * 20	中华人民共和国人出境通行证
 * 21	往来港澳通行证
 * 23	前往港澳通行证
 * 24	港澳同胞回乡证(港澳居民来往内地通行证)
 * 25	大陆居民往来台湾通行证
 * 27	往来香港澳门特别行政区通行证
 * 28	华侨回国定居证
 * 29	台湾同胞定居证
 * 30	外国人出人境证
 * 31	外国人旅行证
 * 32	外国人居留证
 * 33	外国人临时居留证
 * 35	人籍证书
 * 36	出籍证书
 * 37	复籍证书
 * 38	暂住证
 * 40	出海渔船民证
 * 41	临时出海渔船民证
 * 42	出海船舶户口簿
 * 43	出海船舶户口证
 * 44	粤港澳流动渔民证
 * 45	粤港澳临时流动渔民证
 * 46	粤港澳流动渔船户口簿
 * 47	搭靠台轮许可证
 * 48	劳务人员登轮作业证
 * 49	台湾居民登陆证
 * 50	贸易证
 * 60	边境通行证.
 * 61	深圳市过境耕作证
 * 70	香港特别行政区护照
 * 71	澳门特别行政区护照
 * 81	缅甸中方(缅方)通行证
 * 82	云南边境地区境外边民人出境证
 * 90	中朝边境地区出入境通行证
 * 91	朝中边境地区居民过境通行证
 * 92	鸭绿江、图们江水文作业证
 * 93	中朝流筏固定代表证
 * 94	中朝(朝中)鸭绿江、图们江航行船舶船员证
 * 95	中朝(朝中)边境地区公安总代表证
 * 96	中朝(朝中)边境地区公安副总代表证
 * 97	中朝(朝中)边境地区公安代表证
 * 99	其他证件
 */
public enum CertificateTypeEnum {

    CERTIFICATE_TYPE_ID_CARD(0,"身份证"),
    CERTIFICATE_TYPE_DIPLOMATIC_PASSPORT(11,"外交护照"),
    CERTIFICATE_TYPE_OFFICIAL_PASSPORT(12,"公务护照"),
    CERTIFICATE_TYPE_ORDINARY_PASSPORT_ON_OFFICIAL(13,"因公普通护照"),
    CERTIFICATE_TYPE_ORDINARY_PASSPORT(14,"普通护照"),
    CERTIFICATE_TYPE_TRAVEL_OF_CHINA(15,"中华人民共和国旅行证"),
    CERTIFICATE_TYPE_TAIWAN_RESIDENTS_PASS_AND_FROM_MAINLAND(16,"台湾居民来往大陆通行证"),
    CERTIFICATE_TYPE_SEAMAN(17,"海员证"),
    CERTIFICATE_TYPE_CREW(18,"机组人员证"),
    CERTIFICATE_TYPE_RAILWAY_EMPLOYEE(19,"铁路员工证"),
    CERTIFICATE_TYPE_EXIT_AND_PASS_OF_CHINA(20,"中华人民共和国人出境通行证"),
    CERTIFICATE_TYPE_PASS_AND_FORM_HK_AND_MACAO(21,"往来港澳通行证"),
    CERTIFICATE_TYPE_PASS_TO_HK_AND_MACAO(23,"前往港澳通行证"),
    CERTIFICATE_TYPE_HK_PASS_AND_FRON_TO_MAINLAND(24,"港澳居民来往内地通行证"),
    CERTIFICATE_TYPE_CHINESE_PASS_AND_FROM_TAIWAN(25,"大陆居民往来台湾通行证"),
    CERTIFICATE_TYPE_PASS_AND_FORM_HK_AND_MC(27,"往来香港澳门特别行政区通行证"),
    CERTIFICATE_TYPE_RESIDENCE_OF_OVERSEAS_CHINESE(28,"华侨回国定居证"),
    CERTIFICATE_TYPE_RESIDENCE_OF_TAIWAN(29,"台湾同胞定居证"),
    CERTIFICATE_TYPE_PASS_AND_FROM_OF_FOREIGNER(30,"外国人出人境证"),
    CERTIFICATE_TYPE_TRAVEL_OF_FROEIGNER(31,"外国人旅行证"),
    CERTIFICATE_TYPE_RESIDENCE_OF_FOREIGNER(32,"外国人居留证"),
    CERTIFICATE_TYPE_TEMP_RESIDENCE_OF_FOREIGNER(33,"外国人临时居留证"),
    CERTIFICATE_TYPE_NATURALIZATION(35,"入籍证书"),
    CERTIFICATE_TYPE_EXPATRIATION(36,"出籍证书"),
    CERTIFICATE_TYPE_P(37,"复籍证书"),
    CERTIFICATE_TYPE_TEMP_RESIDENCE(38,"暂住证"),
    CERTIFICATE_TYPE_GO_TO_SEA(40,"出海渔船民证"),
    CERTIFICATE_TYPE_TEMP_GO_TO_SEA(41,"临时出海渔船民证"),
    CERTIFICATE_TYPE_BOAT_ACCOUNT_BOOK(42,"出海船舶户口簿"),
    CERTIFICATE_TYPE_BOAT_ACCOUNT(43,"出海船舶户口证"),
    CERTIFICATE_TYPE_MOVE_FISHERMEN_OF_YUE_HK_MC(44,"粤港澳流动渔民证"),
    CERTIFICATE_TYPE_TEMP_MOVE_FISHERMEN_OF_YUE_HK_MC(45,"粤港澳临时流动渔民证"),
    CERTIFICATE_TYPE_MOVE_BOAT_ACCOUNT_BOOK_OF_YUE_HK_MC(46,"粤港澳流动渔船户口簿"),
    CERTIFICATE_TYPE_DA_KAO_TAI_LUN(47,"搭靠台轮许可证"),
    CERTIFICATE_TYPE_SERVICE_PERSONNEL_BOARDING_OPERATION(48,"劳务人员登轮作业证"),
    CERTIFICATE_TYPE_TAIWAN_RESIDENT_BOARDING(49,"台湾居民登陆证"),
    CERTIFICATE_TYPE_BUSINESS(50,"贸易证"),
    CERTIFICATE_TYPE_PASS_IN_BORDER(60,"边境通行证"),
    CERTIFICATE_TYPE_TRANSIT_FARMING_OF_SHENZHEN(61,"深圳市过境耕作证"),
    CERTIFICATE_TYPE_PASSPORT_OF_HK(70,"香港特别行政区护照"),
    CERTIFICATE_TYPE_PASSPORT_OF_MACAO(71,"澳门特别行政区护照"),
    CERTIFICATE_TYPE_PASS_MYANMAR(81, "缅甸中方(缅方)通行证"),
    CERTIFICATE_TYPE_(82, "云南边境地区境外边民人出境证"),
    CERTIFICATE_TYPE_ENTER_IN_BORDER_OF_CHINA_AND_KOREA(90, "中朝边境地区出入境通行证"),
    CERTIFICATE_TYPE_PASS_IN_BORDER_OF_CHINA_AND_KOREA(91, "朝中边境地区居民过境通行证"),
    CERTIFICATE_TYPE_WATER_OPERATION(92, "鸭绿江、图们江水文作业证"),
    CERTIFICATE_TYPE_REPRESENT_OF_CHINA_AND_KOREA(93, "中朝流筏固定代表证"),
    CERTIFICATE_TYPE_SEAMAN_OF_CHINA_AND_KOREA(94, "中朝(朝中)鸭绿江、图们江航行船舶船员证"),
    CERTIFICATE_TYPE_GENERAL_DEPUTY_IN_BORDER_OF_CHINA_AND_KOREA(95, "中朝(朝中)边境地区公安总代表证"),
    CERTIFICATE_TYPE_ALTERNATE_DEPUTY_IN_BORDER_OF_CHINA_AND_KOREA(96, "中朝(朝中)边境地区公安副总代表证"),
    CERTIFICATE_TYPE_DEPUTY_IN_BORDER_OF_CHINA_AND_KOREA(97, "中朝(朝中)边境地区公安代表证"),
    CERTIFICATE_TYPE_OTHER(99, "其他证件");


    @Getter
    @Setter
    private int typeInt;
    @Getter
    @Setter
    private String typeZh;

    private CertificateTypeEnum(int typeInt, String typeZh){
        this.typeInt = typeInt;
        this.typeZh = typeZh;
    }



    public static String getCertificateTypeZh(int typeInt){
        for(CertificateTypeEnum c : CertificateTypeEnum.values()){
            if(c.typeInt == typeInt){
                return c.typeZh;
            }
        }
        return null;
    }


    public static int getCertificateTypeInt(String typeZh){
        for(CertificateTypeEnum c : CertificateTypeEnum.values()){
            if(c.typeZh.compareTo(typeZh) == 0){
                return c.typeInt;
            }
        }
        return -1;
    }
}
