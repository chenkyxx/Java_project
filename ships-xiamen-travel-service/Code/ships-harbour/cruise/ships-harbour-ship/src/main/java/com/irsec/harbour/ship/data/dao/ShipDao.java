package com.irsec.harbour.ship.data.dao;

import com.irsec.harbour.ship.data.dto.ShipSearchDTO;
import com.irsec.harbour.ship.data.entity.ShipBoat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 16:25
 * @Description:
 */
@Repository
public interface ShipDao extends JpaRepository<ShipBoat, String>, JpaSpecificationExecutor<ShipBoat> {

    @Query(value = "SELECT new com.irsec.harbour.ship.data.dto.ShipSearchDTO(id,shipZhName) from ShipBoat where shipZhName like CONCAT('%',?1,'%')")
    List<ShipSearchDTO> getListByNameZh(@Param("shipZhName") String shipZhName);
}
