package com.irsec.harbour.ship.data.impl;

import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.dao.FlightPlanDao;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import com.irsec.harbour.ship.data.entity.ShipLane;
import com.irsec.harbour.ship.utils.BeanExtUtil;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.UUIDTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 10:51
 * @Description:
 */
@Service
public class FlightPlanDaoImpl {
    Logger logger = LoggerFactory.getLogger(FlightPlanDaoImpl.class);
    @Autowired
    FlightPlanDao flightPlanDao;
    @Autowired
    LaneDaoImpl laneDao;
    @Autowired
    ShipFlightImpl shipFlightImpl;

    @Autowired
    private EntityManager entityManager;

    public ShipFlightPlan save(ShipFlightPlan shipFlightPlan) {
        return flightPlanDao.saveAndFlush(shipFlightPlan);
    }


    public ShipFlightPlan update(FlightPlanDTO flightPlanDTO) throws Exception {
        // 已申请
        ShipFlightPlan shipFlightPlan = null;
        shipFlightPlan = flightPlanDao.getOne(flightPlanDTO.getId());
        if (StringUtils.isEmpty(shipFlightPlan.getId())) {
            return null;
        } else {
            //修改
            String[] nullPropertyArrays = BeanExtUtil.getNullProperty(flightPlanDTO);
            BeanUtils.copyProperties(flightPlanDTO, shipFlightPlan, nullPropertyArrays);
            if (!StringUtils.isEmpty(flightPlanDTO.getBerthId())) {
                shipFlightPlan.setBerId(flightPlanDTO.getBerthId());
            }
            if (!StringUtils.isEmpty(flightPlanDTO.getShipAgentType())) {
                shipFlightPlan.setShipAgent(flightPlanDTO.getShipAgentType());
            }

            if (!StringUtils.isEmpty(flightPlanDTO.getPortType())) {
                shipFlightPlan.setPortType(flightPlanDTO.getPortType());
            }
            if (!StringUtils.isEmpty(flightPlanDTO.getPlanDepartTime())) {
                shipFlightPlan.setPlanDepartTime(DateUtil.strToDate(flightPlanDTO.getPlanDepartTime(), "yyyyMMddHHmmss"));
            }
            if (!StringUtils.isEmpty(flightPlanDTO.getPlanArriveTime())) {
                shipFlightPlan.setPlanArriveTime(DateUtil.strToDate(flightPlanDTO.getPlanArriveTime(), "yyyyMMddHHmmss"));
            }
            if (!StringUtils.isEmpty(flightPlanDTO.getPlanArrivePortTime())) {
                shipFlightPlan.setPlanArrivePortTime(DateUtil.strToDate(flightPlanDTO.getPlanArrivePortTime(), "yyyyMMddHHmmss"));
            }


            flightPlanDao.save(shipFlightPlan);
            if (flightPlanDTO.getLaneChanged() == 0) {
                //表示航线被修改了
                //为了简化航线变化的操作，先将之前的航线记录删除
                laneDao.deleteByFlightPlanId(flightPlanDTO.getId());
                //再insert新的航线记录
                List<ShipLane> shipLanes = new ArrayList<>();
                for (LaneDTO laneDTO : flightPlanDTO.getLane()) {
                    ShipLane shipLane = new ShipLane();
                    shipLane.setId(UUIDTool.newUUID());
                    shipLane.setFlightId(flightPlanDTO.getId());
                    shipLane.setArriveTime(DateUtil.strToDate(laneDTO.getPlanArriveTime(), "yyyy-MM-dd HH:mm"));
                    shipLane.setPlace(laneDTO.getPlace());
                    shipLane.setPortType(laneDTO.getPortType());
                    shipLane.setOrder(laneDTO.getOrder());
                    shipLane.setPlaceCode(laneDTO.getPlaceCode());
                    shipLanes.add(shipLane);
                }
                laneDao.save(shipLanes);
            }
            return shipFlightPlan;
        }
    }

