package com.irsec.harbour.ship.data.impl;

import com.irsec.harbour.ship.data.bean.CertificateTypeEnum;
import com.irsec.harbour.ship.data.dao.ShipPassengerDao;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.ShipFlight;
import com.irsec.harbour.ship.data.entity.ShipLane;
import com.irsec.harbour.ship.data.entity.ShipLuggageCode;
import com.irsec.harbour.ship.data.entity.ShipPassenger;
import com.irsec.harbour.ship.service.ShipFlightLanesCacheService;
import com.irsec.harbour.ship.service.ShipPassengerBatchInsertPreparedStatementSetter;
import com.irsec.harbour.ship.service.TouristLeaderCacheService;
import com.irsec.harbour.ship.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
public class ShipPassengerImpl {

    @Autowired
    ShipPassengerDao shipPassengerDao;

    @Autowired
    ShipLuggageCodeImpl shipLuggageCodeImpl;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    ShipManualCheckinImpl shipManualCheckinImpl;
    @Autowired
    ShipLuggageCheckInImpl shipLuggageCheckInImpl;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    LaneDaoImpl laneDao;

    /**
     * 根据id数组删除多个旅客
     *
     * @param shipPassengers
     */
    @Transactional
    public void deleteAllByUserIdIn(List<ShipPassenger> shipPassengers) {
        Set<String> passengerIds = new HashSet<>();

        for (ShipPassenger shipPassenger : shipPassengers) {
            passengerIds.add(shipPassenger.getId());
        }
        String[] passengerIdArray = new String[passengerIds.size()];
        passengerIds.toArray(passengerIdArray);
        log.info(Arrays.toString(passengerIdArray));
        //删除验票记录
        if (!passengerIds.isEmpty()) {
            //删除旅客验票记录
            shipManualCheckinImpl.deleteAllByPassengerIdIn(passengerIds);
            //删除行李条验证记录
            shipLuggageCheckInImpl.deleteAllByPassengerIds(passengerIds);
            //删除乘客的行李条
            shipLuggageCodeImpl.deleteAllByPassengerIdIn(passengerIdArray);
            //删除乘客
            //shipPassengerDao.deleteAllByIdIn(passengerIds);
            List resultList = new ArrayList<>(Arrays.asList(passengerIdArray));
            String idstr = com.irsec.harbour.ship.utils.StringUtils.getOrInSql("ID", resultList);
            String sql = "DELETE from SHIP_PASSENGER where " + idstr;
            log.info("sql : {}", sql);
            entityManager.createNativeQuery(sql).executeUpdate();
        }
    }


    public List<ShipPassenger> findAllByUserIdIn(String[] userId) {
        List<ShipPassenger> list = new ArrayList<>();
        //如果数量超过1000个则分批进行查询
        if (userId.length > 1000) {
            //2001

            String[][] sectionArray = com.irsec.harbour.ship.utils.StringUtils.getSection(userId, 1000);
            for (String[] section : sectionArray) {
                list.addAll(shipPassengerDao.findAllByUserIdIn(section));
            }
        } else {
            list.addAll(shipPassengerDao.findAllByUserIdIn(userId));
        }
        //return shipPassengerDao.findAllByUserIdIn(userId);
        return list;

    }

    /**
     * 存储旅客
     *
     * @param shipPassengers
     */
    @org.springframework.transaction.annotation.Transactional
    public void saveAll(List<ShipPassenger> shipPassengers) {
        String sql = "insert into ship_passenger (birth_day, carrier_ch, carrier_en, certificate_type, check_device_no, checking_time, contact, country, create_time, escape_area, flight_id, floor, group_no, id_number, is_checking_tacket, is_print, location, member_level, passenger_name_ch, passenger_name_en, passport_id, passport_validity, plan_arrive_time, print_time, reserve_no, room_no, route, sail_date, sex, ship_name_ch, ship_name_en, ship_no, starting_port, ticket_type, tourist_identity, tourist_type, update_time, user_barcode, user_id, id,remarks) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        int[] ints = jdbcTemplate.batchUpdate(sql, new ShipPassengerBatchInsertPreparedStatementSetter(shipPassengers));
//        Iterator<ShipPassenger> iter = shipPassengers.iterator();
//        //entityManager.getTransaction().begin();
//        while (iter.hasNext()){
//            ShipPassenger passenger = iter.next();
//            entityManager.persist(passenger);
//        }
        //entityManager.getTransaction().commit();
        // shipPassengerDao.saveAll(shipPassengers);
    }

    /**
     * 分页按条件查询
     *
     * @param specification
     * @param pageable
     * @return
     */
    public Page<ShipPassenger> findAll(Specification<ShipPassenger> specification, Pageable pageable) {
        return shipPassengerDao.findAll(specification, pageable);
    }

