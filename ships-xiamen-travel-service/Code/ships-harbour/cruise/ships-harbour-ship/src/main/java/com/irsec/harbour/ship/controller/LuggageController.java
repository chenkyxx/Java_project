package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.dao.LuggageCheckInDao;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.*;
import com.irsec.harbour.ship.data.group.ValidatedGroupLog;
import com.irsec.harbour.ship.data.impl.ShipFlightImpl;
import com.irsec.harbour.ship.data.impl.ShipLuggageCheckInImpl;
import com.irsec.harbour.ship.data.impl.ShipLuggageCodeImpl;
import com.irsec.harbour.ship.data.impl.ShipPassengerImpl;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.PageCheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

/**
 * @Auther: Jethro
 * @Date: 2019/8/31 14:28
 * @Description: 行李条相关接口
 */

@RestController
@RequestMapping("/api/v1/ticket/luggage")
public class LuggageController {

    Logger logger = LoggerFactory.getLogger(LuggageController.class);

    @Autowired
    ShipPassengerImpl shipPassengerImpl;

    @Autowired
    ShipFlightImpl shipFlightImpl;

    @Autowired
    ShipLuggageCheckInImpl shipLuggageCheckInDao;

    @Autowired
    ShipLuggageCodeImpl shipLuggageCodeDao;

    @PostMapping("/check")
    @Transactional
    public ResponseEntity luggageChecking(@RequestBody @Validated OneInputDTO<LuggageCheckInputDTO> params, BindingResult result) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        if(result.hasErrors()){
            logger.error(result.getFieldError().getDefaultMessage());
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }
        LuggageCheckInputDTO data = params.getData();

        ShipLuggageCheckin checkin = new ShipLuggageCheckin();

        initLuggageCheckIn(checkin,data,0);

