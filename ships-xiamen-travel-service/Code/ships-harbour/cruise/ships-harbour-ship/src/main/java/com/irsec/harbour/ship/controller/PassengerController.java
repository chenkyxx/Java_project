package com.irsec.harbour.ship.controller;


import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.entity.*;
import com.irsec.harbour.ship.data.impl.*;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.utils.*;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.irsec.harbour.ship.utils.UUIDTool.getStrOutOfOrder;

/**
 * 旅客接口
 */
@RestController
@RequestMapping("api/v1/passenger")
public class PassengerController {

    @Autowired
    ShipPassengerImpl shipPassengerImpl;

    @Autowired
    ShipFlightImpl shipFlightImpl;

    @Autowired
    ShipManualCheckinImpl shipManualCheckinImpl;

    @Autowired
    ShipLuggageCodeImpl shipLuggageCodeImpl;

    @Autowired
    ShipLuggageCheckInImpl shipLuggageCheckInImpl;

    Logger logger = LoggerFactory.getLogger(PassengerController.class);

    /**
     * 一次增加多名旅客
     *
     * @param params
     * @return
     */
    @PostMapping("add")
    public ResponseEntity addList(@RequestBody ManyInputDTO<ShipPassenger> params) {
        String reqId = params.getReqId();
        List<ShipPassenger> passengers = params.getDatas();
        List<ShipLuggageCode> shipLuggageCodeList = new ArrayList<>();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        try {
            //判断旅客信息
            String checkInfo = checkAddPassengerInfo(passengers);
            if (!StringUtils.isEmpty(checkInfo)) {
                return responseBuilder.Error(checkInfo);
            }
            //获取所有的航班号
            Set<String> allFlights = new HashSet<>();
            for (ShipPassenger shipPassenger : passengers) {
                allFlights.add(shipPassenger.getFlightId());
            }
            //获取航班信息
            List<ShipFlight> shipFlightList = shipFlightImpl.findAllById(allFlights);
            List<Map<String, Object>> resultData = new ArrayList();
            for (ShipPassenger passenger : passengers) {
                //新生产id
                passenger.setId(UUIDTool.newUUID());

                //航班信息从航班中获取
                Optional<ShipFlight> optional = shipFlightList.stream().filter(flight -> passenger.getFlightId().equals(flight.getId())).findAny();
                if (!optional.isPresent()) {
                    String m = "没有找到对应的航班，请检查flightId,flightId=" + passenger.getFlightId();
                    logger.info(m);
                    return responseBuilder.Error(m);
                }
                ShipFlight shipFlight = optional.get();

                //复制航班信息
                BeanUtils.copyProperties(shipFlight, passenger, "id");

                //新生产二维码
                //getNewBarcode(passenger);
                //passenger.setUserBarcode(passenger.getUserBarcode());

                //生成对应的行李条码
                List<ShipLuggageCode> shipLuggageCodes = generateShipLuggageCode(3,passenger);

                shipLuggageCodeList.addAll(shipLuggageCodes);
                //其他字段置空
                passenger.setIsCheckingTacket(ConstanCollection.NOT_CHECK);
                passenger.setCheckingTime(null);
                passenger.setIsPrint(ConstanCollection.UNPRINTED);
                passenger.setPrintTime(null);

                //组织返回的数据
                Map<String, Object> map = new HashMap<>();
                map.put("userId", passenger.getUserId());
                map.put("userBarcode", passenger.getUserBarcode());
                resultData.add(map);
            }
            shipPassengerImpl.saveAll(passengers);

            shipLuggageCodeImpl.saveAll(shipLuggageCodeList);

            logger.info("数据库写入成功旅客数据成功,旅客信息 = " + passengers.toString());

            RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
            rowsResponseParams.setTotal(resultData.size());
            rowsResponseParams.setSubtotal(resultData.size());
            rowsResponseParams.setRows(resultData);

            return responseBuilder.OK(rowsResponseParams);

        } catch (DataIntegrityViolationException ex) {
            logger.error("增加旅客请求-旅客信息写入数据库错误");
            logger.error(ex.getMessage());
            return responseBuilder.Error("旅客信息写入数据库错误 可能是userId重复");
        } catch (Exception ex) {
            logger.error("增加旅客请求-旅客信息写入数据库错误",ex);
            logger.error(ex.getMessage());

            return responseBuilder.Error("旅客信息写入数据库错误");
        }
    }

