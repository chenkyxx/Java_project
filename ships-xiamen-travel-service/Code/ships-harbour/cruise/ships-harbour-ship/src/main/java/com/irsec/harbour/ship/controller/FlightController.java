package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.bean.LogConstantEnum;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.ShipFlight;
import com.irsec.harbour.ship.data.entity.ShipLane;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import com.irsec.harbour.ship.data.group.ValidatedGroupLog;
import com.irsec.harbour.ship.data.impl.FlightPlanDaoImpl;
import com.irsec.harbour.ship.data.impl.LaneDaoImpl;
import com.irsec.harbour.ship.data.impl.ShipFlightImpl;
import com.irsec.harbour.ship.data.impl.ShipPassengerImpl;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.IpUtil;
import com.irsec.harbour.ship.utils.PageCheckUtil;
import com.irsec.harbour.ship.utils.UUIDTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 航班接口
 */
@RestController
@RequestMapping("api/v1/flight")
public class FlightController {

    Logger logger = LoggerFactory.getLogger(FlightController.class);

    @Autowired
    ShipFlightImpl shipFlightImpl;
    @Autowired
    FlightPlanDaoImpl flightPlanDao;
    @Autowired
    ShipPassengerImpl shipPassengerDao;
    @Autowired
    LaneDaoImpl laneDao;
    /**
     * 增加航班
     */
    @PostMapping("add")
    public ResponseEntity addFlight(@Validated({ValidatedGroup1.class,ValidatedGroup3.class,LaneDTO.ValidatedGroupFlight.class}) @RequestBody OneInputDTO<ShipFlightDTO> flightRequestParams, BindingResult bindingResult) {

        String reqId = flightRequestParams.getReqId();
        ShipFlightDTO shipFlightDto = flightRequestParams.getData();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        if(bindingResult.hasErrors()){
            return responseBuilder.Error(bindingResult.getFieldError().getDefaultMessage());
        }
        System.out.println(bindingResult);
        System.out.println(bindingResult.getAllErrors().size());



        if (shipFlightDto == null) {
            logger.info("增加航班请求-flight数据错误");
            return responseBuilder.BadRequest("flight参数错误");
        }

        //验证航班
        String m = checkAddShipFlight(shipFlightDto);
        if (!StringUtils.isEmpty(m)) {
            return responseBuilder.BadRequest(m);
        }

        try {


            ShipFlight shipFlight = null;
            //判断靠离泊计划id，是否已经被使用
            if(shipFlightImpl.isFindFlightPlan(shipFlightDto.getFlightPlanId())){
                return responseBuilder.BadRequest("该靠离泊计划已经被使用了，请更换新的靠离泊计划");
            }
            if(!StringUtils.isEmpty(shipFlightDto.getId())){
                shipFlight = shipFlightImpl.findById(shipFlightDto.getId());
                if(shipFlight != null){
                    return responseBuilder.BadRequest("该 id 已存在，id:"+shipFlightDto.getId());
                }
            }
            shipFlight = shipFlightDto.convertTo();
            shipFlight.setCheckTime(null);

            shipFlightImpl.save(shipFlight);
            logger.info("数据库成功写入航班数据成功,航班信息 = " + shipFlightDto.toString());
        } catch (Exception ex) {
            logger.error("增加航班请求-航班信息写入数据库错误");
            logger.error(ex.getMessage());
            return responseBuilder.Error("航班信息写入数据库错误");
        }

        return responseBuilder.OK();
    }


    @PostMapping("update")
    public ResponseEntity updateFlight(@RequestBody @Validated({ValidatedGroup2.class,ValidatedGroup3.class}) OneInputDTO<ShipFlightDTO> flightRequestParams, BindingResult bindingResult) {
        String reqId = flightRequestParams.getReqId();
        ShipFlightDTO shipFlightDto = flightRequestParams.getData();

        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        if(bindingResult.hasErrors()){
            return responseBuilder.Error(bindingResult.getFieldError().getDefaultMessage());
        }
        if (shipFlightDto == null) {
            logger.info("修改航班请求-flight数据错误");
            return responseBuilder.BadRequest("flight参数错误");
        }

        if (StringUtils.isEmpty(shipFlightDto.getId())) {
            logger.info("修改航班请求-flight数据错误，没有id");
            return responseBuilder.BadRequest("flight数据错误 没有id");
        }

        try {
            ShipFlight shipFlight = shipFlightDto.convertTo();

            shipFlightImpl.update(shipFlight, shipFlightDto.getIsChangedLane());

            logger.info("数据库成功写入航班数据成功,航班信息 = " + shipFlight.toString());
        } catch (EntityNotFoundException e){
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception ex) {
            logger.error("增加航班请求-航班信息写入数据库错误");
            logger.error(ex.getMessage());
            return responseBuilder.Error("航班信息写入数据库错误");
        }


        return responseBuilder.OK();
    }