    public ShipFlightPlan updateStatusApply(FlightPlanDTO flightPlanDTO) {
        // 已申请
        ShipFlightPlan shipFlightPlan = null;
        shipFlightPlan = flightPlanDao.getOne(flightPlanDTO.getId());
        if (StringUtils.isEmpty(shipFlightPlan.getId())) {
            return null;
        } else {
            //将待作业/带计划状态变更为已申请
            shipFlightPlan.setPlanStatus(ConstanCollection.STATUS_APPLY);
            flightPlanDao.save(shipFlightPlan);
            return shipFlightPlan;
        }
    }

    public ShipFlightPlan updateStatusWaitToPlan(FlightPlanDTO flightPlanDTO) {
        //待作业/待计划
        ShipFlightPlan shipFlightPlan = null;
        shipFlightPlan = flightPlanDao.getOne(flightPlanDTO.getId());
        if (StringUtils.isEmpty(shipFlightPlan.getId())) {
            return null;
        } else {
            //将已申请状态变更为待计划/待作业状态
            shipFlightPlan.setPlanStatus(ConstanCollection.STATUS_WAIT_TO_PLAN);
            flightPlanDao.save(shipFlightPlan);
            return shipFlightPlan;
        }
    }

    public ShipFlightPlan updateStatusReject(FlightPlanDTO flightPlanDTO) {
        //已驳回
        ShipFlightPlan shipFlightPlan = null;
        shipFlightPlan = flightPlanDao.getOne(flightPlanDTO.getId());
        if (StringUtils.isEmpty(shipFlightPlan.getId())) {
            return null;
        } else {
            //将待反馈/待计划/待作业状态变更为驳回状态
            shipFlightPlan.setPlanStatus(ConstanCollection.STATUS_REJECT);
            flightPlanDao.save(shipFlightPlan);
            return shipFlightPlan;
        }
    }

    public ShipFlightPlan updateStatusWaitToFeedback(FlightPlanDTO flightPlanDTO) {
        //待反馈 (界面上就时点击了作业计划)
        ShipFlightPlan shipFlightPlan = null;
        shipFlightPlan = flightPlanDao.getOne(flightPlanDTO.getId());
        if (StringUtils.isEmpty(shipFlightPlan.getId())) {
            return null;
        } else if (shipFlightPlan.getPlanStatus() == ConstanCollection.STATUS_FEEDBACK) {
            //不能将状态为已反馈的计划修改回待反馈
            return shipFlightPlan;
        } else {
            //将待作业/待计划状态变更为待反馈
            shipFlightPlan.setPlanStatus(ConstanCollection.STATUS_WAIT_TO_FEEDBACK);
            shipFlightPlan.setPlanPassTime(DateUtil.strToDate(flightPlanDTO.getPlanPassTime(), "yyyyMMddHHmmss"));
            shipFlightPlan.setPlanCloseTime(DateUtil.strToDate(flightPlanDTO.getPlanCloseTime(), "yyyyMMddHHmmss"));
            flightPlanDao.save(shipFlightPlan);
            return shipFlightPlan;
        }
    }