    /**
     * 生成该旅客对应的行李条并保存
     * @param num
     * @param passenger
     */
    private List<ShipLuggageCode> generateShipLuggageCode(int num, ShipPassenger passenger) {
        List<ShipLuggageCode> list = new ArrayList<>();

        for(int i=0; i<num;i++){
            ShipLuggageCode shipLuggageCode = new ShipLuggageCode();
            shipLuggageCode.setId(UUIDTool.newUUID());
            shipLuggageCode.setLuggageCode(UUIDTool.getRandomCh()+getStrOutOfOrder(passenger.getPassportId()));
            shipLuggageCode.setPassengerId(passenger.getId());
            shipLuggageCode.setIsPrint(ConstanCollection.UNPRINTED);
            list.add(shipLuggageCode);
        }
        return list;
    }


    /**
     * 删除旅客
     */
    @PostMapping("delete")
    public ResponseEntity delete(@RequestBody ManyInputDTO<ShipPassenger> params) {
        String reqId = params.getReqId();
        List<ShipPassenger> passengers = params.getDatas();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        //判断旅客信息
        String checkInfo = checkDeletePassengerInfo(passengers);
        if (!StringUtils.isEmpty(checkInfo)) {
            return responseBuilder.Error(checkInfo);
        }


        String[] userIdList = new String[passengers.size()];
        for (int i = 0; i < userIdList.length; i++) {
            userIdList[i] = passengers.get(i).getUserId();
        }

        try {
            List<ShipPassenger> allByUserIdIn = shipPassengerImpl.findAllByUserIdIn(userIdList);

            String[] passerId = allByUserIdIn.stream().map(x -> x.getId()).toArray(String[]::new);

            List<ShipManualCheckin> checkinList = shipManualCheckinImpl.findAllByPassengerIdIn(passerId);
            if (checkinList.size() > 0) {
                List<String> ps = Lists.newArrayList(checkinList).stream().filter(s -> s.getVerifyResult() == ConstanCollection.CHECK_SUCCESS ).map(s -> s.getPassengerName()).collect(Collectors.toList());
                HashSet<String> nameSet = new HashSet<>(ps.size());
                for(String name : ps){
                    nameSet.add(name);
                }
                if(!ps.isEmpty()){
                    return responseBuilder.Error(String.format("旅客[%s]已经验票通过，不能删除。", nameSet.stream().collect(Collectors.joining(","))));
                }
            }
            List<ShipLuggageCheckin> luggageCheckinList = shipLuggageCheckInImpl.findAllByPassenegrIdIn(passerId);
            if(luggageCheckinList.size()>0){
                List<String> ps = Lists.newArrayList(luggageCheckinList).stream().filter(s -> s.getVerifyResult() == ConstanCollection.CHECK_SUCCESS).map(s -> s.getShipPassenger().getPassengerNameCh()).collect(Collectors.toList());

                HashSet<String> nameSet = new HashSet<>(ps.size());
                for(String name : ps){
                    nameSet.add(name);

                }

                if(!ps.isEmpty()){
                    return responseBuilder.Error(String.format("旅客[%s]行李条验证通过，不能删除。", nameSet.stream().collect(Collectors.joining(","))));
                }
            }
            List<String> luggageCheckPassPassengerIds = luggageCheckinList.stream().filter(l -> l.getVerifyResult() == ConstanCollection.CHECK_SUCCESS).map(l->l.getPassengerId()).collect(Collectors.toList());
            List<String> ticketCheckPassPassengerIds = checkinList.stream().filter(c -> c.getVerifyResult() == ConstanCollection.CHECK_SUCCESS).map(c->c.getPassengerId()).collect(Collectors.toList());

            List<ShipPassenger> notPassPassengerIds = allByUserIdIn.stream().filter(p -> {
                boolean isFind = true;
                if(luggageCheckPassPassengerIds.contains(p.getId()) || ticketCheckPassPassengerIds.contains(p.getId())){
                    isFind = false;
                }
                return isFind;
            }).collect(Collectors.toList());

            if(!notPassPassengerIds.isEmpty()){
                shipPassengerImpl.deleteAllByUserIdIn(notPassPassengerIds);
                logger.info("数据库中删除旅客成功 ");
            }
            return responseBuilder.OK();

        } catch (Exception ex) {
            logger.error("删除旅客请求-旅客信息写入数据库错误");
            logger.error(ex.getMessage());

            return responseBuilder.Error("旅客信息从数据库中删除错误.");
        }

    }

