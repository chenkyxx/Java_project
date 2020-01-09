package com.irsec.harbour.ship.data.dao;

import com.irsec.harbour.ship.data.entity.ShipLuggageCheckin;
import com.irsec.harbour.ship.data.entity.ShipManualCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/29 17:03
 * @Description:
 */
@Repository
public interface LuggageCheckInDao extends JpaRepository<ShipLuggageCheckin, String>, JpaSpecificationExecutor<ShipLuggageCheckin> {

    ShipLuggageCheckin findByPassengerId(String passengerId);


    @Query(value = "select count(distinct luggageCode) from ShipLuggageCheckin where deviceNo = ?1 and createTime >= ?2 and createTime <= ?3")
    int getCountByDeviceNoAndTime(String deviceNo,String startTime,String endTime);


    void deleteAllByPassengerIdIn(String[] ids);

    List<ShipLuggageCheckin> findAllByPassengerIdIn(String[] passengerId);
}
