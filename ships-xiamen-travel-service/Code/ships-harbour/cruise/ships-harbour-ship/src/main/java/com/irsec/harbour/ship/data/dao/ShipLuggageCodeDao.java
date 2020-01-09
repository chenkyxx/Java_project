package com.irsec.harbour.ship.data.dao;

import com.irsec.harbour.ship.data.entity.ShipLuggageCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/31 09:34
 * @Description: 行李条数据库操作
 */

@Repository
public interface ShipLuggageCodeDao extends JpaRepository<ShipLuggageCode, String>, JpaSpecificationExecutor<ShipLuggageCode> {

    List<ShipLuggageCode> findAllByLuggageCode(String luggageCode);

    ShipLuggageCode findByPassengerIdAndLuggageCode(String passengerId, String luggageCode);

    List<ShipLuggageCode> findAllByLuggageCodeIn(String[] luggageCodes);

    ShipLuggageCode findByLuggageCodeAndPassengerId(String luggageCode, String passengerId);
    ShipLuggageCode findByLuggageCode(String luggageCode);
    ShipLuggageCode findByIdAndLuggageCode(String id,String luggageCode);

    void deleteAllByPassengerIdIn(String[] passengerIds);
}