    /**
     * 根据护照编号或者身份证号进行查询
     *
     * @return
     */
    @PostMapping("queryByPrinter")
    public ResponseEntity queryByPassport(@RequestBody QueryInputDTO<PassengerConditionDTO> queryRequestParams) {
        String reqId = queryRequestParams.getReqId();
        PassengerConditionDTO condition = queryRequestParams.getCondition();

        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        if (condition == null) {
            logger.info("需要查询的旅客信息不允许为空-condition数据错误");
            return responseBuilder.BadRequest("condition参数错误");
        }


        //检查queryRequestParams参数
        String cm = PageCheckUtil.checkPageQueryParams(queryRequestParams);
        if (!StringUtils.isEmpty(cm)) {
            return responseBuilder.BadRequest(cm);
        }

        if(StringUtils.isEmpty(condition.getPassportId()) && !StringUtils.isEmpty(condition.getCertificateType())){
            logger.info("需要查询的旅客信息 旅行证件号码不能为空");
            return responseBuilder.BadRequest("需要查询的旅客信息 旅行证件号码不能为空");
        }
        if(!StringUtils.isEmpty(condition.getPassportId()) && StringUtils.isEmpty(condition.getCertificateType())){
            logger.info("需要查询的旅客信息 旅行证件类型不能为空");
            return responseBuilder.BadRequest("需要查询的旅客信息 旅行证件类型不能为空");
        }

        //如果身份证或护照号码都为空
        if (StringUtils.isEmpty(condition.getPassportId()) && StringUtils.isEmpty(condition.getIdNumber())) {
            logger.info("需要查询的旅客信息身份证号码与护照号码不能都为空");
            return responseBuilder.BadRequest("需要查询的旅客信息身份证号码与护照号码不能都为空");
        }

        Specification<ShipPassenger> specification = (root, query, cb) ->
        {
            List<Predicate> predicateList = new ArrayList();

            if(!StringUtils.isEmpty(condition.getCertificateType()) && !StringUtils.isEmpty(condition.getPassportId())){
                predicateList.add(cb.equal(root.get("passportId"), condition.getPassportId()));
                predicateList.add(cb.equal(root.get("certificateType"),condition.getCertificateType()));
            }

/*            if (!StringUtils.isEmpty(condition.getPassportId())) {
                Predicate predicate = cb.equal(root.get("passportId"), condition.getPassportId());
                predicateList.add(predicate);
            }*/

            if (!StringUtils.isEmpty(condition.getIdNumber())) {
                Predicate predicate = cb.equal(root.get("idNumber"), condition.getIdNumber());
                predicateList.add(predicate);
            }

            if (condition.getSailDateSt() != null) {
                Date d1 = condition.getSailDateSt();

                Predicate predicate = cb.greaterThanOrEqualTo(root.get("sailDate"), d1);
                predicateList.add(predicate);
            }

            if (condition.getSailDateEnd() != null) {
                Date d1 = condition.getSailDateEnd();

                Predicate predicate = cb.lessThanOrEqualTo(root.get("sailDate"), d1);
                predicateList.add(predicate);
            }

            Predicate[] predicates = new Predicate[predicateList.size()];
            return cb.and(predicateList.toArray(predicates));
        };

        try {
            Page<ShipPassenger> pageData = shipPassengerImpl.findAll(specification, PageRequest.of(queryRequestParams.getPageIndex(), queryRequestParams.getPageSize()));

            //List<ShipPassenger> resultData = new ArrayList();
            List<HashMap<String,Object>> result = new ArrayList<>();
            if (pageData != null) {
                for (ShipPassenger shipPassenger : pageData) {
                    result.add(shipPassengerImpl.getPassengerAndFlightAndLuggages(shipPassenger));
                }
            }
            RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
            rowsResponseParams.setRows(result);
            rowsResponseParams.setTotal((int) pageData.getTotalElements());
            rowsResponseParams.setSubtotal(result.size());

            logger.info("执行旅客查询接口成功");

            return responseBuilder.OK(rowsResponseParams);

        } catch (Exception e) {
            logger.error("查询旅客信息by护照号出错");
            logger.error(e.getMessage());

            return responseBuilder.Error("查询旅客信息by护照号出错");
        }


    }


