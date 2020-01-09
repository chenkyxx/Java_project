package com.irsec.harbour.ship.data.dao;

import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 10:50
 * @Description:
 */
@Repository
public interface FlightPlanDao extends JpaRepository<ShipFlightPlan, String>, JpaSpecificationExecutor<ShipFlightPlan> {

    @Query(value = "SELECT berId from ShipFlightPlan where berId in (?1)")
    List<String> getBerIdByBerthId(List<String> ids);

    long countAllByBerId(String berId);

    long countAllByShipId(String shipId);

    List<ShipFlightPlan> findAllByShipId(String shipId);
}
