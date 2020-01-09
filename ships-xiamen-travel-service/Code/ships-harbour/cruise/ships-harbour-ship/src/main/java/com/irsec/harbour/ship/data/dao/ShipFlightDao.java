package com.irsec.harbour.ship.data.dao;

import com.irsec.harbour.ship.data.entity.ShipFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipFlightDao extends JpaRepository<ShipFlight, String>, JpaSpecificationExecutor<ShipFlight> {


}
