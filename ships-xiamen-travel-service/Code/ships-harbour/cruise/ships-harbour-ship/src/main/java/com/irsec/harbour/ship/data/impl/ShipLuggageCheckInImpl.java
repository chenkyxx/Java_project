package com.irsec.harbour.ship.data.impl;

import com.irsec.harbour.ship.controller.PassengerController;
import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.dao.LuggageCheckInDao;
import com.irsec.harbour.ship.data.dto.LuggageQueryDTO;
import com.irsec.harbour.ship.data.dto.TicketCheckingConditionDTO;
import com.irsec.harbour.ship.data.entity.ShipLuggageCheckin;
import com.irsec.harbour.ship.data.entity.ShipLuggageCode;
import com.irsec.harbour.ship.data.entity.ShipManualCheckin;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.*;

/**
 * @Auther: Jethro
 * @Date: 2019/8/29 17:05
 * @Description:
 */
@Service
@Slf4j
public class ShipLuggageCheckInImpl {

    @Autowired
    private LuggageCheckInDao luggageCheckInDao;

    @Autowired
    private EntityManager entityManager;



    public boolean existById(String checkId){
        return luggageCheckInDao.existsById(checkId);
    }
    public ShipLuggageCheckin save(ShipLuggageCheckin shipLuggageCheckin){
        return luggageCheckInDao.save(shipLuggageCheckin);
    }



    public void cancelAfterVerification(List<ShipLuggageCheckin> list){

        for(ShipLuggageCheckin shipLuggageCheckin : list){
            shipLuggageCheckin.setIsCancel(1);
            luggageCheckInDao.save(shipLuggageCheckin);
        }
    }

    public List<ShipLuggageCheckin> findAllByPassenegrIdIn(String[] id){
        List<ShipLuggageCheckin> list = new ArrayList<>();
        //如果数量超过1000个则分批进行查询
        if(id.length>1000){
            String[][] sectionArray = com.irsec.harbour.ship.utils.StringUtils.getSection(id,1000);
            for(String[] section: sectionArray){
                list.addAll(luggageCheckInDao.findAllByPassengerIdIn(section));
            }
        }else{
            list.addAll(luggageCheckInDao.findAllByPassengerIdIn(id));
        }
        return list;

    }

    public List<ShipLuggageCheckin> findAllByLuggageCode(String luggageCode){
        Specification<ShipLuggageCheckin> spec = new Specification<ShipLuggageCheckin>() {
            @Override
            public Predicate toPredicate(Root<ShipLuggageCheckin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.equal(root.get("isCancel"),0));
                list.add(cb.equal(root.get("luggageCode"),luggageCode));
                list.add(cb.equal(root.get("verifyResult"),0));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };

        List<ShipLuggageCheckin> page = luggageCheckInDao.findAll(spec);
        return page;
    }


    public ShipLuggageCheckin findByPassengerId(String id){
        if(id == null){
            return null;
        }
        return luggageCheckInDao.findByPassengerId(id);
    }

    public void deleteAllByPassengerIds(Set<String> passengerIds){
        //luggageCheckInDao.deleteAllByPassengerIdIn(passengerIds);
        List resultList= new ArrayList(passengerIds.size());
        resultList.addAll(passengerIds);
        String idstr = com.irsec.harbour.ship.utils.StringUtils.getOrInSql("PASSENGER_ID",resultList);
        String sql = "DELETE from SHIP_LUGGAGE_CHECKIN where " + idstr;
        log.info("sql : {}",sql);
        entityManager.createNativeQuery(sql).executeUpdate();
    }