    /**
     * 航班删除接口
     * @param flightRequestParams
     * @return
     */
    @PostMapping("delete")
    public ResponseEntity deleteFlight(@RequestBody OneInputDTO<ShipFlightDTO> flightRequestParams) {
        String reqId = flightRequestParams.getReqId();
        ShipFlightDTO shipFlightDto = flightRequestParams.getData();

        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        if (shipFlightDto == null) {
            logger.info("修改航班请求-flight数据错误");
            return responseBuilder.BadRequest("flight参数错误");
        }

        if (StringUtils.isEmpty(shipFlightDto.getId())) {
            logger.info("修改航班请求-flight数据错误，没有id");
            return responseBuilder.BadRequest("flight数据错误 没有id");
        }
        try {
            shipFlightImpl.deleteFlightById(shipFlightDto.getId());
            logger.info("数据库成功删除航班数据成功,航班信息 = " + shipFlightDto.toString());
        } catch (Exception ex) {
            logger.error("删除航班请求-航班信息删除数据库错误");
            logger.error(ex.getMessage());
            return responseBuilder.Error("航班信息删除数据库错误");
        }

        return responseBuilder.OK();
    }


    /**
     * 查询航班
     */
    @PostMapping("query")
    public ResponseEntity queryFlight(@RequestBody QueryInputDTO<FlightConditionDTO> pageRequestParams) {
        logger.info("开始执行航班查询接口");

        OutputDTOBuilder builder = new OutputDTOBuilder(pageRequestParams.getReqId());
        FlightConditionDTO condition = pageRequestParams.getCondition();


        //检查queryRequestParams参数
        String cm = PageCheckUtil.checkPageQueryParams(pageRequestParams);
        if (!StringUtils.isEmpty(cm)) {
            return builder.BadRequest(cm);
        }

        try {
            Page<ShipFlight> data = null;
            if (condition != null) {

                Specification<ShipFlight> specification = (root, query, cb) -> {
                    List<Predicate> predicatesList = new ArrayList<>();


                    //查询承运人
                    if (!StringUtils.isEmpty(condition.getCarrier())) {
                        Predicate namePredicate1 = cb.like(root.get("carrierCh"), '%' + condition.getCarrier() + '%');
                        Predicate namePredicate2 = cb.like(root.get("carrierEn"), '%' + condition.getCarrier() + '%');

                        Predicate predicate = cb.or(namePredicate1, namePredicate2);
                        predicatesList.add(predicate);
                    }

                    //查询邮轮
                    if (!StringUtils.isEmpty(condition.getShipName())) {
                        Predicate namePredicate1 = cb.like(root.get("shipNameCh"), '%' + condition.getShipName() + '%');
                        Predicate namePredicate2 = cb.like(root.get("shipNameEn"), '%' + condition.getShipName() + '%');

                        Predicate predicate = cb.or(namePredicate1, namePredicate2);
                        predicatesList.add(predicate);
                    }

                    //查询航班号
                    if (!StringUtils.isEmpty(condition.getShipNo())) {
                        Predicate namePredicate1 = cb.like(root.get("shipNo"), '%' + condition.getShipNo() + '%');
                        predicatesList.add(namePredicate1);

                    }

                    //启航日期
                    if (!StringUtils.isEmpty(condition.getSailDateSt())) {
                        Date d1 = DateUtil.truncateDate(condition.getSailDateSt());

                        Predicate namePredicate1 = cb.greaterThanOrEqualTo(root.get("sailDate"), d1);
                        predicatesList.add(namePredicate1);

                    }

                    //启航日期
                    if (!StringUtils.isEmpty(condition.getSailDateEnd())) {
                        Date d1 = DateUtil.addTruncateDays(condition.getSailDateEnd(), 1);

                        Predicate namePredicate1 = cb.lessThan(root.get("sailDate"), d1);
                        predicatesList.add(namePredicate1);
                    }


                    Predicate[] predicates = new Predicate[predicatesList.size()];
                    return cb.and(predicatesList.toArray(predicates));

                };


                data = shipFlightImpl.findAll(specification, PageRequest.of(pageRequestParams.getPageIndex(), pageRequestParams.getPageSize()));

            } else {
                data = shipFlightImpl.findAll(PageRequest.of(pageRequestParams.getPageIndex(), pageRequestParams.getPageSize()));
            }


            List<ShipFlightDTO> resultData = new ArrayList();

            if (data != null) {
                for (ShipFlight flight : data) {
                    resultData.add(new ShipFlightDTO().convertFrom(flight));
                }
            }


            RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
            rowsResponseParams.setRows(resultData);
            rowsResponseParams.setTotal((int) data.getTotalElements());
            rowsResponseParams.setSubtotal(resultData.size());

            logger.info("执行航班查询接口成功");

            return builder.OK(rowsResponseParams);

        } catch (Exception ex) {
            logger.error("查询航班请求-查询信息写入数据库错误");
            logger.error(ex.getMessage());
            return builder.Error("查询航班数据错误");
        }

    }


