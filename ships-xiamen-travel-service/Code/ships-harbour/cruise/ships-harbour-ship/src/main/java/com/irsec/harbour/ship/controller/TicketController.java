package com.irsec.harbour.ship.controller;


import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.entity.*;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.impl.ShipFlightImpl;
import com.irsec.harbour.ship.data.impl.ShipLuggageCheckInImpl;
import com.irsec.harbour.ship.data.impl.ShipManualCheckinImpl;
import com.irsec.harbour.ship.data.impl.ShipPassengerImpl;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.JsonUtil;
import com.irsec.harbour.ship.utils.PageCheckUtil;
import com.irsec.harbour.ship.service.CheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;


/**
 * 验票接口
 */
@RestController
@RequestMapping("api/v1/ticket")
public class TicketController {


    @Autowired
    ShipPassengerImpl shipPassengerImpl;

    @Autowired
    ShipFlightImpl shipFlightImpl;

    @Autowired
    ShipLuggageCheckInImpl shipLuggageCheckInDao;

    @Autowired
    ShipManualCheckinImpl shipManualCheckinImpl;

    @Value("${uploadImg.url}")
    private String uploadImgUrl;

    @Autowired
    CheckService checkService;

    Logger logger = LoggerFactory.getLogger(TicketController.class);

    /**
     * 验票
     */
    @PostMapping("checking")
    @Transactional
    public ResponseEntity checking(@RequestBody @Validated({ValidatedGroup1.class}) OneInputDTO<TicketCheckingInputDTO> params, BindingResult result) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        TicketCheckingInputDTO data = params.getData();
        //String checkInfo = checkTicketChecking(data);
        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        ShipManualCheckin checkin = new ShipManualCheckin();
        checkService.InitShipManualCheckin(checkin, data, 0);
        try {
            Map<String, Object> resultMap = checkService.check(checkin, data, false);
            TicketCheckingOutputDTO responseParams = (TicketCheckingOutputDTO)resultMap.get(ConstanCollection.RESULT);
            return responseBuilder.OK(responseParams);
        }catch (Exception ex) {
            logger.error("数据处理出错，错误二维码.barcode=" + data.getUserBarcode());
            logger.error(ex.getMessage());
            return responseBuilder.Error("数据处理出错.");
        }
    }


    /**
     * 离线上传
     *
     * @return
     */
    @PostMapping("officeUpload")
    public ResponseEntity officeUpload(@RequestBody ManyInputDTO<TicketCheckingInputDTO> params) {

        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        try {
            //一次最多上传50条

            //验证输入的信息
            List<TicketCheckingInputDTO> datas = params.getDatas();
            String checkInfo = checkOfflineUpdate(datas);
            if (!StringUtils.isEmpty(checkInfo)) {
                return responseBuilder.Error(checkInfo);
            }

            //数据初始化
            List<ShipManualCheckin> checkinList = new ArrayList<>();
            List<String> barcodeList = new ArrayList<>();
            for (TicketCheckingInputDTO data : datas) {
                ShipManualCheckin checkin = new ShipManualCheckin();
                checkService.InitShipManualCheckin(checkin, data, 1);
                checkinList.add(checkin);

                barcodeList.add(data.getUserBarcode());
            }

            //根据二维码批量查询旅客
            String[] barcodeArray = new String[barcodeList.size()];
            barcodeList.toArray(barcodeArray);
            List<ShipPassenger> shipPassengers = shipPassengerImpl.findAllByPassportIdIn(barcodeArray);

            //根据旅客的flightId批量查询航班
            Set<String> flightIds = new HashSet<>();
            for (ShipPassenger shipPassenger : shipPassengers) {
                flightIds.add(shipPassenger.getFlightId());
            }
            List<ShipFlight> shipFlights = shipFlightImpl.findAllById(flightIds);


            //判断是否有航班没有取到
            if (shipFlights.size() != flightIds.size()) {
                return responseBuilder.Error("通过旅客的flightId，找不到对应的航班号");
            }


            //依次判断旅客是否正常
            List<ShipPassenger> savePassenger = new ArrayList<>();
            for (ShipManualCheckin checkin : checkinList) {
                ShipPassenger shipPassenger = getPassengerByUserBarcode(shipPassengers, checkin.getBarcode());
                if (shipPassenger == null) {
                    logger.info(String.format("二维码%s没有找到对应旅客信息", checkin.getBarcode()));
                    checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_NOT_FOUND_PASSENGER);
                } else {

                    //从内存里面找到对应的航班
                    ShipFlight flight = getFlightByFlightId(shipFlights, shipPassenger);
                    if (flight == null) {
                        String m = String.format("通过旅客%s的flight，找不到对应的航班号", shipPassenger.getId());
                        logger.info(m);
                        return responseBuilder.Error(m);
                    }

                    writeCheckin(checkin, shipPassenger, flight);

                    savePassenger.add(shipPassenger);
                }
            }

            //保存到数据库
            shipManualCheckinImpl.saveAllCheckin(checkinList, savePassenger);


            return responseBuilder.OK();


        } catch (Exception ex) {

            logger.error("离线上传数据处理出错");
            logger.error(ex.getMessage());
            return responseBuilder.Error("离线上传数据处理出错.");
        }
    }

    private ShipFlight getFlightByFlightId(List<ShipFlight> shipFlights, ShipPassenger shipPassenger) {
        Optional<ShipFlight> optional = shipFlights.stream().filter(x -> x.getId().equals(shipPassenger.getFlightId())).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }


    private void writeCheckin(ShipManualCheckin checkin, ShipPassenger shipPassenger, ShipFlight flightShip) {
        //判断日期是今天
        if (DateUtil.dateEqual(flightShip.getSailDate(), new Date())) {
            //验票成功
            checkin.setVerifyResult(ConstanCollection.CHECK_SUCCESS);
            //修改passenger表的字段
            //1-验证通过
            shipPassenger.setIsCheckingTacket(ConstanCollection.PASS);
            shipPassenger.setCheckingTime(checkin.getCheckingTime());
            shipPassenger.setCheckDeviceNo(checkin.getCheckDeviceNo());

        } else if(flightShip.getSailDate().after(new Date())){
            checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_NOT_TODAY);
            shipPassenger.setIsCheckingTacket(ConstanCollection.NOT_PASS);
        }else {
            checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_ERROR_CODE);
            shipPassenger.setIsCheckingTacket(ConstanCollection.NOT_PASS);
        }

        //旅客信息
        checkin.setPassengerId(shipPassenger.getId());
        checkin.setPassengerName(shipPassenger.getPassengerNameCh());
        checkin.setPassportId(shipPassenger.getPassportId());
        checkin.setIdNumber(shipPassenger.getIdNumber());
        checkin.setContact(shipPassenger.getContact());
        checkin.setCountry(shipPassenger.getCountry());

        //航班信息
        checkin.setShipNo(flightShip.getShipNo());
        checkin.setFlightId(flightShip.getId());

    }


    private ShipPassenger getPassengerByUserBarcode(List<ShipPassenger> shipPassengers, String userBarcode) {
        for (ShipPassenger passenger : shipPassengers) {
            if (userBarcode.equals(passenger.getUserBarcode())) {
                return passenger;
            }
        }

        return null;
    }


    /**
     * 查询验票记录
     */
    @RequestMapping(value = "checking/query", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity queryChecking(@RequestBody QueryInputDTO<TicketCheckingConditionDTO> queryRequestParams) {
        OutputDTOBuilder responseDtoBuilder = new OutputDTOBuilder(queryRequestParams.getReqId());
        TicketCheckingConditionDTO condition = queryRequestParams.getCondition();

        if (condition == null) {
            return responseDtoBuilder.BadRequest("condition 不允许为空");
        }

        //检查queryRequestParams参数
        String cm = PageCheckUtil.checkPageQueryParams(queryRequestParams);
        if (!StringUtils.isEmpty(cm)) {
            return responseDtoBuilder.BadRequest(cm);
        }
        try {
            //List<ShipManualCheckin> resultData = new ArrayList<>();
            Page<Map<String,Object>>  reultMapList = shipManualCheckinImpl.getManualCheckInRecordByCondition(false,condition, queryRequestParams.getPageIndex(), queryRequestParams.getPageSize());

            RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
            rowsResponseParams.setTotal((int) reultMapList.getTotalElements());
            rowsResponseParams.setSubtotal(reultMapList.getContent().size());
            rowsResponseParams.setRows(reultMapList.getContent());
            return responseDtoBuilder.OK(rowsResponseParams);
        } catch (Exception ex) {
            logger.error("查询验票信息出现错误");
            logger.error(ex.getMessage());
            return responseDtoBuilder.Error("查询验票信息出现内部错误");
        }
    }




    //检测旅客信息
    private String checkTicketChecking(TicketCheckingInputDTO data) {

        if (data == null) {
            String m = "data 参数为空";
            logger.info(m);
            return m;
        }

        String barcode = data.getUserBarcode();

        if(StringUtils.isEmpty(data.getCheckinId())){
            String m = "没有CheckInId";
            logger.info(m);
            return m;
        }

        if (StringUtils.isEmpty(barcode)) {
            String m = "没有二维码信息";
            logger.info(m);
            return m;
        }

        if (data.getCheckingTime() == null) {
            String m = "没有检票时间";
            logger.info(m);
            return m;
        }


        if (StringUtils.isEmpty(data.getCheckDeviceNo())) {
            String m = "没有设备编号";
            logger.info(m);
            return m;
        }

        return null;
    }


    //检测旅客信息
    private String checkOfflineUpdate(List<TicketCheckingInputDTO> datas) {

        if (datas == null) {
            String m = "datas 参数为空";
            logger.info(m);
            return m;
        }

        if (datas.size() == 0) {
            String m = "datas 长度为0";
            logger.info(m);
            return m;
        }


        if (datas.size() > 50) {
            String m = "datas 不能大于50";
            logger.info(m);
            return m;
        }

        for (int i = 0; i < datas.size(); i++) {
            TicketCheckingInputDTO data = datas.get(i);

            String m = checkTicketChecking(data);
            if (!StringUtils.isEmpty(m)) {
                return String.format("第%d行 %s", i, m);
            }
        }
        return null;
    }

    /**
     * 验票核销
     */
    @RequestMapping(value = "cancel", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity queryChecking(@RequestBody  @Validated({FaceCheckDTO.ValidateGroupCancel.class}) OneInputDTO<FaceCheckDTO> params, BindingResult result) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        FaceCheckDTO data = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(data.getType() == 0){
                int res = shipManualCheckinImpl.cancelAfterVerification(data.getId());
                if(res != 1){
                    return responseBuilder.Error("该记录不存在");
                }
            }else{
                List<ShipLuggageCheckin> list = shipLuggageCheckInDao.findAllByLuggageCode(data.getId());
                if(!CollectionUtils.isEmpty(list)){
                    shipLuggageCheckInDao.cancelAfterVerification(list);
                }
            }
            return responseBuilder.OK();
        }catch (Exception e){
            logger.error("核销验票记录出错: {}",e);
            return responseBuilder.Error("核销验票记录出错");
        }
    }

}