    /**
     * 查询旅客根据条形码
     *
     * @param passporId
     * @return
     */
    public ShipPassenger findByPassportId(String passporId) {
        Date today0dian = DateUtil.getToday0dian();
        Specification<ShipPassenger> spec = new Specification<ShipPassenger>() {
            @Override
            public Predicate toPredicate(Root<ShipPassenger> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.equal(root.get("passportId"), passporId));
                list.add(cb.greaterThanOrEqualTo(root.get("sailDate"), today0dian));
                //list.add(cb.and(cb.greaterThanOrEqualTo(root.get("sailDate"),today0dian),cb.lessThanOrEqualTo(root.get("sailDate"),today24dian)));
                //list.add(cb.and(cb.greaterThanOrEqualTo(root.get("sailDate"),today0dian),cb.lessThanOrEqualTo(root.get("sailDate"),today24dian)));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        Sort sort = Sort.by(Sort.Direction.ASC, "sailDate");
        List<ShipPassenger> list = shipPassengerDao.findAll(spec, sort);

        //返回一个Map，当日旅客，和未来旅客
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
        // return shipPassengerDao.findByPassportId(passporId);
    }

    /**
     * 查询旅客根据条形码
     *
     * @param userBarcode
     * @return
     */
    public List<ShipPassenger> findAllByUserBarcodeIn(String[] userBarcode) {
        return shipPassengerDao.findAllByUserBarcodeIn(userBarcode);
    }

    /**
     * 查询旅客根据证件号
     *
     * @param passporIds
     * @return
     */
    public List<ShipPassenger> findAllByPassportIdIn(String[] passporIds) {
        return shipPassengerDao.findAllByPassportIdIn(passporIds);
    }


    /**
     * 查询航班人数
     *
     * @param filterId
     * @param checkDeviceNo
     * @return
     */
    public int countByFlightIdAndCheckDeviceNoAndIsCheckingTacket(String filterId, String checkDeviceNo, int isCheckingTacket) {


        return (int) shipPassengerDao.countByFlightIdAndCheckDeviceNoAndIsCheckingTacket(filterId, checkDeviceNo, isCheckingTacket);
    }


    /**
     * 更新旅客打印凭证状态
     *
     * @param userBarCode
     * @return
     */
    public ShipPassenger updatePrintStatus(String passengerId, String userBarCode) {
        ShipPassenger shipPassenger = shipPassengerDao.findByUserBarcodeAndId(userBarCode, passengerId);
        if (shipPassenger == null || StringUtils.isEmpty(shipPassenger.getId())) {
            return null;
        }
        shipPassenger.setIsPrint(1);
        shipPassenger.setPrintTime(new Date());
        shipPassengerDao.save(shipPassenger);
        return shipPassenger;
    }


    public List<String> findLeaderNameByGroupNoAndShipNo(String groupNo, String shipNo) {
        String sql = "select passenger_name_ch from ship_passenger where group_no = '" + groupNo + "' and ship_no = '" + shipNo + "' and  tourist_identity='领队'";
        List<Object> result = entityManager.createNativeQuery(sql).getResultList();
        List<String> list = new ArrayList<>();
        for (Object object : result) {
            list.add((String) object);
        }
        return list;
    }

    /**
     * 获取用于
     */
    public HashMap<String, Object> getPassengerAndFlightAndLuggages(ShipPassenger shipPassenger) {
        PassengerPrintQueryDto passengerQueryDTO = new PassengerPrintQueryDto();
        FlightQueryDTO flightQueryDTO = new FlightQueryDTO();
        ShipFlight shipFlight = shipPassenger.getShipFlight();
        List<ShipLuggageCode> luggageCodes = shipPassenger.getShipLuggageCodes();
        List<LuggageCodeDTO> luggageCodeDTOList = new ArrayList<>();

        //乘客信息
        BeanUtils.copyProperties(shipPassenger, passengerQueryDTO);
        passengerQueryDTO.setCertificateType(CertificateTypeEnum.getCertificateTypeZh(shipPassenger.getCertificateType()));
        passengerQueryDTO.setBirthDay(shipPassenger.getBirthDay());
        passengerQueryDTO.setPassportValidity(shipPassenger.getPassportValidity());
        passengerQueryDTO.setIsPrint(shipPassenger.getIsPrint());
        passengerQueryDTO.setOfficeCode(shipPassenger.getPassportId());

        String key = shipPassenger.getShipNo() + "-" + shipPassenger.getGroupNo();

        if (TouristLeaderCacheService.isExistGroup(key)) {
            passengerQueryDTO.setLeader(TouristLeaderCacheService.getLeader(key));
        } else {
            List<String> leaderList = findLeaderNameByGroupNoAndShipNo(shipPassenger.getGroupNo(), shipPassenger.getShipNo());
            if (leaderList.isEmpty()) {
                passengerQueryDTO.setLeader("暂无");
            } else {
                for (String name : leaderList) {
                    TouristLeaderCacheService.addLeader(key, name);
                }
                passengerQueryDTO.setLeader(TouristLeaderCacheService.getLeader(key));
            }
        }


        //航班信息
        BeanUtils.copyProperties(shipFlight, flightQueryDTO);
        flightQueryDTO.setSailDate(shipFlight.getSailDate());
        flightQueryDTO.setRoutes(shipFlight.getShipLaneList());

        //行李条信息
        for (ShipLuggageCode shipLuggageCode : luggageCodes) {
            LuggageCodeDTO luggageCodeDTO = new LuggageCodeDTO();
            BeanUtils.copyProperties(shipLuggageCode, luggageCodeDTO);
            luggageCodeDTOList.add(luggageCodeDTO);
        }


        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("passenger", passengerQueryDTO);
        resultMap.put("flight", flightQueryDTO);
        resultMap.put("luggages", luggageCodeDTOList);

        return resultMap;
    }

    public ShipPassenger findById(String id) {
        Optional<ShipPassenger> shipPassengerOptional = shipPassengerDao.findById(id);
        if (shipPassengerOptional.isPresent()) {
            return shipPassengerOptional.get();
        } else {
            return null;
        }
    }


    public List<ShipPassenger> findAllById(String[] ids) {
        return shipPassengerDao.findAllByIdIn(ids);
    }


    public Page<PassengerQueryDTO> findAllPassengerByFlightPlanId(int page, int pageSize, String flightPlanId) {
        Page<PassengerQueryDTO> result = null;
        Specification<ShipPassenger> spec = new Specification<ShipPassenger>() {
            @Override
            public Predicate toPredicate(Root<ShipPassenger> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.equal(root.join("shipFlight").get("flightPlanId"), flightPlanId));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ShipPassenger> passengerPage = shipPassengerDao.findAll(spec, pageable);
        List<PassengerQueryDTO> list = getListPassengerQueryDTO(passengerPage.getContent());
        result = new PageImpl<PassengerQueryDTO>(list, passengerPage.getPageable(), passengerPage.getTotalElements());
        return result;
    }

    public Page<PassengerQueryDTO> findAllPassengerByCondition(int page, int pageSize, PassengerQueryConditionDTO condition) {
        Page<PassengerQueryDTO> result = null;
        Specification<ShipPassenger> spec = new Specification<ShipPassenger>() {
            @Override
            public Predicate toPredicate(Root<ShipPassenger> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (condition != null) {
                    if (!StringUtils.isEmpty(condition.getPassengerName())) {
                        list.add(cb.or(cb.like(root.get("passengerNameCh"), "%" + condition.getPassengerName() + "%"), cb.like(root.get("passengerNameEn"), "%" + condition.getPassengerName() + "%")));
                    }
                    if (!StringUtils.isEmpty(condition.getGroupNo())) {
                        list.add(cb.equal(root.get("groupNo"), condition.getGroupNo()));
                    }
                    if (!StringUtils.isEmpty(condition.getShipNo())) {
                        list.add(cb.equal(root.get("shipNo"), condition.getShipNo()));
                    }

                    if (!StringUtils.isEmpty(condition.getType())) {
                        list.add(cb.equal(root.get("certificateType"), condition.getType()));
                    }

                    if (!StringUtils.isEmpty(condition.getIdNumber())) {
                        list.add(cb.like(root.get("passportId"), "%" + condition.getIdNumber() + "%"));
                    }

                    if (!StringUtils.isEmpty(condition.getIdCard())) {
                        list.add(cb.like(root.get("idNumber"), "%" + condition.getIdNumber() + "%"));
                    }

                    if (!StringUtils.isEmpty(condition.getPhone())) {
                        list.add(cb.like(root.get("contact"), "%" + condition.getPhone() + "%"));
                    }
                }
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ShipPassenger> passengerPage = shipPassengerDao.findAll(spec, pageable);
        List<PassengerQueryDTO> list = getListPassengerQueryDTO(passengerPage.getContent());
        result = new PageImpl<PassengerQueryDTO>(list, passengerPage.getPageable(), passengerPage.getTotalElements());
        return result;
    }


    private List<PassengerQueryDTO> getListPassengerQueryDTO(List<ShipPassenger> content) {
        List<PassengerQueryDTO> list = new ArrayList<>();


        for (ShipPassenger shipPassenger : content) {
            PassengerQueryDTO passengerQueryDTO = new PassengerQueryDTO();
            BeanUtils.copyProperties(shipPassenger, passengerQueryDTO);

            //缓存航班的航线信息
            if (!StringUtils.isEmpty(shipPassenger.getFlightId())) {
                List<ShipLane> lanes = null;
                lanes = ShipFlightLanesCacheService.getShipLines(shipPassenger.getFlightId());
                if (lanes == null) {
                    //在数据库中进行查询
                    lanes = laneDao.findByshipFlightId(shipPassenger.getFlightId());
                    if (lanes != null) {
                        ShipFlightLanesCacheService.putShipLanes(shipPassenger.getFlightId(), lanes);
                    }
                }
                passengerQueryDTO.setRoute(lanes);
            }


            if (StringUtils.isEmpty(passengerQueryDTO.getPassengerNameCh())) {
                passengerQueryDTO.setPassengerNameCh(shipPassenger.getPassengerNameEn());
            }
            //getIsCheckingTacket 默认值为0，验票成功则为1
            if (shipPassenger.getIsCheckingTacket() == 1) {
                passengerQueryDTO.setIsChecked(0);
                passengerQueryDTO.setVerifyResult(0);
            } else if (shipPassenger.getIsCheckingTacket() == 2) {
                passengerQueryDTO.setIsChecked(0);
                passengerQueryDTO.setVerifyResult(-1);
            } else {
                passengerQueryDTO.setIsChecked(-1);
            }

            passengerQueryDTO.setBirthDay(DateUtil.dateToStr(shipPassenger.getBirthDay(), "yyyy-MM-dd HH:mm:ss"));
            passengerQueryDTO.setSailDate(DateUtil.dateToStr(shipPassenger.getSailDate(), "yyyy-MM-dd HH:mm:ss"));
            passengerQueryDTO.setPassportValidity(DateUtil.dateToStr(shipPassenger.getPassportValidity(), "yyyy-MM-dd HH:mm:ss"));
            passengerQueryDTO.setArrivetime(DateUtil.dateToStr(shipPassenger.getCheckingTime(), "yyyy-MM-dd HH:mm:ss"));

            list.add(passengerQueryDTO);
        }
        return list;
    }

    @Transactional
    public int updatePassengerLeader(String groupNo, String leader) {
        return shipPassengerDao.updatePassengerLeaderByGroupNo(groupNo, leader);
    }


    public List<ShipPassenger> findLeaderByGroupNo(String groupNo) {
        List<ShipPassenger> list = shipPassengerDao.findAllByGroupNoAndTouristIdentity(groupNo, "领队");
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list;
        }
    }


    public List<ShipPassenger> findPassengerByUpdateTime(Date startTime, Date endTime) {
        List<ShipPassenger> list = null;

        Specification<ShipPassenger> spec = new Specification<ShipPassenger>() {
            @Override
            public Predicate toPredicate(Root<ShipPassenger> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.and(cb.greaterThanOrEqualTo(root.get("updateTime"), startTime), cb.lessThanOrEqualTo(root.get("updateTime"), endTime)));
                list.add(cb.equal(root.get("isCheckingTacket"), 1));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        list = shipPassengerDao.findAll(spec);
        return list;
    }


    public ShipPassenger findByCertificateTypeAndCertifiId(Integer certificateType, String certificateId) {
        //List<ShipPassenger> list = shipPassengerDao.findByPassportIdAndCertificateTypeOrderByCreateTimeDesc(certificateId, certificateType);
        Date today0dian = DateUtil.getToday0dian();
        Specification<ShipPassenger> spec = new Specification<ShipPassenger>() {
            @Override
            public Predicate toPredicate(Root<ShipPassenger> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.equal(root.get("certificateType"), certificateType));
                list.add(cb.greaterThanOrEqualTo(root.get("sailDate"), today0dian));
                list.add(cb.equal(root.get("passportId"), certificateId));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        Sort sort = Sort.by(Sort.Direction.ASC, "sailDate");
        List<ShipPassenger> list = shipPassengerDao.findAll(spec, sort);

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }


    public int countAllByFlightIdIn(String[] flights) {
        return shipPassengerDao.countAllByFlightIdIn(flights);
    }

    public ShipPassenger findByIdNumber(String idNumber) {
        Date today0dian = DateUtil.getToday0dian();
        Specification<ShipPassenger> spec = new Specification<ShipPassenger>() {
            @Override
            public Predicate toPredicate(Root<ShipPassenger> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.equal(root.get("idNumber"), idNumber));
                list.add(cb.greaterThanOrEqualTo(root.get("sailDate"), today0dian));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        Sort sort = Sort.by(Sort.Direction.ASC, "sailDate");
        List<ShipPassenger> list = shipPassengerDao.findAll(spec, sort);

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