    //检测航班
    private String checkAddShipFlight(ShipFlightDTO shipFlight) {


        if (StringUtils.isEmpty(shipFlight.getCarrierCh())) {
            String m = String.format("验证航班信息 carrierCh为空");
            logger.info(m);
            return m;
        }

        if (StringUtils.isEmpty(shipFlight.getShipNameCh())) {
            String m = String.format("验证航班信息 shipNameCh为空");
            logger.info(m);
            return m;
        }


        if (StringUtils.isEmpty(shipFlight.getShipNo())) {
            String m = String.format("验证航班信息 shipNo为空");
            logger.info(m);
            return m;
        }

        if (shipFlight.getSailDate() == null) {
            String m = String.format("验证航班信息 sailDate为空");
            logger.info(m);
            return m;
        }


        if (StringUtils.isEmpty(shipFlight.getStartingPort())) {
            String m = String.format("验证航班信息 startingPort为空");
            logger.info(m);
            return m;
        }

        if (StringUtils.isEmpty(shipFlight.getRoutes())) {
            String m = String.format("验证航班信息 route为空");
            logger.info(m);
            return m;
        }


        return null;
    }

    /**
     * 新增航班-靠离泊计划查询接口
     * @return
     */
    @PostMapping("/listPlan")
    public ResponseEntity<String> flightListPlan(@RequestBody OneInputWithLogDTO<FlightPlanDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FlightPlanDTO paramsData = params.getData();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();

        try{
            List<FlightPlanQueryDTO> pageResult = flightPlanDao.queryFlightPlanByCarrierId(paramsData.getId());
            if(pageResult != null ){
                rowsResponseParams.setRows(pageResult);
                rowsResponseParams.setTotal((int) pageResult.size());
                rowsResponseParams.setSubtotal(pageResult.size());
            }else{
                rowsResponseParams.setRows(null);
                rowsResponseParams.setTotal(0);
                rowsResponseParams.setSubtotal(0);
            }

        }catch (Exception e){
            return responseBuilder.Error("查询靠离泊计划时，数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }


    /**
     * 靠离泊计划旅客跳转接口
     * @return
     */
    @PostMapping("/passenger/query")
    public ResponseEntity<String> flightJobHistory(@RequestBody @Validated({ValidatedGroup2.class})  QueryInputWithLogDTO<PassengerQueryDTO> params, BindingResult result, HttpServletRequest request) {
        String reqId = params.getReqId();
        OutputDTOBuilder builder = new OutputDTOBuilder(reqId);
        PassengerQueryDTO condition = params.getCondition();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        if(result.hasErrors()){
            return builder.Error(result.getFieldError().getDefaultMessage());
        }
        try{
            if(condition == null){
                logger.error("condition数据为空");
                return builder.BadRequest("数据有误，请重试");
            }

            //检查queryRequestParams参数
            String cm = PageCheckUtil.checkPageQueryParams(params);
            if (!StringUtils.isEmpty(cm)) {
                return builder.BadRequest(cm);
            }
            //根据靠离泊id查询出对应的航班
            //根据航班id查询对应的旅客
            Page<PassengerQueryDTO> resultPage = shipPassengerDao.findAllPassengerByFlightPlanId(params.getPageIndex(),params.getPageSize(),condition.getId());
            if(resultPage != null ){
                rowsResponseParams.setRows(resultPage.getContent());
                rowsResponseParams.setTotal((int) resultPage.getTotalElements());
                rowsResponseParams.setSubtotal(resultPage.getContent().size());
            }else{
                rowsResponseParams.setRows(null);
                rowsResponseParams.setTotal(0);
                rowsResponseParams.setSubtotal(0);
            }

        }catch (Exception e){
            logger.error("旅客查询，数据处理出错",e);
            return builder.Error("数据处理出错");
        }
        return builder.OK(rowsResponseParams);
    }


}