    public ShipFlightPlan updateStatusFeedback(FlightPlanDTO flightPlanDTO) throws EntityNotFoundException {
        // 已反馈  （界面上点击了作业反馈）

        Optional<ShipFlightPlan> optionalShipFlightPlan = flightPlanDao.findById(flightPlanDTO.getId());

        if (!optionalShipFlightPlan.isPresent()) {
            throw new EntityNotFoundException();
        } else {
            ShipFlightPlan shipFlightPlan = optionalShipFlightPlan.get();
            //为了简化航线变化的操作，先将之前的航线记录删除
            laneDao.deleteByFlightPlanId(flightPlanDTO.getId());
            //再insert新的航线记录
            List<ShipLane> shipLanes = new ArrayList<>();
            for (LaneDTO laneDTO : flightPlanDTO.getLane()) {
                ShipLane shipLane = new ShipLane();
                shipLane.setId(UUIDTool.newUUID());
                shipLane.setFlightId(flightPlanDTO.getId());
                shipLane.setArriveTime(DateUtil.strToDate(laneDTO.getPlanArriveTime(), "yyyy-MM-dd HH:mm"));
                shipLane.setPlace(laneDTO.getPlace());
                shipLane.setPortType(laneDTO.getPortType());
                shipLane.setPlaceCode(laneDTO.getPlaceCode());
                shipLanes.add(shipLane);
            }

            laneDao.save(shipLanes);
            //最后将待反馈状态变更为已反馈
            shipFlightPlan.setPlanStatus(ConstanCollection.STATUS_FEEDBACK);
            shipFlightPlan.setActualArriveTime(DateUtil.strToDate(flightPlanDTO.getActualArriveTime(), "yyyyMMddHHmmss"));
            shipFlightPlan.setActualDepartTime(DateUtil.strToDate(flightPlanDTO.getActualDepartTime(), "yyyyMMddHHmmss"));
            shipFlightPlan.setActualPassTime(DateUtil.strToDate(flightPlanDTO.getActualPassTime(), "yyyyMMddHHmmss"));
            shipFlightPlan.setActualCloseTime(DateUtil.strToDate(flightPlanDTO.getActualCloseTime(), "yyyyMMddHHmmss"));
            shipFlightPlan.setInboundLuggage(flightPlanDTO.getInboundLuggage());
            shipFlightPlan.setOutboundLuggage(flightPlanDTO.getOutboundLuggage());
            shipFlightPlan.setInboundNumber(flightPlanDTO.getInboundNumber());
            shipFlightPlan.setOutboundNumber(flightPlanDTO.getOutboundNumber());

            shipFlightPlan.setLeavePassTime(DateUtil.strToDate(flightPlanDTO.getLeavePassTime(), "yyyyMMddHHmmss"));
            shipFlightPlan.setLeaveCloseTime(DateUtil.strToDate(flightPlanDTO.getLeaveCloseTime(), "yyyyMMddHHmmss"));
            shipFlightPlan.setGarbageNum(flightPlanDTO.getGarbageNum());
            shipFlightPlan.setAddWaterNum(flightPlanDTO.getAddWaterNum());
            shipFlightPlan.setCraneNum(flightPlanDTO.getCraneNum());
            shipFlightPlan.setForkliftNum(flightPlanDTO.getForkliftNum());
            shipFlightPlan.setHelpWorkerNum(flightPlanDTO.getHelpWorkerNum());
            shipFlightPlan.setSailorNum(flightPlanDTO.getSailorNum());


            flightPlanDao.save(shipFlightPlan);
            return shipFlightPlan;
        }
    }

    public ShipFlightPlan delete(String flightPlanId) {
        ShipFlightPlan shipFlightPlan = flightPlanDao.getOne(flightPlanId);
        if (StringUtils.isEmpty(shipFlightPlan.getId())) {
            return null;
        } else {
            laneDao.deleteByFlightPlanId(flightPlanId);
            flightPlanDao.deleteById(flightPlanId);
            return shipFlightPlan;
        }
    }

