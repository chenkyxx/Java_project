package com.irsec.harbour.ship.data.dao;

import com.irsec.harbour.ship.data.entity.ShipLane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 11:16
 * @Description:
 */
@Repository
public interface LaneDao extends JpaRepository<ShipLane, String>, JpaSpecificationExecutor<ShipLane> {

    @Modifying
    @Query(value = "delete from ShipLane where flightId = (?1)")
    @Transactional
    void deleteByFlightPlanId(@Param("flightId") String flightId);



    @Modifying
    @Query(value = "delete from ShipLane where shipFlightId = (?1)")
    @Transactional
    void deleteByFlightId(@Param("shipFlightId") String flightId);


    List<ShipLane> findByShipFlightIdOrderByOrder(String shipFlightId);
}
