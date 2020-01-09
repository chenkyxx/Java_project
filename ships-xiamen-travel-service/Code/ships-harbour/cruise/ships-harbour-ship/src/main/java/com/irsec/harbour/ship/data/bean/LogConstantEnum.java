package com.irsec.harbour.ship.data.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 11:11
 * @Description: 系统日志操作类型常量集合
 */
public enum LogConstantEnum {

    LOG_OPT_ADD_BERTH(0,"新增泊位","新增了泊位："),
    LOG_OPT_EDIT_BERTH(1,"修改泊位","修改了泊位："),
    LOG_OPT_DELETE_BERTH(2,"删除泊位","删除了泊位："),
    LOG_OPT_QUERY_BERTH(3,"查看泊位","查看了泊位，查询条件为："),
    LOG_OPT_ADD_SHIP(4,"新增船舶","新增了船舶："),
    LOG_OPT_EDIT_SHIP(5,"修改船舶","修改了船舶："),
    LOG_OPT_DELETE_SHIP(6,"删除船舶","删除了船舶："),
    LOG_OPT_QUERY_SHIP(7,"查看船舶","查看了船舶，查询条件为："),
    LOG_OPT_ADD_FLIGHT(8,"新增靠离泊计划","新增靠离泊计划，id:"),
    LOG_OPT_EDIT_FLIGHT(9,"修改靠离泊计划","修改靠离泊计划，id:"),
    LOG_OPT_DELETE_FLIGHT(10,"删除靠离泊计划","删除靠离泊计划，id:"),
    LOG_OPT_ADD_JOB(11,"申请生产作业计划","申请生产作业计划，作业计划id:"),
    LOG_OPT_CANCEL_JOB(12,"取消生产作业计划","取消生产作业计划，作业计划id:"),
    LOG_OPT_SUBMIT_JOB(13,"提交生产作业计划","提交生产作业计划，作业计划id:"),
    LOG_OPT_FEEDBACK_JOB(14,"反馈生产作业计划","反馈生产作业计划，作业计划id:"),
    LOG_OPT_REJECT_JOB(15,"驳回生产作业计划","驳回生产作业计划，作业计划id:"),
    LOG_OPT_FLIGHT_QUERY(16,"查询靠离泊计划","查询了靠离泊计划，查询条件为:"),
    LOG_OPT_JOB_QUERY(17,"查询生产作业计划","查询了生产作业，查询条件为:"),
    LOG_OPT_JOB_HISTORY_QUERY(18,"查询生产作业计划历史","查询了生产作业计划历史，查询条件为:"),
    LOG_OPT_PUSH_CHECKIN(19,"推送日志","");


    // 成员变量
    @Getter
    @Setter
    private int optType;
    @Getter
    @Setter
    private String optContent;
    @Getter
    @Setter
    private String optdetail;

    // 构造方法
    private LogConstantEnum(int optType, String optContent, String optdetail) {
        this.optType = optType;
        this.optContent = optContent;
        this.optdetail = optdetail;
    }


    /**
     * 根据标记号获取内容
     * @param optType
     * @return
     */
    public static String getOptContent(int optType) {
        for (LogConstantEnum c : LogConstantEnum.values()) {
            if (c.optType == optType) {
                return c.optContent;
            }
        }
        return null;
    }

    /**
     * 根据内容获取标记号
     * @param optContent
     * @return
     */
    public static int getOptType(String optContent) {
        int type = -1;
        for (LogConstantEnum c : LogConstantEnum.values()) {
            if (optContent.compareTo(c.optContent) == 0) {
                type = c.optType;
                break;
            }
        }
        return type;
    }

    public static List<HashMap<String,Object>> getList(){
        List<HashMap<String,Object>> list = new ArrayList<>();
        for (LogConstantEnum c : LogConstantEnum.values()) {
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("id",c.optType);
            hashMap.put("value",c.optContent);
            list.add(hashMap);
        }
        return list;
    }
}
