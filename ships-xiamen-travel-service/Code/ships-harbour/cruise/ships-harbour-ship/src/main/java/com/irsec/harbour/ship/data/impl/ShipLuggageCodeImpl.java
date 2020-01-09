package com.irsec.harbour.ship.data.impl;

import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.dao.ShipLuggageCodeDao;
import com.irsec.harbour.ship.data.entity.ShipLuggageCode;
import com.irsec.harbour.ship.service.ShipLuggageCodeBatchInsertPreparedStatementSetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/31 09:35
 * @Description: 行李条数据库操作实现层
 */
@Service
@Slf4j
public class ShipLuggageCodeImpl {


    @Autowired
    ShipLuggageCodeDao shipLuggageCodeDao;

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;


    @Transactional
    public  void saveAll(List<ShipLuggageCode> list){
//        for(ShipLuggageCode shipLuggageCode:list){
//            shipLuggageCodeDao.save(shipLuggageCode);
//        }
        String sql = "insert into ship_luggage_code (create_time, is_print, luggage_code, passenger_id, print_time, update_time, id) values (?, ?, ?, ?, ?, ?, ?)";
        int[] ints = jdbcTemplate.batchUpdate(sql, new ShipLuggageCodeBatchInsertPreparedStatementSetter(list));

//        Iterator<ShipLuggageCode> iterator = list.iterator();
//        while (iterator.hasNext()){
//            ShipLuggageCode shipLuggageCode = iterator.next();
//            entityManager.persist(shipLuggageCode);
//        }
        //shipLuggageCodeDao.saveAll(list);
    }

    public ShipLuggageCode save(ShipLuggageCode shipLuggageCode){
        return shipLuggageCodeDao.save(shipLuggageCode);
    }

    public ShipLuggageCode updatePrintStatus(String passengerId, String luggageCode){
        ShipLuggageCode shipLuggageCode = shipLuggageCode = shipLuggageCodeDao.findByLuggageCodeAndPassengerId(luggageCode, passengerId);
        if(shipLuggageCode == null || StringUtils.isEmpty(shipLuggageCode.getId())){
            return null;
        }else{
            shipLuggageCode.setIsPrint(ConstanCollection.PRINTED);
            shipLuggageCode.setPrintTime(new Date());
            shipLuggageCodeDao.save(shipLuggageCode);
            return shipLuggageCode;
        }
    }


    public ShipLuggageCode findByLuggageCode(String luggageCode)throws Exception{
        return shipLuggageCodeDao.findByLuggageCode(luggageCode);
    }


    public List<ShipLuggageCode> findAllByLuggageCode(String[] luggageCode){
        return shipLuggageCodeDao.findAllByLuggageCodeIn(luggageCode);
    }

    public void deleteAllByPassengerIdIn(String[] passengerIds){
        List resultList= new ArrayList<>(Arrays.asList(passengerIds));
        String idstr = com.irsec.harbour.ship.utils.StringUtils.getOrInSql("PASSENGER_ID",resultList);
        String sql = "DELETE from SHIP_LUGGAGE_CODE where " + idstr;
        log.info("sql : {}",sql);
        entityManager.createNativeQuery(sql).executeUpdate();
        //shipLuggageCodeDao.deleteAllByPassengerIdIn(passengerIds);
    }

    public ShipLuggageCode findByIdAndLuggageCode(String id, String luggageCode){
        return shipLuggageCodeDao.findByIdAndLuggageCode(id, luggageCode);
    }
}