        try {
            //检查该checkInid是否存在


            if(shipLuggageCheckInDao.existById(data.getCheckinId())){
                logger.info("CheckInId:{} 已存在,不进行验证",data.getCheckinId());
                return responseBuilder.OK();
            }
            ShipLuggageCode shipLuggageCode = shipLuggageCodeDao.findByLuggageCode(data.getLuggageCode());
            if(shipLuggageCode == null){
                logger.error("不存在该条记录,二维码："+data.getLuggageCode());
                notFoundLuggageCode(checkin);
                //当前设备验证行李的数量
                int verifyTotal =  shipLuggageCheckInDao.getTodayPassNumberByDeviceNo(data.getCheckDeviceNo());
                TicketCheckingOutputDTO responseParams = new TicketCheckingOutputDTO();
                responseParams.setVerifyResult(checkin.getVerifyResult());
                responseParams.setMsg("二维码："+data.getLuggageCode()+" 不存在");
                responseParams.setVerifyTotal(verifyTotal);

                return responseBuilder.OK(responseParams);
            }
            checkin.setLuggageId(shipLuggageCode.getId());
            ShipPassenger shipPassenger = shipPassengerImpl.findById(shipLuggageCode.getPassengerId());

            if (shipPassenger == null) {
                //保存此次验票信息
                logger.error("行李条二维码 %d 没有找到对应旅客信息", data.getLuggageCode());
                checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_NOT_FOUND_PASSENGER);
                shipLuggageCheckInDao.save(checkin);
                //当前设备验证行李的数量
                int verifyTotal =  shipLuggageCheckInDao.getTodayPassNumberByDeviceNo(data.getCheckDeviceNo());
                TicketCheckingOutputDTO responseParams = new TicketCheckingOutputDTO();
                responseParams.setVerifyResult(checkin.getVerifyResult());
                responseParams.setMsg("二维码没有找到对应的旅客");
                responseParams.setVerifyTotal(verifyTotal);
                return responseBuilder.OK(responseParams);
            } else {
                //获取航班信息
                ShipFlight flightShip = shipPassenger.getShipFlight();
                if (flightShip == null) {
                    return responseBuilder.Error("通过旅客的flight，找不到对应的航班号");
                }
                //判断日期是今天
                writeCheckin(checkin, shipPassenger, flightShip);
                //保存到数据库中
                shipLuggageCheckInDao.save(checkin);

                TicketCheckingOutputDTO responseParams = new TicketCheckingOutputDTO();

                //获取到指定格式的返回信息
                HashMap<String,Object> resultMap =shipPassengerImpl.getPassengerAndFlightAndLuggages(shipPassenger);
                //当前设备验证行李的数量
                int verifyTotal =  shipLuggageCheckInDao.getTodayPassNumberByDeviceNo(data.getCheckDeviceNo());
                responseParams.setData(resultMap);
                responseParams.setVerifyResult(checkin.getVerifyResult());

                responseParams.setVerifyTotal(verifyTotal);
                return responseBuilder.OK(responseParams);
            }
        } catch (EntityNotFoundException e){
            //没有找到这个行李条二维码
            logger.error("不存在该条记录,二维码："+data.getLuggageCode());
            notFoundLuggageCode(checkin);
            return responseBuilder.BadRequest("不存在该条记录,二维码："+data.getLuggageCode());
        }catch (EmptyResultDataAccessException e){
            //没有找到这个行李条二维码
            logger.error("不存在该条记录,二维码："+data.getLuggageCode());
            notFoundLuggageCode(checkin);
            return responseBuilder.BadRequest("不存在该条记录,二维码："+data.getLuggageCode());
        }catch (Exception ex) {
            logger.error("数据处理出错，错误二维码.barcode=" + data.getLuggageCode());
            logger.error(ex.getMessage());
            return responseBuilder.Error("数据处理出错.");
        }
    }
    private void notFoundLuggageCode(ShipLuggageCheckin checkin){
        checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_ERROR_CODE);
        shipLuggageCheckInDao.save(checkin);
    }
    /**
     * 初始化行李条检查记录
     * @param checkin
     * @param data
     * @param optType 同旅客验票
     */
    private void initLuggageCheckIn(ShipLuggageCheckin checkin, LuggageCheckInputDTO data,int optType) {
        checkin.setId(data.getCheckinId());
        checkin.setOperationType(optType);
        checkin.setDeviceNo(data.getCheckDeviceNo());
        checkin.setLuggageCode(data.getLuggageCode());
        checkin.setVerifyTime(data.getCheckingTime());
    }
    private void writeCheckin(ShipLuggageCheckin checkin, ShipPassenger shipPassenger, ShipFlight flightShip) {
        //判断日期是今天
        if (DateUtil.dateEqual(flightShip.getSailDate(), new Date())) {
            //验票成功
            checkin.setVerifyResult(ConstanCollection.CHECK_SUCCESS);
        } else if(flightShip.getSailDate().after(new Date())){
            checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_NOT_TODAY);
        } else {
            checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_ERROR_CODE);
        }
        //旅客信息
        checkin.setPassengerId(shipPassenger.getId());
        //航班信息
        checkin.setFlightId(flightShip.getId());
    }

    /**
     * 离线上传
     *
     * @return
     */
    @PostMapping("/offlineCheck")
    public ResponseEntity offlineCheck(@RequestBody @Validated ManyInputDTO<LuggageCheckInputDTO> params, BindingResult result) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        if(result.hasErrors()){
            logger.error(result.getFieldError().getDefaultMessage());
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try {
            //一次最多上传50条
            //验证输入的信息
            List<LuggageCheckInputDTO> datas = params.getDatas();
            if(datas == null || datas.isEmpty()){
                return responseBuilder.Error("数据不能为空");
            }else if(datas.size() > 50){
                return responseBuilder.Error("datas 不能大于50");
            }

            for(LuggageCheckInputDTO data : datas){

                ShipLuggageCheckin checkin = new ShipLuggageCheckin();
                initLuggageCheckIn(checkin,data,1);
                try{
                    if(shipLuggageCheckInDao.existById(data.getCheckinId())){
                        logger.info("CheckInId:{} 已存在,不进行验证",data.getCheckinId());
                        continue;
                    }

                    ShipLuggageCode shipLuggageCode = shipLuggageCodeDao.findByLuggageCode(data.getLuggageCode());
                    if(shipLuggageCode == null){
                        logger.error("不存在该条记录,二维码："+data.getLuggageCode());
                        notFoundLuggageCode(checkin);
                        continue;
                        //return responseBuilder.BadRequest("不存在该条记录,二维码："+data.getLuggageCode());
                    }
                    checkin.setLuggageId(shipLuggageCode.getId());
                    ShipPassenger shipPassenger = shipPassengerImpl.findById(shipLuggageCode.getPassengerId());

                    if (shipPassenger == null) {
                        //保存此次验票信息
                        logger.error("行李条二维码 %d 没有找到对应旅客信息", data.getLuggageCode());
                        checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_NOT_FOUND_PASSENGER);
                        shipLuggageCheckInDao.save(checkin);
                    } else {
                        //获取航班信息
                        ShipFlight flightShip = shipPassenger.getShipFlight();
                        if (flightShip == null) {
                            logger.error("通过旅客的flight，找不到对应的航班号,旅客id:{}",shipPassenger.getId());
                        }
                        //判断日期是今天
                        writeCheckin(checkin, shipPassenger, flightShip);
                        //保存到数据库中
                        shipLuggageCheckInDao.save(checkin);
                    }
                }catch (EntityNotFoundException e){
                    //没有找到这个行李条二维码
                    logger.error("不存在该条记录,二维码："+data.getLuggageCode());
                    notFoundLuggageCode(checkin);
                    return responseBuilder.OK("不存在该条记录,二维码："+data.getLuggageCode());
                }catch (EmptyResultDataAccessException e){
                    //没有找到这个行李条二维码
                    logger.error("不存在该条记录,二维码："+data.getLuggageCode());
                    notFoundLuggageCode(checkin);
                    return responseBuilder.OK("不存在该条记录,二维码："+data.getLuggageCode());
                }
            }
            return responseBuilder.OK();
        } catch (Exception ex) {
            logger.error("离线上传数据处理出错");
            logger.error(ex.getMessage());
            return responseBuilder.Error("离线上传数据处理出错.");
        }
    }


    @PostMapping("/query")
    public ResponseEntity query(@RequestBody QueryInputDTO<TicketCheckingConditionDTO> queryRequestParams) {
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
        try{
            Page<LuggageQueryDTO> page = shipLuggageCheckInDao.findByCondition(queryRequestParams.getPageIndex(),queryRequestParams.getPageSize(),condition);
            RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
            rowsResponseParams.setTotal((int) page.getTotalElements());
            rowsResponseParams.setSubtotal(page.getContent().size());
            rowsResponseParams.setRows(page.getContent());

            return responseDtoBuilder.OK(rowsResponseParams);
        }catch (Exception e){
            logger.error("查询行李条验证记录时，数据处理出错");
            return responseDtoBuilder.Error("查询行李条验证记录时，数据处理出错");
        }

    }
}