    //检测旅客信息
    private String checkAddPassengerInfo(List<ShipPassenger> passengers) {
        String m1 = checkPassengersInfo(passengers);
        if (!StringUtils.isEmpty(m1)) return m1;


        //判断userId是否为空
        for (int i = 0; i < passengers.size(); i++) {
            ShipPassenger passenger = passengers.get(i);
            if (StringUtils.isEmpty(passenger.getUserId())) {
                String m = String.format("验证旅客信息 第%d个数据，userId为空", i);
                logger.info(m);
                return m;
            }
            if(StringUtils.isEmpty(passenger.getCertificateType())){
                String m = String.format("验证旅客信息 第%d个数据，证件类型 为空", i);
                logger.info(m);
                return m;
            }
            if (StringUtils.isEmpty(passenger.getPassportId())) {
                String m = String.format("验证旅客信息 第%d个数据，证件号为空", i);
                logger.info(m);
                return m;
            }

//            if (StringUtils.isEmpty(passenger.getFlightId())) {
//                String m = String.format("验证旅客信息 第%d个数据，flightId为空", i);
//                logger.info(m);
//                return m;
//            }

//            if(StringUtils.isEmpty(passenger.getPassengerNameEn())){
//                String m = String.format("验证旅客信息 第%d个数据，passengerNameEn为空", i);
//                logger.info(m);
//                return m;
//            }

//            if (StringUtils.isEmpty(passenger.getPassengerNameCh())) {
//                String m = String.format("验证旅客信息 第%d个数据，passengerName为空", i);
//                logger.info(m);
//                return m;
//            }

//            if(StringUtils.isEmpty(passenger.getRoomNo())){
//                String m = String.format("验证旅客信息 第%d个数据，RoomNo为空", i);
//                logger.info(m);
//                return m;
//            }
//
//            if(StringUtils.isEmpty(passenger.getFloor())){
//                String m = String.format("验证旅客信息 第%d个数据，楼层 为空", i);
//                logger.info(m);
//                return m;
//            }



//            if(!StringUtils.isEmpty(passenger.getCertificateType())){
//                if(passenger.getCertificateType() != 1 && passenger.getCertificateType() != 2 && passenger.getCertificateType() != 3){
//                    String m = String.format("验证旅客信息 第%d个数据，旅行证件类型不是 ：护照，港澳通行证，台湾通行证中的一种", i);
//                    logger.info(m);
//                    return m;
//                }
//            }else{
//                String m = String.format("验证旅客信息 第%d个数据，证件类型 为空", i);
//                logger.info(m);
//                return m;
//            }

//            if(!StringUtils.isEmpty(passenger.getSex())){
//                if (!("F".equals(passenger.getSex()) || "M".equals(passenger.getSex()))) {
//                    String m = String.format("验证旅客信息 第%d个数据, sex 不符合标准 M-男 F-女", i);
//                    logger.info(m);
//                    return m;
//                }
//            }

/*            if (StringUtils.isEmpty(passenger.getCountry())) {
                String m = String.format("验证旅客信息 第%d个数据，country为空", i);
                logger.info(m);
                return m;
            }*/


/*            if (StringUtils.isEmpty(passenger.getPassportValidity())) {
                String m = String.format("验证旅客信息 第%d个数据，passportValidity为空", i);
                logger.info(m);
                return m;
            }*/

//            if (StringUtils.isEmpty(passenger.getUserBarcode())) {
//                String m = String.format("验证旅客信息 第%d个数据，userBarcode为空", i);
//                logger.info(m);
//                return m;
//            }


/*            if(!StringUtils.isEmpty(passenger.getTouristType()) && passenger.getTouristType() == 0){
            }
            if(!StringUtils.isEmpty(passenger.getTouristType()) && passenger.getTouristType() == 0){
                if(StringUtils.isEmpty(passenger.getTouristIdentity())){
                    String m = String.format("验证旅客信息 第%d个数据，touristIdentity为空", i);
                    logger.info(m);
                    return m;
                }
                if(passenger.getTouristIdentity().compareTo("领队") != 0  && passenger.getTouristIdentity().compareTo("乘客") != 0 && passenger.getTouristIdentity().compareTo("员工") != 0){
                    String m = String.format("验证旅客信息 第%d个数据，touristIdentity的值只能是 '领队','乘客','员工' 中的一个", i);
                    logger.info(m);
                    return m;
                }

                if(StringUtils.isEmpty(passenger.getGroupNo())){
                    String m = String.format("验证旅客信息 第%d个数据，groupNo为空", i);
                    logger.info(m);
                    return m;
                }
            }*/

        }


        //判断数组中的 userId，否存在空
        List<String> repeatUserId = new ArrayList();
        for (int i = 0; i < passengers.size(); i++) {
            ShipPassenger passenger = passengers.get(i);
            if (repeatUserId.contains(passenger.getUserId())) {
                String m = String.format("验证旅客信息 第%d个数据，userId重复", i);
                logger.info(m);
                return m;
            } else {
                repeatUserId.add(passenger.getUserId());
            }
        }


        return "";
    }

