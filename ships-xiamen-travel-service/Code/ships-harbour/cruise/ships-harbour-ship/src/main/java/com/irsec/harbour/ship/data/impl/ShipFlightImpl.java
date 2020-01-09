package com.irsec.harbour.ship.data.impl;


import com.irsec.harbour.ship.data.dao.ShipPassengerDao;
import com.irsec.harbour.ship.data.entity.ShipFlight;
import com.irsec.harbour.ship.data.entity.ShipLane;
import com.irsec.harbour.ship.data.entity.ShipPassenger;
import com.irsec.harbour.ship.data.dao.ShipFlightDao;
import com.irsec.harbour.ship.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
public class ShipFlightImpl {


    @Autowired
    ShipFlightDao shipFlightDao;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    ShipPassengerDao shipPassengerDao;
    @Autowired
    ShipPassengerImpl shipPassengerImpl;
    @Autowired
    LaneDaoImpl laneDao;

    @Autowired
    ShipLuggageCodeImpl shipLuggageCodeImpl;

    public ShipFlight save(ShipFlight shipFlight) {
        List<ShipLane> laneList = shipFlight.getShipLaneList();
        shipFlight = shipFlightDao.save(shipFlight);
        laneDao.save(laneList);
        return shipFlight;
    }

    public ShipFlight findById(String shipFlightId) {
        Optional<ShipFlight> optional = shipFlightDao.findById(shipFlightId);
        return optional.isPresent() ? optional.get() : null;
    }

    public boolean isFindFlightPlanId(String flightPlanId){
        String sql = "select id from ship_flight where flight_plan_id = '"+flightPlanId+"'";
        log.info("isFindFlightPlanId sql :{}", sql);
        List<Object> result = entityManager.createNativeQuery(sql).getResultList();
        if(result == null || result.isEmpty()){
            return false;
        }else {
            return true;
        }
    }


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void update(ShipFlight shipFlight, int isChangedLane) {
            ShipFlight tempShipFlight = findById(shipFlight.getId());
        if (tempShipFlight != null) {
            List<ShipLane> laneList = shipFlight.getShipLaneList();
            shipFlight.setShipLaneList(null);
            BeanUtils.copyProperties(shipFlight, tempShipFlight, "createTime", "updateTime");
            shipFlightDao.save(tempShipFlight);
            List<ShipPassenger> passengerList = shipPassengerDao.findAllByFlightId(shipFlight.getId());
            for (ShipPassenger shipPassenger : passengerList) {
                //将修改后的数据同步给shipPassenger
                BeanUtils.copyProperties(shipFlight, shipPassenger, "id","createTime","updateTime");
            }
            shipPassengerDao.saveAll(passengerList);
            //判断是否需要修改航线
            if(isChangedLane == 0 ){
                //先删除之前的
                laneDao.deleteByFlightId(shipFlight.getId());
                laneDao.save(laneList);
            }
        }else {
            throw new EntityNotFoundException();
        }
    }

    @Transactional
    public void deleteFlightById(String id){
        List<ShipPassenger> passengerList = shipPassengerDao.findAllByFlightId(id);
        String[] passengerIds = new String[passengerList.size()];
        int i=0;
        for (ShipPassenger shipPassenger : passengerList){
            passengerIds[i] = shipPassenger.getId();
            i++;
        }
        //删除该航班的旅客
        shipPassengerImpl.deleteAllByUserIdIn(passengerList);
        //shipPassengerDao.deleteAll(passengerList);
        //删除该航班的航线
        laneDao.deleteByFlightId(id);
        //删除本航班
        shipFlightDao.deleteById(id);
    }

    public Page<ShipFlight> findAll(Specification<ShipFlight> specification, Pageable pageable) {
        return shipFlightDao.findAll(specification, pageable);
    }

    public Page<ShipFlight> findAll(Pageable pageable) {
        return shipFlightDao.findAll(pageable);
    }


    public List<ShipFlight> findAllById(Iterable<String> id) {
        return shipFlightDao.findAllById(id);
    }

    public List<ShipFlight> findAllByCreateTime(Date startTime, Date endTime){
        List<ShipFlight> list = new ArrayList<>();
        String startDate = DateUtil.dateToStr(startTime,"yyyy-MM-dd HH:mm:ss");
        String endDate = DateUtil.dateToStr(endTime,"yyyy-MM-dd HH:mm:ss");

        String sql = "select id,sail_date from ship_flight where sail_date >= TO_DATE ('"+startDate+"','yyyy-MM-dd HH24:mi:ss')"
                +" and sail_date <= TO_DATE ('"+endDate+"','yyyy-MM-dd HH24:mi:ss')";
        List<Object> result = entityManager.createNativeQuery(sql).getResultList();

        for(Object object : result){
            Object[] obj = (Object[]) object;
            ShipFlight shipFlight = new ShipFlight();
            shipFlight.setId((String)obj[0]);
            shipFlight.setSailDate((Date) obj[1]);
            list.add(shipFlight);
        }
        return list;
    }

    public boolean isFindFlightPlan(String flightPlanId){
        String sql = "select id from ship_flight where flight_plan_id = '"+flightPlanId+"'";
        log.info("isFindFlightPlanId sql :{}", sql);
        List<Object> result = entityManager.createNativeQuery(sql).getResultList();
        if(result == null || result.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

    public List<String> isFindFlightPlanId(List<String> ids){
        List<String> list = new ArrayList<>();
        if(ids.isEmpty()){
            return list;
        }

        String idStr = "(";
        for(String id : ids){
            idStr += "'"+id+"',";
        }
        if(idStr.contains(",")){
            idStr = idStr.substring(0,idStr.lastIndexOf(","));
        }
        idStr += ")";
        String sql = "select id,flight_plan_id from ship_flight where flight_plan_id in "+idStr+"";

        List<Object> result = entityManager.createNativeQuery(sql).getResultList();
        if(result != null){
            for(Object object : result){
                Object[] objects = (Object[]) object;
                list.add((String) objects[1]);
            }
        }
        return list;
    }
}
