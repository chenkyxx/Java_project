package com.irsec.harbour.ship.service;

import com.irsec.harbour.ship.data.entity.ShipLuggageCode;
import com.irsec.harbour.ship.utils.DateUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
/**
 * @Auther: Jethro
 * @Date: 2019/10/8 16:38
 * @Description:
 */
public class ShipLuggageCodeBatchInsertPreparedStatementSetter implements BatchPreparedStatementSetter {

    final List<ShipLuggageCode> temList;

    public ShipLuggageCodeBatchInsertPreparedStatementSetter(List<ShipLuggageCode> temList) {
        this.temList = temList;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ShipLuggageCode shipLuggageCode = temList.get(i);
        Date date = new Date(new java.util.Date().getTime());
        ps.setDate(1,date);
        ps.setInt(2,shipLuggageCode.getIsPrint());
        ps.setString(3,shipLuggageCode.getLuggageCode());
        ps.setString(4,shipLuggageCode.getPassengerId());
        ps.setDate(5,null);
        ps.setDate(6,date);
        ps.setString(7,shipLuggageCode.getId());
    }

    @Override
    public int getBatchSize() {
        return temList.size();
    }
}
