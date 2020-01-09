package com.irsec.harbour.ship.data.dao;

import com.irsec.harbour.ship.data.entity.ShipPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipPassengerDao extends JpaRepository<ShipPassenger, String>, JpaSpecificationExecutor<ShipPassenger> {

    List<ShipPassenger> findAllByUserBarcodeIn(String[] userBarcode);

    List<ShipPassenger> findAllByPassportIdIn(String[] passportId);


    List<ShipPassenger> findAllByIdIn(String[] id);

    ShipPassenger findByUserBarcode(String userBarcode);
    //List<ShipPassenger> findByPassportIdAndCertificateTypeOrderByCreateTimeDesc(String passportId, Integer certificateType);
    ShipPassenger findByUserBarcodeAndId(String userBarcode,String id);

    ShipPassenger findByPassportId(String passportId);

    void deleteAllByUserIdIn(String[] userId);
    void deleteAllByIdIn(String[] passengerIds);
    List<ShipPassenger> findAllByUserIdIn(String[] userId);

    long countByFlightIdAndCheckDeviceNoAndIsCheckingTacket(String flightId, String checkDeviceNo, int isCheckingTacket);

    List<ShipPassenger> findAllByFlightId(String flightId);

    int countAllByFlightIdIn(String[] flightIds);



    @Modifying
    @Query("update ShipPassenger set leader = (?2) where groupNo = (?1)")
    public int updatePassengerLeaderByGroupNo(String groupNo, String leader);

    List<ShipPassenger> findAllByGroupNoAndTouristIdentity(String groupNo, String touristIdentity);
}
