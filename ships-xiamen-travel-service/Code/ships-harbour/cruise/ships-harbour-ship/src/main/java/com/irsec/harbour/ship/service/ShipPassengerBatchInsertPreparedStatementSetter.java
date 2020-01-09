package com.irsec.harbour.ship.service;

import com.irsec.harbour.ship.data.entity.ShipPassenger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/10/8 17:22
 * @Description:
 */
public class ShipPassengerBatchInsertPreparedStatementSetter implements BatchPreparedStatementSetter {
    final List<ShipPassenger> temList;

    public ShipPassengerBatchInsertPreparedStatementSetter(List<ShipPassenger> temList) {
        this.temList = temList;
    }

    /**
     *
      * @param ps
     * @param i
     * @throws SQLException
     */

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ShipPassenger passenger = temList.get(i);
        Date date = new Date(new java.util.Date().getTime());
        ps.setDate(1, passenger.getBirthDay() == null?null:new Date(passenger.getBirthDay().getTime()));
        ps.setString(2,passenger.getCarrierCh());
        ps.setString(3,passenger.getCarrierEn());
        ps.setInt(4,passenger.getCertificateType());
        ps.setString(5,passenger.getCheckDeviceNo());
        ps.setDate(6,null);
        ps.setString(7,passenger.getContact());
        ps.setString(8,passenger.getCountry());
        ps.setDate(9,date);
        ps.setString(10,passenger.getEscapeArea());
        ps.setString(11,passenger.getFlightId());
        ps.setString(12,passenger.getFloor());
        ps.setString(13,passenger.getGroupNo());
        ps.setString(14,passenger.getIdNumber());
        ps.setInt(15,passenger.getIsCheckingTacket());
        ps.setInt(16,passenger.getIsPrint());
        ps.setString(17,passenger.getLocation());
        ps.setString(18,passenger.getMemberLevel());
        ps.setString(19,passenger.getPassengerNameCh());
        ps.setString(20,passenger.getPassengerNameEn());
        ps.setString(21,passenger.getPassportId());
        ps.setDate(22,passenger.getPassportValidity()==null?null:new Date(passenger.getPassportValidity().getTime()));
        ps.setString(23,passenger.getPlanArriveTime());
        ps.setDate(24,null);
        ps.setString(25,passenger.getReserveNo());
        ps.setString(26,passenger.getRoomNo());
        ps.setString(27,passenger.getRoute());
        ps.setDate(28,passenger.getSailDate() == null ? null :new Date(passenger.getSailDate().getTime()));
        ps.setString(29,passenger.getSex());
        ps.setString(30,passenger.getShipNameCh());
        ps.setString(31,passenger.getShipNameEn());
        ps.setString(32,passenger.getShipNo());
        ps.setString(33,passenger.getStartingPort());
        ps.setString(34,passenger.getTicketType());
        ps.setString(35,passenger.getTouristIdentity());
        //ps.setInt(36,null);
        ps.setObject(36,passenger.getTouristType());
        ps.setDate(37,date);
        ps.setString(38,passenger.getUserBarcode());
        ps.setString(39,passenger.getUserId());
        ps.setString(40,passenger.getId());
        ps.setString(41,passenger.getRemarks());
    }

    @Override
    public int getBatchSize() {
        return temList.size();
    }


    /**
     * sql
     *
     *
     * INSERT INTO ship_passenger (
     * 	birth_day, 1
     * 	carrier_ch, 2
     * 	carrier_en, 3
     * 	certificate_type, 4
     * 	check_device_no, 5
     * 	checking_time, 6
     * 	contact, 7
     * 	country, 8
     * 	create_time, 9
     * 	escape_area, 10
     * 	flight_id, 11
     * 	FLOOR, 12
     * 	group_no, 13
     * 	id_number, 14
     * 	is_checking_tacket, 15
     * 	is_print, 16
     * 	LOCATION, 17
     * 	member_level, 18
     * 	passenger_name_ch, 19
     * 	passenger_name_en, 20
     * 	passport_id, 21
     * 	passport_validity, 22
     * 	plan_arrive_time, 23
     * 	print_time, 24
     * 	reserve_no, 25
     * 	room_no, 26
     * 	route, 27
     * 	sail_date, 28
     * 	sex, 29
     * 	ship_name_ch, 30
     * 	ship_name_en, 31
     * 	ship_no, 32
     * 	starting_port, 33
     * 	ticket_type, 34
     * 	tourist_identity, 35
     * 	tourist_type, 36
     * 	update_time, 37
     * 	user_barcode, 38
     * 	user_id, 39
     * 	ID  40
     * )
     * VALUES
     * 	(
     * 		?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
     * 	)
     */
}