    //检测旅客信息
    private String checkDeletePassengerInfo(List<ShipPassenger> passengers) {
        String m1 = checkPassengersInfo(passengers);
        if (!StringUtils.isEmpty(m1)) return m1;

        //判断userId是否为空
        for (int i = 0; i < passengers.size(); i++) {
            ShipPassenger passenger = passengers.get(i);
            if (StringUtils.isEmpty(passenger.getUserId())) {
                String m = String.format("验证旅客信息 第%d个数据，userId为空", i);
                logger.info(m);
                return m;
            }
        }


        return null;
    }

    //检测旅客的数组
    private String checkPassengersInfo(List<ShipPassenger> passengers) {
        if (passengers == null) {
            String m = "验证旅客信息，datas 数据为空";
            logger.info(m);
            return m;
        }

        if (passengers.size() == 0) {
            String m = "验证旅客信息，datas 参数长度为0，没有数据";
            logger.info(m);
            return m;
        }

//        if (passengers.size() > 1000) {
//            String m = "验证旅客信息，datas 数据量超过1000，请分配上传/删除";
//            logger.info(m);
//            return m;
//        }

        return null;
    }


    /**
     * 根据前缀获得一个行李条
     * @param prefix
     * @param barCode
     * @return
     */
    public String encodeBarCodeToLuggageCode(String prefix,String barCode){
        barCode = prefix +barCode;
        StringBuffer sb = new StringBuffer();
        byte[] barCodeBytes = barCode.getBytes();
        byte[] luggageCodeBytes = new byte[barCodeBytes.length];
        for(int i=0; i< barCodeBytes.length; i++){
            int temp = ~barCodeBytes[i];
            luggageCodeBytes[i] = (byte) temp;
        }
        sb.append(luggageCodeBytes);
        return  sb.toString();
    }




