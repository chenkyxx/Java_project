package com.irsec.harbour.ship.service;

import com.irsec.harbour.ship.data.entity.ShipLane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: Jethro
 * @Date: 2019/10/12 10:37
 * @Description: 缓存航班的航线信息，减少重复查询
 *
 */
@Service
@Slf4j
public class ShipFlightLanesCacheService {

    private static ConcurrentHashMap<String,List<ShipLane>> shipLaneCache = new ConcurrentHashMap<>();


    public static void putShipLanes(String flightId, List<ShipLane> lanes){
        shipLaneCache.put(flightId, lanes);
    }

    public static List<ShipLane> getShipLines(String flightId){
        return shipLaneCache.getOrDefault(flightId,null);
    }



    @Scheduled(cron = "1 0 0 * * ?")
    private void clearGroupNoCache() {
        log.info("定时清空缓存任务开始执行");
        Set<String> groupNoSet = shipLaneCache.keySet();
        log.info("清空了缓存中的航线信息：{}",groupNoSet.toArray());
        shipLaneCache.clear();
    }
}
