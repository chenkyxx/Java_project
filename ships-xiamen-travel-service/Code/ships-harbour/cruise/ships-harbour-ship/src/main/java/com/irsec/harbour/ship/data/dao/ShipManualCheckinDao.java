package com.irsec.harbour.ship.data.dao;


import com.irsec.harbour.ship.data.entity.ShipManualCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ShipManualCheckinDao extends JpaRepository<ShipManualCheckin, String>, JpaSpecificationExecutor<ShipManualCheckin> {

    List<ShipManualCheckin> findAllByPassengerIdIn(String[] id);
    void deleteAllByPassengerIdIn(String[] ids);

}
