package com.irsec.harbour.ship.data.dao;

import com.irsec.harbour.ship.data.entity.ShipLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 10:25
 * @Description: 日志操作的相关类
 */
@Repository
public interface LogDao extends JpaRepository<ShipLog, Integer>,JpaSpecificationExecutor<ShipLog> {
}