    /**
     * 更新旅客的打印凭证信息
     *
     */
    @PostMapping("status")
    public ResponseEntity printStatus(@RequestBody @Validated({PrintStatusDTO.ValidatedGroupPrintStatus.class}) OneInputDTO<PrintStatusDTO> params, BindingResult result) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        if(result.hasErrors()){
            logger.error(result.getFieldError().getDefaultMessage());
            return responseBuilder.BadRequest(result.getFieldError().getDefaultMessage());
        }
        PrintStatusDTO data = params.getData();

        try {
            if(data.getPrintType() == 0){
                //更新旅客船票的打印状态
                ShipPassenger shipPassenger = shipPassengerImpl.updatePrintStatus(data.getPassengerId(),data.getCode());
                if(shipPassenger == null){
                    throw new EntityNotFoundException();
                }
                logger.info("修改旅客凭证打印状态成功， 旅客id：{}, 旅客船票code ：{}",shipPassenger.getId(),data.getCode());
            }else if(data.getPrintType() == 1){
                //更新；旅客行李条的打印状态
                ShipLuggageCode shipLuggageCode = shipLuggageCodeImpl.updatePrintStatus(data.getPassengerId(),data.getCode());
                if(shipLuggageCode == null){
                    throw new EntityNotFoundException();
                }
                logger.info("修改旅客凭证打印状态成功, 旅客id：{},旅客行李条code ：{}",shipLuggageCode.getPassengerId() ,data.getCode());
            }

            //行李条的打印情况
        }catch (EntityNotFoundException e){
            logger.error("修改旅客凭证打印状态-不存在该条记录");
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (EmptyResultDataAccessException e){
            logger.error("修改旅客凭证打印状态-不存在该条记录");
            return responseBuilder.BadRequest("不存在该条记录");
        } catch (Exception ex) {
            logger.error("修改旅客凭证打印状态-数据处理错误");
            logger.error(ex.getMessage());
            return responseBuilder.Error("修改旅客凭证打印状态-数据处理错误.");
        }
        return responseBuilder.OK();
    }

    /**
     * 旅客查询接口
     * @return
     */
    @PostMapping("/query")
    public ResponseEntity<String> query(@RequestBody QueryInputWithLogDTO<PassengerQueryConditionDTO> params, BindingResult result, HttpServletRequest request) {
        String reqId = params.getReqId();
        OutputDTOBuilder builder = new OutputDTOBuilder(reqId);
        PassengerQueryConditionDTO condition = params.getCondition();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        if(result.hasErrors()){
            return builder.Error(result.getFieldError().getDefaultMessage());
        }
        try{
/*            if(condition == null){
                logger.error("condition数据为空");
                return builder.BadRequest("数据有误，请重试");
            }else {

                if((StringUtils.isEmpty(condition.getIdNumber())&&!StringUtils.isEmpty(condition.getType()))  || (!StringUtils.isEmpty(condition.getIdNumber())&&StringUtils.isEmpty(condition.getType()))){
                    logger.error("查询条件 idNumber 和 type 有一个为空");
                    return builder.BadRequest("查询条件 idNumber 和 type 必须都不为空或者都为空");
                }
            }*/
            //检查queryRequestParams参数
            String cm = PageCheckUtil.checkPageQueryParams(params);
            if (!StringUtils.isEmpty(cm)) {
                return builder.BadRequest(cm);
            }
            //根据靠离泊id查询出对应的航班
            //根据航班id查询对应的旅客
            Page<PassengerQueryDTO> resultPage = shipPassengerImpl.findAllPassengerByCondition(params.getPageIndex(),params.getPageSize(),condition);

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

    public static String getPassengerName(ShipPassenger shipPassenger){
        if(StringUtils.isEmpty(shipPassenger.getPassengerNameCh())){
            return shipPassenger.getPassengerNameEn();
        }
        return shipPassenger.getPassengerNameCh();
    }
}