    public Page<ShipFlightPlan> queryJobByCondition(final JobConditionDTO condition, int page, int pageSize, boolean isHistory, boolean isExport) {
        Page<ShipFlightPlan> result = null;
        Specification<ShipFlightPlan> spec = null;
        spec = new Specification<ShipFlightPlan>() {
            @Override
            public Predicate toPredicate(Root<ShipFlightPlan> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (isHistory) {
                    //如果是查询生产作业历史，则只查询状态为已反馈的
                    //status 等于4，表示这里只查询已反馈记录；
                    list.add(cb.equal(root.get("planStatus"), ConstanCollection.STATUS_FEEDBACK));
                } else {
                    //查询生产作业计划，则查询状态为待计划和待反馈
                    //status 等于1或者等于3，表示这里只查询待计划或者待反馈的记录；
                    if (StringUtils.isEmpty(condition.getStatus())) {
                        list.add(cb.or(cb.equal(root.get("planStatus"), ConstanCollection.STATUS_WAIT_TO_PLAN), cb.equal(root.get("planStatus"), ConstanCollection.STATUS_WAIT_TO_FEEDBACK), cb.equal(root.get("planStatus"), ConstanCollection.STATUS_FEEDBACK)));
                    } else {
                        list.add(cb.equal(root.get("planStatus"), condition.getStatus()));
                    }
                }

                if (condition != null) {
                    if (!StringUtils.isEmpty(condition.getBerthId())) {
                        list.add(cb.equal(root.get("berId"), condition.getBerthId()));
                    }
                    if (!StringUtils.isEmpty(condition.getShipNameZh())) {
                        list.add(cb.like(root.join("shipBoat").get("shipZhName"), "%" + condition.getShipNameZh() + "%"));
                    }
                    if (condition.getStartTime() != null) {
                        list.add(cb.greaterThanOrEqualTo(root.get("planArriveTime"), condition.getStartTime()));
                    }
                    if (condition.getEndTime() != null) {
                        list.add(cb.lessThanOrEqualTo(root.get("planArriveTime"), condition.getEndTime()));
                    }
                }
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        if (isExport) {
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
            List<ShipFlightPlan> list = flightPlanDao.findAll(spec, sort);
            result = new PageImpl<>(list, PageRequest.of(0, list.size() == 0 ? 1 : list.size()), list.size() == 0 ? 1 : list.size());
        } else {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");
            result = flightPlanDao.findAll(spec, pageable);
        }
        return result;
    }


    public List<FlightPlanQueryDTO> queryFlightPlanByCarrierId(final String carrierId) {
        List<FlightPlanQueryDTO> result = null;
        Specification<ShipFlightPlan> spec = new Specification<ShipFlightPlan>() {
            @Override
            public Predicate toPredicate(Root<ShipFlightPlan> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(criteriaBuilder.equal(root.join("shipBoat").get("carrierId"), carrierId));
                return criteriaQuery.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        List<ShipFlightPlan> list = flightPlanDao.findAll(spec, sort);
        List<String> ids = list.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<String> isSelectFlightPlans = shipFlightImpl.isFindFlightPlanId(ids);

        result = getListFlightPlanQueryDTO(list, isSelectFlightPlans);

        return result;
    }

    public Page<FlightPlanQueryDTO> queryFlightPlanByCondition(final FlightPlanConditionDTO condition, int page, int pageSize, boolean isExport) {
        List<FlightPlanQueryDTO> result = null;
        Specification<ShipFlightPlan> spec = new Specification<ShipFlightPlan>() {
            @Override
            public Predicate toPredicate(Root<ShipFlightPlan> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                //status 小于等于2，表示这里只查询已申请，待作业，和已驳回状态的记录；

                if (!StringUtils.isEmpty(condition.getQueryType()) && condition.getQueryType() == 1) {
                    list.add(cb.lessThanOrEqualTo(root.get("planStatus"), ConstanCollection.STATUS_WAIT_TO_PLAN));
                } else {
                    list.add(cb.lessThanOrEqualTo(root.get("planStatus"), ConstanCollection.STATUS_REJECT));
                }


                if (condition != null) {
                    if (!StringUtils.isEmpty(condition.getBerthId())) {
                        list.add(cb.equal(root.get("berId"), condition.getBerthId()));
                    }
                    if (!StringUtils.isEmpty(condition.getPortType())) {
                        list.add(cb.equal(root.get("portType"), condition.getPortType()));
                    }
                    if (!StringUtils.isEmpty(condition.getShipNameZh())) {
                        list.add(cb.like(root.join("shipBoat").get("shipZhName"), "%" + condition.getShipNameZh() + "%"));
                    }
                    if (condition.getStartTime() != null) {
                        list.add(cb.greaterThanOrEqualTo(root.get("planArriveTime"), condition.getStartTime()));
                    }
                    if (condition.getEndTime() != null) {
                        list.add(cb.lessThanOrEqualTo(root.get("planArriveTime"), condition.getEndTime()));
                    }

                }
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };


        if (isExport) {
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
            List<ShipFlightPlan> list = flightPlanDao.findAll(spec, sort);
            result = getListFlightPlanQueryDTO(list, null);
            return new PageImpl<>(result, PageRequest.of(0, list.size() == 0 ? 1 : list.size()), list.size() == 0 ? 1 : list.size());
        } else {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");

            Page<ShipFlightPlan> list = flightPlanDao.findAll(spec, pageable);
            List<String> ids = list.get().map(item -> item.getId()).collect(Collectors.toList());
            List<String> isSelectFlightPlans = shipFlightImpl.isFindFlightPlanId(ids);
            result = getListFlightPlanQueryDTO(list.getContent(), isSelectFlightPlans);
            return new PageImpl<>(result, list.getPageable(), list.getTotalElements());
        }
    }

    private List<FlightPlanQueryDTO> getListFlightPlanQueryDTO(List<ShipFlightPlan> list, List<String> isSelectFlightPlans) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<FlightPlanQueryDTO> result = new ArrayList<FlightPlanQueryDTO>();
        for (ShipFlightPlan shipFlightPlan : list) {
            FlightPlanQueryDTO flightPlanQueryDTO = new FlightPlanQueryDTO();
            if (isSelectFlightPlans != null) {
                if (isSelectFlightPlans.contains(shipFlightPlan.getId())) {
                    flightPlanQueryDTO.setIsSelect(0);
                } else {
                    flightPlanQueryDTO.setIsSelect(1);
                }
            }
            if (shipFlightPlan.getShipBoat() != null) {
                flightPlanQueryDTO.setShipCallSign(shipFlightPlan.getShipBoat().getShipCallSign());
                flightPlanQueryDTO.setShipNameZh(shipFlightPlan.getShipBoat().getShipZhName());
                flightPlanQueryDTO.setShipType(shipFlightPlan.getShipBoat().getShipType());
                flightPlanQueryDTO.setShipNameEn(shipFlightPlan.getShipBoat().getShipEnName());
            }

            if (shipFlightPlan.getShipBerth() != null) {
                flightPlanQueryDTO.setBerthName(shipFlightPlan.getShipBerth().getBerthName());
            }

            flightPlanQueryDTO.setId(shipFlightPlan.getId());
            flightPlanQueryDTO.setPrePort(shipFlightPlan.getPrePort());
            flightPlanQueryDTO.setNextPort(shipFlightPlan.getNextPort());
            flightPlanQueryDTO.setBerId(shipFlightPlan.getBerId());
            flightPlanQueryDTO.setShipId(shipFlightPlan.getShipId());
            flightPlanQueryDTO.setShipAgent(shipFlightPlan.getShipAgent());
            flightPlanQueryDTO.setPortType(shipFlightPlan.getPortType());
            flightPlanQueryDTO.setCapacity(shipFlightPlan.getCapacity());
            flightPlanQueryDTO.setCreateTime(DateUtil.dateToStr(shipFlightPlan.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            flightPlanQueryDTO.setCreateUser(shipFlightPlan.getCreateUser());
            flightPlanQueryDTO.setIsLead(shipFlightPlan.getIsLead());
            flightPlanQueryDTO.setPlanArriveTime(DateUtil.dateToStr(shipFlightPlan.getPlanArriveTime(), "yyyy-MM-dd HH:mm:ss"));
            flightPlanQueryDTO.setPlanDepartTime(DateUtil.dateToStr(shipFlightPlan.getPlanDepartTime(), "yyyy-MM-dd HH:mm:ss"));
            flightPlanQueryDTO.setStatus(shipFlightPlan.getPlanStatus());
            flightPlanQueryDTO.setShipLanes(shipFlightPlan.getShipLaneList());
            flightPlanQueryDTO.setSide(shipFlightPlan.getSide());
            flightPlanQueryDTO.setPlanArrivePortTime(shipFlightPlan.getPlanArrivePortTime());
            result.add(flightPlanQueryDTO);
        }
        return result;
    }

    public boolean findAllByBerId(String berId) {

        Specification<ShipFlightPlan> spec = new Specification<ShipFlightPlan>() {
            @Override
            public Predicate toPredicate(Root<ShipFlightPlan> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.equal(root.get("berId"), berId));
                list.add(cb.notEqual(root.get("planStatus"), ConstanCollection.STATUS_FEEDBACK));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        List<ShipFlightPlan> list = flightPlanDao.findAll(spec);

        if (CollectionUtils.isEmpty(list)) {
            //为空表示该泊位关联的靠离泊计划均是已反馈，或者该泊位没有靠离泊计划
            return true;
        }
        return false;
    }


    public boolean findAllByShipId(String shipId) {


        Specification<ShipFlightPlan> spec = new Specification<ShipFlightPlan>() {
            @Override
            public Predicate toPredicate(Root<ShipFlightPlan> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.equal(root.get("shipId"), shipId));
                list.add(cb.notEqual(root.get("planStatus"), ConstanCollection.STATUS_FEEDBACK));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        List<ShipFlightPlan> list = flightPlanDao.findAll(spec);

        if (CollectionUtils.isEmpty(list)) {
            //为空表示该船关联的靠离泊计划均是已反馈，或者该船没有靠离泊计划
            return true;
        }

        return false;
    }


    public List<ShipFlightPlan> findAllByTime(Date startTime, Date endTime) {
        //计划到达时间在当前时间之前
        //计划离泊时间在当前时间之后

        Date date = new Date();
        List<ShipFlightPlan> list = null;
        Specification<ShipFlightPlan> spec = new Specification<ShipFlightPlan>() {
            @Override
            public Predicate toPredicate(Root<ShipFlightPlan> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                //查询计划靠泊时间在近一个小时以内的
                list.add(cb.or(cb.and(cb.lessThanOrEqualTo(root.get("planArriveTime"), date), cb.greaterThanOrEqualTo(root.get("planDepartTime"), date)), cb.and(cb.greaterThanOrEqualTo(root.get("planArriveTime"), startTime), cb.lessThanOrEqualTo(root.get("planArriveTime"), endTime))));
                list.add(cb.isNull(root.get("actualDepartTime")));
                list.add(cb.notEqual(root.get("planStatus"), ConstanCollection.STATUS_REJECT));
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        list = flightPlanDao.findAll(spec);
        return list;

    }


    public int findPassNumberByFlightPlanId(String flightPlanId) {
        String sql = "SELECT DISTINCT(MANUAL.ID_NUMBER) FROM SHIP_MANUAL_CHECK_IN MANUAL LEFT JOIN SHIP_FLIGHT FLIGHT ON MANUAL.FLIGHT_ID = FLIGHT.\"ID\" WHERE FLIGHT.FLIGHT_PLAN_ID = '" + flightPlanId + "' AND MANUAL.VERIFY_RESULT = 0";
        logger.info("findPassNumberByFlightPlanId sql :{}", sql);

        List<Object> result = entityManager.createNativeQuery(sql).getResultList();
        if (result == null) {
            return 0;
        } else {
            return result.size();
        }
    }

    public ShipFlightPlan findFlightPlanById(String flightPlanId) {
        Optional<ShipFlightPlan> optionalShipFlightPlan = flightPlanDao.findById(flightPlanId);
        if (optionalShipFlightPlan.isPresent()) {
            return optionalShipFlightPlan.get();
        } else {
            return null;
        }
    }
}
