package com.irsec.harbour.ship.data.bean;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 11:31
 * @Description: 常量集合
 */
public class ConstanCollection {
    final static public String DEVICE_WEB = "Web端";


    /**
     * 靠离泊计划状态
     *      * 0 : 已计划
     *      * 1 : 待作业/待计划
     *      * 2 : 已驳回
     *      * 3 : 待反馈
     *      * 4 : 已反馈
     */
    final static public int STATUS_APPLY = 0;
    final static public int STATUS_WAIT_TO_PLAN = 1;
    final static public int STATUS_REJECT = 2;
    final static public int STATUS_WAIT_TO_FEEDBACK = 3;
    final static public int STATUS_FEEDBACK = 4;


    /*
    *   行李条打印状态
    *   0:未打印
    *   1:已打印
     */

    final static public int UNPRINTED = 0;
    final static public int PRINTED = 1;



    /**
     * 行李条/票 数据库 验证状态
     * 0:验证通过
     * -1: 二维码错误
     * -2:旅客验证错误
     * -3:不是当天的航班
     * -4:非验票时间段
     * -5:本票当天已验证
     */
    final static public int CHECK_SUCCESS = 0;
    final static public int CHECK_FAILED_ERROR_CODE = -1;
    final static public int CHECK_FAILED_NOT_FOUND_PASSENGER = -2;
    final static public int CHECK_FAILED_NOT_TODAY = -3;
    final static public int CHECK_NOT_IN_CHECK_TIME = -4;
    final static public int CHECK_TICKET_HAS_CHECKED = -5;

    /**
     * 乘客表 票验证状态
     */

    final static public  int NOT_CHECK = 0;
    final static public int PASS = 1;
    final static public int NOT_PASS = 2;

    final static public String FIELD_ID = "id";
    final static public String FIELD_BAR_CODE = "barcode";
    final static public String FIELD_VERIFY_RESULT = "verifyResult";
    final static public String FIELD_PASSENGER_ID = "passagerId";
    final static public String FIELD_CHECKING_TIME = "checkingTime";
    final static public String FIELD_CHECK_DEVICE_NO = "checkDeviceNo";
    final static public String FIELD_PASSENGER_NAME = "passengerName";
    final static public String FIELD_PASSPORT_ID = "passportId";
    final static public String FIELD_ID_NUMBER = "idNumber";
    final static public String FIELD_CONTACT = "contact";
    final static public String FIELD_COUNTRY = "country";
    final static public String FIELD_OPERATION_TYPE = "operationType";
    final static public String FIELD_UPLOAD_TIME = "uploadTime";
    final static public String FIELD_FLIGHT_ID = "flightId";
    final static public String FIELD_SHIP_NO = "shipNo";
    final static public String FIELD_SHIP_LANE_LIST = "shipLaneList";
    final static public String FIELD_CERTIFICATE_TYPE = "certificateType";
    final static public String FIELD_SEX = "sex";
    final static public String FIELD_ROOM_NO = "roomNo";
    final static public String FIELD_BIRTHDAY = "birthDay";

    final static public String FIELD_SHIP_AGENT = "shipAgent";
    final static public String FIELD_SAILDATE = "sailDate";
    final static public String FIELD_SHIP_NAME_CH = "shipNameCh";
    final static public String FIELD_VERIFY_TOTAL = "verifyTotal";
    final static public String FIELD_CERTIFICATE_ID = "certificateId";
    final static public String FIELD_VALIDITY_DATE = "validityDate";

    final static public String FIELD_GUEST_STATE  = "GuestState";
    final static public String FIELD_TICKET_ID = "TicketID";
    final static public String FIELD_TICKET_STATE = "TicketState";

    /**
     * 2019-11-28 新需求
     */
    final static public String FIELD_MANUAL_PASS = "manual_pass";

    /**
     * checkService返回内容
     */
    final static public String TERMINAL = "terminal";
    final static public String RESULT = "result";



    private static Map<Integer,Integer> faceCheckCertificateTypeMap = new HashMap<>(3);

    public static Map<Integer, Integer> getFaceCheckCertificateTypeMap() {
        faceCheckCertificateTypeMap.put(1, 21);
        faceCheckCertificateTypeMap.put(2, 25);
        faceCheckCertificateTypeMap.put(3, 14);
        return faceCheckCertificateTypeMap;
    }
}
