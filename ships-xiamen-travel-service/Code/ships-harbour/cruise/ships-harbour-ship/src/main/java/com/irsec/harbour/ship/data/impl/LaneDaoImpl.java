package com.irsec.harbour.ship.data.impl;

import com.irsec.harbour.ship.data.dao.LaneDao;
import com.irsec.harbour.ship.data.entity.ShipLane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 11:17
 * @Description:
 */
@Service
public class LaneDaoImpl {

    @Autowired
    LaneDao laneDao;

    public void save(List<ShipLane> list){
        for(ShipLane shipLane : list){
            laneDao.save(shipLane);
        }
    }
    public void save(ShipLane shipLane){
        laneDao.save(shipLane);
    }

    /**
     *
     *  根据靠离泊计划的id删除
     */
    public void deleteByFlightPlanId(String flightPlanId){
        laneDao.deleteByFlightPlanId(flightPlanId);
    }
    /**
     *
     *  根据航线的id删除
     */
    public void deleteOne(String id){
        laneDao.deleteById(id);
    }



    public void deleteByFlightId(String flightId){
        laneDao.deleteByFlightId(flightId);
    }

    //根据航班id进行查询
    public List<ShipLane> findByshipFlightId(String shipFlightId){
        return laneDao.findByShipFlightIdOrderByOrder(shipFlightId);
    }
}