    public int getTodayPassNumberByDeviceNo(String deviceNo){
        String today0dianStr = DateUtil.dateToStr(new Date(),"yyyyMMdd")+"000000";
        String today24dianStr = DateUtil.dateToStr(new Date(),"yyyyMMdd")+"235959";
        Date today0dianDate = DateUtil.strToDate(today0dianStr,"yyyyMMddHHmmss");
        Date today24dianDate = DateUtil.strToDate(today24dianStr,"yyyyMMddHHmmss");
        Specification<ShipLuggageCheckin> spec = new Specification<ShipLuggageCheckin>() {
            @Override
            public Predicate toPredicate(Root<ShipLuggageCheckin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.equal(root.get("isCancel"),0));
                list.add(cb.equal(root.get("deviceNo"),deviceNo));
                list.add(cb.equal(root.get("verifyResult"),ConstanCollection.CHECK_SUCCESS));
                list.add(cb.greaterThanOrEqualTo(root.get("createTime"),today0dianDate));
                list.add(cb.lessThanOrEqualTo(root.get("createTime"),today24dianDate));

                //return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        List<ShipLuggageCheckin> page = luggageCheckInDao.findAll(spec);
        HashSet<String> luggageSet = new HashSet<>();
        for(ShipLuggageCheckin shipLuggageCheckin : page){
            luggageSet.add(shipLuggageCheckin.getLuggageCode());
        }
        //num = luggageCheckInDao.getCountByDeviceNoAndTime(deviceNo,today0dian,today24dian);
        return luggageSet.size();
    }


    public Page<LuggageQueryDTO> findByCondition(int page, int pageSize, TicketCheckingConditionDTO conditionDTO) throws IOException {
        Page<LuggageQueryDTO> result = null;
        Specification<ShipLuggageCheckin> spec = new Specification<ShipLuggageCheckin>() {
            @Override
            public Predicate toPredicate(Root<ShipLuggageCheckin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.equal(root.get("isCancel"),0));
                if (conditionDTO != null) {
                    if (!StringUtils.isEmpty(conditionDTO.getVerifyResult())) {
                        if(conditionDTO.getVerifyResult() == ConstanCollection.CHECK_SUCCESS){
                            list.add(cb.equal(root.get("verifyResult"), conditionDTO.getVerifyResult()));
                        }else{
                            list.add(cb.notEqual(root.get("verifyResult"),ConstanCollection.CHECK_SUCCESS));
                        }
                    }
                    if (!StringUtils.isEmpty(conditionDTO.getCheckingTimeSt())) {
                        list.add(cb.greaterThanOrEqualTo(root.get("createTime"), conditionDTO.getCheckingTimeSt()));
                    }
                    if(!StringUtils.isEmpty(conditionDTO.getCheckingTimeEnd())){
                        list.add(cb.lessThanOrEqualTo(root.get("createTime"), conditionDTO.getCheckingTimeEnd()));
                    }
                    if (!StringUtils.isEmpty(conditionDTO.getIdNumber())) {
                        list.add(cb.isNotNull(root.join("shipPassenger").get("idNumber")));
                        list.add(cb.like(root.join("shipPassenger").get("idNumber"),"%"+conditionDTO.getIdNumber()+"%"));
                    }
                    if (!StringUtils.isEmpty(conditionDTO.getOperationType())) {
                        list.add(cb.equal(root.get("operationType"), conditionDTO.getOperationType()));
                    }
                    if (!StringUtils.isEmpty(conditionDTO.getPassengerName())) {
                        list.add(cb.like(root.join("shipPassenger").get("passengerNameCh"),"%"+conditionDTO.getPassengerName()+"%"));
                    }
                    if (!StringUtils.isEmpty(conditionDTO.getPassportId())) {
                        list.add(cb.isNotNull(root.join("shipPassenger").get("passportId")));
                        list.add(cb.like(root.join("shipPassenger").get("passportId"),"%"+conditionDTO.getPassportId()+"%"));
                    }

                    if(!StringUtils.isEmpty(conditionDTO.getCertificateType())){
                     list.add(cb.equal(root.join("shipPassenger").get("certificateType"),conditionDTO.getCertificateType()));
                    }
                }
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }

        };
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");
        Page<ShipLuggageCheckin> shipLuggageCheckinPage = luggageCheckInDao.findAll(spec,pageable);
        List<LuggageQueryDTO> list = getListShipLuggageCheckQueryDTO(shipLuggageCheckinPage.getContent());
        result = new PageImpl<>(list,shipLuggageCheckinPage.getPageable(),shipLuggageCheckinPage.getTotalElements());
        return  result;
    }

    private List<LuggageQueryDTO> getListShipLuggageCheckQueryDTO(List<ShipLuggageCheckin> content) throws IOException {

        List<LuggageQueryDTO> list = new ArrayList<>();

        for(ShipLuggageCheckin shipLuggageCheckin : content){
            LuggageQueryDTO luggageQueryDTO = new LuggageQueryDTO();
            if(shipLuggageCheckin.getVerifyResult() == ConstanCollection.CHECK_SUCCESS){
                luggageQueryDTO.setContact(shipLuggageCheckin.getShipPassenger().getContact());
                luggageQueryDTO.setCountry(shipLuggageCheckin.getShipPassenger().getCountry());
                luggageQueryDTO.setIdNumber(shipLuggageCheckin.getShipPassenger().getIdNumber());
                luggageQueryDTO.setPassengerName(PassengerController.getPassengerName(shipLuggageCheckin.getShipPassenger()));
                luggageQueryDTO.setPassportId(shipLuggageCheckin.getShipPassenger().getPassportId());
                luggageQueryDTO.setShipNo(shipLuggageCheckin.getShipPassenger().getShipNo());
                luggageQueryDTO.setCertificateType(shipLuggageCheckin.getShipPassenger().getCertificateType());
                luggageQueryDTO.setCheckedResult("已托运");
                if(shipLuggageCheckin.getShipPassenger().getShipFlight() != null){
                    luggageQueryDTO.setShipLaneList(JsonUtil.BeanToJson(shipLuggageCheckin.getShipPassenger().getShipFlight().getShipLaneList()));
                }
            }else{
                shipLuggageCheckin.setVerifyResult(-1);
            }

            luggageQueryDTO.setCheckDeviceNo(shipLuggageCheckin.getDeviceNo());
            luggageQueryDTO.setCheckingTime(DateUtil.dateToStr(shipLuggageCheckin.getVerifyTime(),"yyyy-MM-dd HH:mm:ss"));
            luggageQueryDTO.setFlightId(shipLuggageCheckin.getFlightId());
            luggageQueryDTO.setId(shipLuggageCheckin.getId());
            luggageQueryDTO.setLuggageCode(shipLuggageCheckin.getLuggageCode());
            luggageQueryDTO.setOperationType(shipLuggageCheckin.getOperationType());
            luggageQueryDTO.setPassagerId(shipLuggageCheckin.getPassengerId());
            luggageQueryDTO.setUploadTime(DateUtil.dateToStr(shipLuggageCheckin.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            luggageQueryDTO.setVerifyResult(shipLuggageCheckin.getVerifyResult());
            list.add(luggageQueryDTO);
        }
        return list;

    }

}
