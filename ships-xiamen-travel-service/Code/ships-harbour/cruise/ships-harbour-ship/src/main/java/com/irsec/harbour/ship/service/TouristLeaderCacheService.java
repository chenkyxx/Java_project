package com.irsec.harbour.ship.service;

import com.irsec.harbour.ship.controller.PassengerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: Jethro
 * @Date: 2019/9/5 10:03
 * @Description: 用于缓存旅团的领队信息
 */
@Service
public class TouristLeaderCacheService {
    Logger logger = LoggerFactory.getLogger(TouristLeaderCacheService.class);

    private static ConcurrentHashMap<String,String> leaderCache = new ConcurrentHashMap<>();

    public static boolean isExistGroup(String groupNo){
        return leaderCache.containsKey(groupNo);
    }


    public static void addLeader(String groupNo, String leader){
        String name  = leaderCache.getOrDefault(groupNo,null);
        if(!StringUtils.isEmpty(name)){
            leader = leader+","+name;
        }
        leaderCache.put(groupNo,leader);
    }
    public static String getLeader(String groupNo){
        String leader  = leaderCache.getOrDefault(groupNo,null);
        return leader;
    }

    public static String removeLeader(String groupNo,String leaderName){
        String leaderNames  = leaderCache.getOrDefault(groupNo,null);
        if(StringUtils.isEmpty(leaderNames)){
            leaderCache.remove(groupNo);
            return "";
        }else{
            String tempLeaderNames = reName(leaderNames, leaderName);
            if(!StringUtils.isEmpty(tempLeaderNames)){
                leaderCache.put(groupNo,tempLeaderNames);
            }else{
                leaderCache.remove(groupNo);
            }
            return tempLeaderNames;
        }
    }

    public static void removeLeader(String groupNo){
        leaderCache.remove(groupNo);
    }


    public static String reName(String sourceName, String reName){
        String tempLeaderNames = "";
        String[] names = sourceName.split(",");
        for(int i=0;i<names.length;i++){
            if(names[i].compareTo(reName) == 0){
                continue;
            }
            tempLeaderNames = tempLeaderNames + names[i]+",";
        }
        if(!StringUtils.isEmpty(tempLeaderNames)){
            tempLeaderNames = tempLeaderNames.substring(0,tempLeaderNames.length()-1);
        }
        return tempLeaderNames;
    }


    @Scheduled(cron = "0 0 0 * * ?")
    private void clearGroupNoCache() {
        logger.info("定时清空缓存任务开始执行");
        Set<String> groupNoSet = leaderCache.keySet();
        logger.info("清空了团号：{}",groupNoSet.toArray());
        leaderCache.clear();
    }
}
