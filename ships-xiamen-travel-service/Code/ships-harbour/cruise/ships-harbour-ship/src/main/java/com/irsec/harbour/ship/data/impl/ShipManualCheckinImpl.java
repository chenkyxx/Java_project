package com.irsec.harbour.ship.data.impl;


import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.dao.ShipManualCheckinDao;
import com.irsec.harbour.ship.data.dao.ShipPassengerDao;
import com.irsec.harbour.ship.data.dto.TicketCheckingConditionDTO;
import com.irsec.harbour.ship.data.entity.ShipFlight;
import com.irsec.harbour.ship.data.entity.ShipLane;
import com.irsec.harbour.ship.data.entity.ShipManualCheckin;
import com.irsec.harbour.ship.data.entity.ShipPassenger;
import com.irsec.harbour.ship.service.CheckRecoderPusher;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.JsonUtil;
import com.irsec.harbour.ship.utils.SecretUtils;
import com.irsec.harbour.ship.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShipManualCheckinImpl {
    Logger logger = LoggerFactory.getLogger(ShipManualCheckinImpl.class);
    @Autowired
    private ShipManualCheckinDao shipManualCheckinDao;

    @Autowired
    private ShipPassengerImpl shipPassengerDao;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LaneDaoImpl laneDao;

    @Autowired
    private CheckRecoderPusher checkRecoderPusher;

    /**
     * 保存验票信息
     *
     * @param shipManualCheckin
     * @param shipPassenger
     */
    @Transactional
    public void saveCheckin(ShipManualCheckin shipManualCheckin, ShipPassenger shipPassenger) {
        if (shipManualCheckin.getVerifyResult() == ConstanCollection.CHECK_SUCCESS) {
            ShipManualCheckin oldShipManualCheckIn = findTodayCheckInByPassportId(shipManualCheckin);
            if (oldShipManualCheckIn != null) {
                shipManualCheckin.setTicketId(oldShipManualCheckIn.getTicketId());
            } else {
                shipManualCheckin.setTicketId(SecretUtils.getTicketId());
            }
        }

        entityManager.merge(shipManualCheckin);
        if (shipPassenger != null) {
            entityManager.merge(shipPassenger);
        }
    }

    public ShipManualCheckin findTodayCheckInByPassportId(ShipManualCheckin shipManualCheckin) {
        Date startDate = DateUtil.getToday0dian();
        Date endDate = DateUtil.getToday24dian();
        Specification<ShipManualCheckin> spec = new Specification<ShipManualCheckin>() {
            @Override
            public Predicate toPredicate(Root<ShipManualCheckin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<>();

                list.add(cb.greaterThanOrEqualTo(root.get("checkingTime"), startDate));
                list.add(cb.lessThanOrEqualTo(root.get("checkingTime"), endDate));
                list.add(cb.equal(root.get("verifyResult"), ConstanCollection.CHECK_SUCCESS));
                list.add(cb.equal(root.get("isCancel"), 1));

                if (!org.springframework.util.StringUtils.isEmpty(shipManualCheckin.getPassportId())) {
                    list.add(cb.equal(root.get("passportId"), shipManualCheckin.getPassportId()));
                } else if (!org.springframework.util.StringUtils.isEmpty(shipManualCheckin.getIdNumber())) {
                    list.add(cb.equal(root.get("idNumber"), shipManualCheckin.getIdNumber()));
                }

                Predicate predicate[] = new Predicate[list.size()];
                list.toArray(predicate);
                return query.where(predicate).getRestriction();
            }
        };
        Page<ShipManualCheckin> shipManualCheckinPage = shipManualCheckinDao.findAll(spec, PageRequest.of(0, 10, Sort.Direction.DESC, "uploadTime"));
        if (shipManualCheckinPage.getTotalElements() > 0) {
            return shipManualCheckinPage.getContent().get(0);
        } else {
            return null;
        }
    }

    public List<ShipManualCheckin> getAllResend(Date start, Date end) {
        Specification<ShipManualCheckin> spec = new Specification<ShipManualCheckin>() {
            @Override
            public Predicate toPredicate(Root<ShipManualCheckin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList();
                //查询未核销
                //predicateList.add(cb.isNotNull(root.get("isCancel")));
                predicateList.add(cb.equal(root.get("isCancel"), 0));
                predicateList.add(cb.equal(root.get("verifyResult"), ConstanCollection.CHECK_SUCCESS));
                //查询开始时间
                predicateList.add(cb.greaterThanOrEqualTo(root.get("checkingTime"), start));
                //查询结束时间
                predicateList.add(cb.lessThanOrEqualTo(root.get("checkingTime"), end));

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicateList.toArray(predicates);
                query.where(predicates);
                return query.getRestriction();
            }
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "uploadTime");
        List<ShipManualCheckin> list = shipManualCheckinDao.findAll(spec, sort);
        return list;
    }

    @Transactional
    public void saveAllCheckin(List<ShipManualCheckin> shipManualCheckin, List<ShipPassenger> shipPassenger) {
        for (ShipManualCheckin checkin : shipManualCheckin)
            entityManager.merge(checkin);

        for (ShipPassenger passenger : shipPassenger)
            entityManager.merge(passenger);
    }


    public Page<ShipManualCheckin> findAll(Specification<ShipManualCheckin> specification, Pageable pageable) {
        return shipManualCheckinDao.findAll(specification, pageable);
    }

    public int cancelAfterVerification(String id) {
        Optional<ShipManualCheckin> shipManualCheckinOpt = shipManualCheckinDao.findById(id);
        ShipManualCheckin shipManualCheckin = null;
        if (shipManualCheckinOpt.isPresent()) {
            shipManualCheckin = shipManualCheckinOpt.get();
        }
        if (shipManualCheckin != null) {
            shipManualCheckin.setIsCancel(1);
            shipManualCheckinDao.save(shipManualCheckin);
            checkRecoderPusher.sendToSingleWindow(shipManualCheckin, true);
            return 1;
        } else {
            return 0;
        }
    }

    public boolean findPassportIdIsPassInToday(String passportId, String flightId) {
        String today0dianStr = DateUtil.dateToStr(new Date(), "yyyyMMdd") + "000000";
        String today24dianStr = DateUtil.dateToStr(new Date(), "yyyyMMdd") + "235959";

        String sql = "select passport_id from SHIP_MANUAL_CHECK_IN where upload_time >= TO_DATE ('" + today0dianStr + "','yyyyMMddHH24miss') and upload_time <= TO_DATE ('" + today24dianStr + "','yyyyMMddHH24miss')  and passport_id = '" + passportId + "'" + "and verify_result=0 and flight_id = '" + flightId + "' and is_cancel = 0";
        logger.info("sql : {}", sql);
        List<Object> result = entityManager.createNativeQuery(sql).getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return false;
        }
        return true;
    }

    public List<ShipManualCheckin> findAllByPassengerIdIn(String[] id) {
        List<ShipManualCheckin> list = new ArrayList<>();
        //如果数量超过1000个则分批进行查询
        if (id.length > 1000) {
            String[][] sectionArray = StringUtils.getSection(id, 1000);
            for (String[] section : sectionArray) {
                list.addAll(shipManualCheckinDao.findAllByPassengerIdIn(section));
            }
        } else {
            list.addAll(shipManualCheckinDao.findAllByPassengerIdIn(id));
        }

        return list;
    }


    public List<ShipManualCheckin> findAllByCheckTime(Date startTime, Date endTime) {
        List<ShipManualCheckin> list = new ArrayList<>();
        List<ShipManualCheckin> shipManualCheckins = null;
        String startDate = DateUtil.dateToStr(startTime, "yyyy-MM-dd HH:mm:ss");
        String endDate = DateUtil.dateToStr(endTime, "yyyy-MM-dd HH:mm:ss");

        String sql = "select id,checking_time, passport_id,manual_pass,CHECK_DEVICE_NO from SHIP_MANUAL_CHECK_IN where verify_result = 0 and is_cancel = 0 and checking_time >= TO_DATE ('" + startDate + "','yyyy-MM-dd HH24:mi:ss')"
                + " and checking_time <= TO_DATE ('" + endDate + "','yyyy-MM-dd HH24:mi:ss')";
        logger.info("findAllByCheckTime sql :{}", sql);

        List<Object> result = entityManager.createNativeQuery(sql).getResultList();
        for (Object object : result) {
            Object[] obj = (Object[]) object;
            ShipManualCheckin shipManualCheckin = new ShipManualCheckin();
            shipManualCheckin.setId((String) obj[0]);
            shipManualCheckin.setCheckingTime((Date) obj[1]);
            shipManualCheckin.setPassportId((String) obj[2]);

            if (obj[3] instanceof BigDecimal) {
                shipManualCheckin.setManualPass(((BigDecimal) obj[3]).intValue());
            } else {
                shipManualCheckin.setManualPass(0);
            }
            shipManualCheckin.setCheckDeviceNo((String) obj[4]);

            list.add(shipManualCheckin);
        }
//        Specification<ShipManualCheckin> spec = new Specification<ShipManualCheckin>() {
//            @Override
//            public Predicate toPredicate(Root<ShipManualCheckin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                List<Predicate> list = new ArrayList<Predicate>();
//                list.add(cb.and(cb.greaterThanOrEqualTo(root.get("checkingTime"),startTime), cb.lessThanOrEqualTo(root.get("checkingTime"), endTime)));
//                list.add(cb.equal(root.get("verifyResult"),0));
//                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
//            }
//        };
//
//        list = shipManualCheckinDao.findAll(spec);
//        if(list == null){
//            return new ArrayList<>();
//        }else {
//            shipManualCheckins = list.parallelStream().distinct().collect(Collectors.toList());
//            return shipManualCheckins;
//        }
        shipManualCheckins = list.parallelStream().distinct().collect(Collectors.toList());
        return shipManualCheckins;
    }

    public void deleteAllByPassengerIdIn(Set<String> ids) {
        List resultList = new ArrayList(ids.size());
        resultList.addAll(ids);
        String idstr = StringUtils.getOrInSql("PASSENGER_ID", resultList);
        String sql = "DELETE from SHIP_MANUAL_CHECK_IN where " + idstr;
        logger.info("sql : {}", sql);
        entityManager.createNativeQuery(sql).executeUpdate();
        //shipManualCheckinDao.deleteAllByPassengerIdIn(ids);
    }

    public int getTodayPassNumber(String deviceNo) {
        String today0dianStr = DateUtil.dateToStr(new Date(), "yyyyMMdd") + "000000";
        String today24dianStr = DateUtil.dateToStr(new Date(), "yyyyMMdd") + "235959";

        String sql = "select distinct passport_id,ID_NUMBER from SHIP_MANUAL_CHECK_IN where upload_time >= TO_DATE ('" + today0dianStr + "','yyyyMMddHH24miss') and upload_time <= TO_DATE ('" + today24dianStr + "','yyyyMMddHH24miss')  and check_device_no = '" + deviceNo + "'" + "and ( verify_result=0 or manual_pass=1) and is_cancel = 0";

        logger.info("getTodayPassNumber sql :{}", sql);
        List<Object> result = entityManager.createNativeQuery(sql).getResultList();
        return num(result);
    }

    private int num(List<Object> result) {
        if (result == null) {
            return 0;
        } else {
            //return result.size();
            Set<String> idSet = new HashSet<>(result.size());
            int nulCount = 0;
            for (Object object : result) {
                Object[] obj = (Object[]) object;

                String idNumber = (String) obj[1];
                if (org.springframework.util.StringUtils.isEmpty(idNumber)) {
                    nulCount++;
                } else {
                    idSet.add(idNumber);
                }
            }
            return idSet.size() + nulCount;
        }
    }

    public Page<Map<String, Object>> getManualCheckInRecordByCondition(boolean isExport, TicketCheckingConditionDTO condition, int pageIndex, int pageSize) throws IOException {

        List<Map<String, Object>> reultMapList = new ArrayList<>();

        Specification specification = (root, query, cb) ->
        {
            List<Predicate> predicateList = new ArrayList();
            //查询未核销
            //predicateList.add(cb.isNotNull(root.get("isCancel")));
            predicateList.add(cb.equal(root.get("isCancel"), 0));
            //旅客姓名
            if (!org.springframework.util.StringUtils.isEmpty(condition.getPassengerName())) {
                Predicate predicate = cb.like(root.get("passengerName"), "%" + condition.getPassengerName() + "%");
                predicateList.add(predicate);
            }
            //航班号
            if (!org.springframework.util.StringUtils.isEmpty(condition.getShipNo())) {
                predicateList.add(cb.equal(root.get("shipNo"), condition.getShipNo()));
            }
            //证件号码
            if (!org.springframework.util.StringUtils.isEmpty(condition.getPassportId())) {
                Predicate predicate = cb.equal(root.get("passportId"), condition.getPassportId());
                predicateList.add(predicate);
            }
            if (!org.springframework.util.StringUtils.isEmpty(condition.getCertificateType())) {
                Predicate predicate = cb.equal(root.join("passenger").get("certificateType"), condition.getCertificateType());
                predicateList.add(predicate);
            }
            //身份证号码
            if (!org.springframework.util.StringUtils.isEmpty(condition.getIdNumber())) {
                Predicate predicate = cb.equal(root.get("idNumber"), condition.getIdNumber());
                predicateList.add(predicate);
            }

            //验证结果
            if (condition.getVerifyResult() != null) {
                if (condition.getVerifyResult() == ConstanCollection.CHECK_SUCCESS) {
                    Predicate predicate = cb.equal(root.get("verifyResult"), ConstanCollection.CHECK_SUCCESS);
                    predicateList.add(predicate);
                } else {
                    Predicate predicate = cb.notEqual(root.get("verifyResult"), ConstanCollection.CHECK_SUCCESS);
                    predicateList.add(predicate);
                }/* else if (condition.getVerifyResult() == -1) {

                    } else {
                        Predicate predicate = cb.notEqual(root.get("verifyResult"), condition.getVerifyResult());
                        predicateList.add(predicate);
                    }*/
            }


            //查询开始时间
            if (condition.getCheckingTimeSt() != null) {
                Predicate predicate = cb.greaterThanOrEqualTo(root.get("checkingTime"), condition.getCheckingTimeSt());
                predicateList.add(predicate);
            }

            //查询结束时间
            if (condition.getCheckingTimeEnd() != null) {
                Predicate predicate = cb.lessThanOrEqualTo(root.get("checkingTime"), condition.getCheckingTimeEnd());
                predicateList.add(predicate);
            }

            //在线离线
            if (condition.getOperationType() != null) {
                Predicate predicate = cb.equal(root.get("operationType"), condition.getOperationType());
                predicateList.add(predicate);
            }


            Predicate[] predicates = new Predicate[predicateList.size()];
            predicateList.toArray(predicates);

            query.where(predicates);
            //query.orderBy(cb.desc(root.get("checkingTime")));

            return query.getRestriction();


        };
        List<ShipManualCheckin> list = null;
        Sort sort = Sort.by(Sort.Direction.DESC, "uploadTime");
        if (isExport) {
            list = shipManualCheckinDao.findAll(specification, sort);
            initMap(list, reultMapList);
            return new PageImpl<Map<String, Object>>(reultMapList, PageRequest.of(0, reultMapList.size()), reultMapList.size());
        } else {
            Page<ShipManualCheckin> data = shipManualCheckinDao.findAll(specification, PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "uploadTime"));
            list = data.getContent();
            initMap(list, reultMapList);
            return new PageImpl<>(reultMapList, data.getPageable(), data.getTotalElements());
        }
    }


    private void initMap(List<ShipManualCheckin> list, List<Map<String, Object>> reultMapList) throws IOException {
        if (!CollectionUtils.isEmpty(list)) {
            for (ShipManualCheckin shipManualCheckin : list) {
                Map<String, Object> map = new HashMap<>();
                map.put(ConstanCollection.FIELD_BAR_CODE, shipManualCheckin.getBarcode());
                map.put(ConstanCollection.FIELD_CHECK_DEVICE_NO, shipManualCheckin.getCheckDeviceNo());
                map.put(ConstanCollection.FIELD_CHECKING_TIME, DateUtil.dateToStr(shipManualCheckin.getCheckingTime(), "yyyy-MM-dd HH:mm:ss"));
                map.put(ConstanCollection.FIELD_CONTACT, shipManualCheckin.getContact());
                map.put(ConstanCollection.FIELD_COUNTRY, shipManualCheckin.getCountry());
                map.put(ConstanCollection.FIELD_FLIGHT_ID, shipManualCheckin.getFlightId());
                map.put(ConstanCollection.FIELD_ID, shipManualCheckin.getId());
                map.put(ConstanCollection.FIELD_ID_NUMBER, shipManualCheckin.getIdNumber());
                map.put(ConstanCollection.FIELD_OPERATION_TYPE, shipManualCheckin.getOperationType());
                map.put(ConstanCollection.FIELD_PASSENGER_ID, shipManualCheckin.getPassengerId());
                map.put(ConstanCollection.FIELD_PASSENGER_NAME, shipManualCheckin.getPassengerName());
                map.put(ConstanCollection.FIELD_PASSPORT_ID, shipManualCheckin.getPassportId());
                map.put(ConstanCollection.FIELD_SHIP_NO, shipManualCheckin.getShipNo());
                map.put(ConstanCollection.FIELD_UPLOAD_TIME, DateUtil.dateToStr(shipManualCheckin.getUploadTime(), "yyyy-MM-dd HH:mm:ss"));
                /**
                 * 2019-11-28 新需求
                 */
                if (shipManualCheckin.getManualPass() != null) {
                    if (shipManualCheckin.getManualPass() == 0) {
                        map.put(ConstanCollection.FIELD_MANUAL_PASS, 0);
                    } else {
                        map.put(ConstanCollection.FIELD_MANUAL_PASS, 1);
                    }
                } else {
                    map.put(ConstanCollection.FIELD_MANUAL_PASS, 0);
                }


                if (shipManualCheckin.getVerifyResult() != 0) {
                    map.put(ConstanCollection.FIELD_VERIFY_RESULT, -1);
                } else {
                    map.put(ConstanCollection.FIELD_VERIFY_RESULT, 0);
                }

                if (shipManualCheckin.getFlight() != null) {
                    map.put(ConstanCollection.FIELD_SHIP_NAME_CH, shipManualCheckin.getFlight().getShipNameCh());
                }

                //证件类型/性别/房间号
                if (!org.springframework.util.StringUtils.isEmpty(shipManualCheckin.getPassengerId())) {
                    ShipPassenger shipPassenger = shipPassengerDao.findById(shipManualCheckin.getPassengerId());
                    if (shipPassenger != null) {
                        map.put(ConstanCollection.FIELD_ROOM_NO, shipPassenger.getRoomNo());
                        map.put(ConstanCollection.FIELD_CERTIFICATE_TYPE, shipPassenger.getCertificateType());
                        map.put(ConstanCollection.FIELD_SAILDATE, DateUtil.dateToStr(shipPassenger.getSailDate(), "yyyy-MM-dd HH:mm:ss"));
                        map.put(ConstanCollection.FIELD_SEX, shipPassenger.getSex());
                        map.put(ConstanCollection.FIELD_BIRTHDAY, DateUtil.dateToStr(shipPassenger.getBirthDay(), "yyyy-MM-dd HH:mm:ss"));
                        map.put(ConstanCollection.FIELD_TICKET_STATE, shipPassenger.getIsPrint());
                    } else {
                        map.put(ConstanCollection.FIELD_CERTIFICATE_TYPE, null);
                        map.put(ConstanCollection.FIELD_ROOM_NO, null);
                        map.put(ConstanCollection.FIELD_SEX, null);
                        map.put(ConstanCollection.FIELD_SAILDATE, null);
                        map.put(ConstanCollection.FIELD_BIRTHDAY, null);
                    }
                } else {
                    map.put(ConstanCollection.FIELD_CERTIFICATE_TYPE, null);
                    map.put(ConstanCollection.FIELD_ROOM_NO, null);
                    map.put(ConstanCollection.FIELD_SEX, null);
                    map.put(ConstanCollection.FIELD_SAILDATE, null);
                    map.put(ConstanCollection.FIELD_BIRTHDAY, null);
                }


                if (!org.springframework.util.StringUtils.isEmpty(shipManualCheckin.getSex())) {
                    map.put(ConstanCollection.FIELD_SEX, shipManualCheckin.getSex());
                }
                if (shipManualCheckin.getCertificateType() != null) {
                    map.put(ConstanCollection.FIELD_CERTIFICATE_TYPE, shipManualCheckin.getCertificateType());
                }
                if (shipManualCheckin.getBirthDay() != null) {
                    map.put(ConstanCollection.FIELD_BIRTHDAY, shipManualCheckin.getBirthDay());
                }

                //航线
                List<ShipLane> shipLaneList = laneDao.findByshipFlightId(shipManualCheckin.getFlightId());
                map.put(ConstanCollection.FIELD_SHIP_LANE_LIST, JsonUtil.BeanToJson(shipLaneList));

                reultMapList.add(map);
            }
        }
    }


    public Map<String, Object> getOneTicketRecord(ShipManualCheckin shipManualCheckin) throws IOException {
        //Optional<ShipManualCheckin> optionalShipManualCheckin = shipManualCheckinDao.findById(id);
        if (shipManualCheckin != null) {
            List<ShipManualCheckin> list = new ArrayList<>();
            List<Map<String, Object>> reultMapList = new ArrayList<>();
            list.add(shipManualCheckin);
            initMap(list, reultMapList);
            if (!CollectionUtils.isEmpty(reultMapList)) {
                return reultMapList.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
