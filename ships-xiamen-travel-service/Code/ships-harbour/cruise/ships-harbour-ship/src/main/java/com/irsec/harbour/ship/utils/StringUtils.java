package com.irsec.harbour.ship.utils;

import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/10/9 17:06
 * @Description:
 */
public class StringUtils {

    public static String getOrInSql(String field, List<String> list) {
        String idStr = "";
        if(list.size()<=1000){
            idStr = getInCondition(field,list);
        }else{
            int loopTime = list.size()/1000;
            int mod = list.size()%1000;
            for(int i=0; i<=loopTime;i++){
                if(i==loopTime && mod > 0){
                    idStr += getInCondition(field,list.subList(i*1000,(i)*1000 + mod));
                }else if(i== loopTime && mod == 0){
                    idStr = idStr.substring(0,idStr.length()-4);
                }else {
                    idStr += getInCondition(field,list.subList(i*1000,(i+1)*1000)) + " or ";
                }
            }
        }
        return idStr;
    }

    public static String getInCondition(String filed,List<String> ids){
        String idStr = filed + " in (";
        for(String id : ids){
            idStr += "'"+id+"',";
        }
        if(idStr.contains(",")){
            idStr = idStr.substring(0,idStr.lastIndexOf(","));
        }
        idStr += ")";

        return idStr;
    }

    public static String[][] getSection(String[] ids, int sectionNumber){
        if(ids == null || ids.length==0 || sectionNumber <=0){
            return  null;
        }
        int time = ids.length/sectionNumber;
        int mod = ids.length%sectionNumber;
        String[][] result = null;
        if(mod == 0){
            result = new String[time][];
        }else{
            result = new String[time+1][];
        }

        int ind=0;
        for(int i=0;i <= time;i++){
            if(i == time && mod > 0){
                result[i] = new String[mod];
                ind = 0;
                for(int index = i*sectionNumber;index<i*sectionNumber+mod;index++,ind++){
                    result[i][ind] = ids[index];
                }
            }else if(i<time) {
                result[i] = new String[sectionNumber];
                ind = 0;
                for(int index = i*sectionNumber;index<(i+1)*sectionNumber;index++, ind++){
                    result[i][ind] = ids[index];
                }
            }
        }
        return result;
    }
}
