package com.irsec.harbour.ship.data.dao;

import com.irsec.harbour.ship.data.entity.ShipBerth;
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
 * @Date: 2019/8/19 09:52
 * @Description: 泊位相关的数据库操作
 */

@Repository
public interface BerthDao extends JpaRepository<ShipBerth, String>, JpaSpecificationExecutor<ShipBerth> {

    @Query(value = "select new com.irsec.harbour.ship.data.entity.ShipBerth(id,berthName) from ShipBerth")
    List<ShipBerth> getAll();
}
