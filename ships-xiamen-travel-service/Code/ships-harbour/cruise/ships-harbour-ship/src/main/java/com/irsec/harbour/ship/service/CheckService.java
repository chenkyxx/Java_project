package com.irsec.harbour.ship.service;

import com.irsec.harbour.ship.controller.CheckController;
import com.irsec.harbour.ship.controller.PassengerController;
import com.irsec.harbour.ship.data.bean.CertificateTypeEnum;
import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.bean.UploadResultBean;
import com.irsec.harbour.ship.data.dto.MapOutputDTO;
import com.irsec.harbour.ship.data.dto.TicketCheckingInputDTO;
import com.irsec.harbour.ship.data.dto.TicketCheckingOutputDTO;
import com.irsec.harbour.ship.data.entity.ShipFlight;
import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import com.irsec.harbour.ship.data.entity.ShipManualCheckin;
import com.irsec.harbour.ship.data.entity.ShipPassenger;
import com.irsec.harbour.ship.data.impl.FlightPlanDaoImpl;
import com.irsec.harbour.ship.data.impl.ShipFlightImpl;
import com.irsec.harbour.ship.data.impl.ShipManualCheckinImpl;
import com.irsec.harbour.ship.data.impl.ShipPassengerImpl;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.GoFastUtils;
import com.irsec.harbour.ship.utils.SecretUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @Auther: Jethro
 * @Date: 2019/9/24 14:34
 * @Description: 验票的服务类
 */
@Service
@Slf4j
public class CheckService {

    @Autowired
    ShipPassengerImpl shipPassengerImpl;

    @Autowired
    ShipFlightImpl shipFlightImpl;
    @Autowired
    ShipManualCheckinImpl shipManualCheckinImpl;

    @Autowired
    FlightPlanDaoImpl flightPlanDaoImpl;


    @Value("${uploadImg.url}")
    private String uploadImgUrl;

    @Autowired
    CheckRecoderPusher checkRecoderPusher;



    public Map<String,Object> check(ShipManualCheckin checkin, TicketCheckingInputDTO data, boolean isTerminal){
        HashMap<String,Object> resultMap = new HashMap<>();
        MapOutputDTO mapOutputDTO = new MapOutputDTO();
        TicketCheckingOutputDTO responseParams = new TicketCheckingOutputDTO();

        try{
        //验票改为验证证件号
        ShipPassenger shipPassenger = shipPassengerImpl.findByPassportId(data.getUserBarcode());

        if (shipPassenger == null) {
            //保存此次验票信息
            log.info("二维码 {} 没有找到对应旅客信息", data.getUserBarcode());
            checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_ERROR_CODE);
            shipManualCheckinImpl.saveCheckin(checkin, null);
            notFindPassenger(isTerminal,resultMap, checkin.getVerifyResult(),0,"未找到该旅客的购票信息",mapOutputDTO);
        } else {

            //获取航班信息
            ShipFlight flightShip = shipFlightImpl.findById(shipPassenger.getFlightId());
            if (flightShip == null) {
                checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_ERROR_CODE);
                notFindPassenger(isTerminal, resultMap, checkin.getVerifyResult(),0, "未找到该旅客的购票信息",mapOutputDTO);
                return resultMap;
            }

            initMap(mapOutputDTO, false);
            //判断是否在验票时间段内
            if(isTerminal && !isInCheckTicketTime(flightShip.getFlightPlanId())){
                writeCheckin(checkin, shipPassenger, flightShip, mapOutputDTO);
                checkin.setVerifyResult(ConstanCollection.CHECK_NOT_IN_CHECK_TIME);
                mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, checkin.getVerifyResult());
                mapOutputDTO.setMsg("当前时间不在验票时间内");
                resultMap.put(ConstanCollection.TERMINAL, mapOutputDTO);
                return resultMap;
            }
            //判断日期是今天
            writeCheckin(checkin, shipPassenger, flightShip, mapOutputDTO);
            if(isTerminal){
                mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, checkin.getVerifyResult());
                resultMap.put(ConstanCollection.TERMINAL, mapOutputDTO);
            }else {
                //获取到指定格式的返回信息
                HashMap<String,Object> result =shipPassengerImpl.getPassengerAndFlightAndLuggages(shipPassenger);
                responseParams.setData(result);
                responseParams.setVerifyResult(checkin.getVerifyResult());
                responseParams.setVerifyTotal(0);
                resultMap.put(ConstanCollection.RESULT, responseParams);
            }
            //先查询当日该证件是否有验证通过的记录，如果有则直接返回-5
            if(shipManualCheckinImpl.findPassportIdIsPassInToday(data.getUserBarcode(),shipPassenger.getFlightId())){
                log.info("证件号：{} ,航班id:{} 旅客当日该航班已经验证通过,不能重复通过",data.getUserBarcode(), shipPassenger.getFlightId());
                checkin.setVerifyResult(ConstanCollection.CHECK_TICKET_HAS_CHECKED);
                shipManualCheckinImpl.saveCheckin(checkin,null);
                //notFindPassenger(isTerminal,resultMap, checkin.getVerifyResult(),0, "该旅客当日已通过");
                if(isTerminal){
                    mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, checkin.getVerifyResult());
                }else {
                    responseParams.setVerifyResult(checkin.getVerifyResult());
                }
                return resultMap;
            }

            //保存到数据库中
            shipManualCheckinImpl.saveCheckin(checkin, shipPassenger);
            checkRecoderPusher.sendToSingleWindow(checkin,false);

            //验票的数量，应该先从redis里面拿
            return resultMap;
        }
       }catch (Exception e){
            log.error("验票过程中出现异常");
       }finally {
           int verifyTotal = shipManualCheckinImpl.getTodayPassNumber(data.getCheckDeviceNo());
           if(isTerminal){
               mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_TOTAL, verifyTotal);
               responseParams = null;
           }else {
               responseParams.setVerifyTotal(verifyTotal);
               mapOutputDTO = null;
           }
       }
       return resultMap;
    }

    private void notFindPassenger(Boolean isTerminal, HashMap<String,Object> resultMap, int verifyStatus ,int verifyTotal, String msg, MapOutputDTO mapOutputDTO) {
        if(isTerminal){
            initMap(mapOutputDTO, false);
            mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, verifyStatus);
            mapOutputDTO.setMsg(msg);
            mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_TOTAL, verifyTotal);
            resultMap.put(ConstanCollection.TERMINAL, mapOutputDTO);
        }else{
            TicketCheckingOutputDTO responseParams = new TicketCheckingOutputDTO();
            responseParams.setVerifyResult(verifyStatus);
            responseParams.setMsg(msg);
            responseParams.setVerifyTotal(verifyTotal);
            resultMap.put(ConstanCollection.RESULT, responseParams);
        }
    }


    public void writeCheckin(ShipManualCheckin checkin, ShipPassenger shipPassenger, ShipFlight flightShip, MapOutputDTO mapOutputDTO) {
        boolean isFill = false;
        //判断日期是今天
        if (DateUtil.dateEqual(flightShip.getSailDate(), new Date())) {
            //验票成功
            checkin.setVerifyResult(ConstanCollection.CHECK_SUCCESS);
            //修改passenger表的字段
            shipPassenger.setIsCheckingTacket(1);
            shipPassenger.setCheckingTime(checkin.getCheckingTime());
            shipPassenger.setCheckDeviceNo(checkin.getCheckDeviceNo());
            isFill = true;
        } else if(flightShip.getSailDate().after(new Date())){
            checkin.setVerifyResult(ConstanCollection.CHECK_FAILED_NOT_TODAY);
            isFill = true;
        }else {
            checkin.setVerifyResult(-1);
        }

        //旅客信息
        checkin.setPassengerId(shipPassenger.getId());
        checkin.setPassengerName(PassengerController.getPassengerName(shipPassenger));
        checkin.setPassportId(shipPassenger.getPassportId());
        checkin.setIdNumber(shipPassenger.getIdNumber());
        checkin.setContact(shipPassenger.getContact());
        checkin.setCountry(shipPassenger.getCountry());
        checkin.setCertificateType(shipPassenger.getCertificateType());
        checkin.setBirthDay(shipPassenger.getBirthDay());
        checkin.setSex(shipPassenger.getSex());
        //航班信息
        checkin.setShipNo(flightShip.getShipNo());
        checkin.setFlightId(flightShip.getId());
        if(isFill && mapOutputDTO != null){
            mapOutputDTO.put(ConstanCollection.FIELD_SHIP_NO, flightShip.getShipNo());
            mapOutputDTO.put(ConstanCollection.FIELD_SHIP_NAME_CH, flightShip.getShipNameCh());
            mapOutputDTO.put(ConstanCollection.FIELD_SAILDATE, DateUtil.dateToStr(flightShip.getSailDate(),"yyyy-MM-dd HH:mm:ss"));
            mapOutputDTO.put(ConstanCollection.FIELD_CERTIFICATE_ID, shipPassenger.getPassportId());
            mapOutputDTO.put(ConstanCollection.FIELD_CERTIFICATE_TYPE, shipPassenger.getCertificateType());
            mapOutputDTO.put(ConstanCollection.FIELD_VALIDITY_DATE, DateUtil.dateToStr(shipPassenger.getPassportValidity(),"yyyy-MM-dd"));
            String name = StringUtils.isEmpty(shipPassenger.getPassengerNameCh())==true?shipPassenger.getPassengerNameEn():shipPassenger.getPassengerNameCh();
            mapOutputDTO.put(ConstanCollection.FIELD_PASSENGER_NAME, name);
        }
        judgeShipInfo(checkin);

    }

    public static void initMap(MapOutputDTO mapOutputDTO , boolean isFace) {
        mapOutputDTO.put(ConstanCollection.FIELD_SAILDATE, null);
        mapOutputDTO.put(ConstanCollection.FIELD_SHIP_NAME_CH, null);
        mapOutputDTO.put(ConstanCollection.FIELD_SHIP_NO, null);
        mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, -1);
        mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_TOTAL, 0);

        if(!isFace){
            //如果不是人脸验证接口调用该方法
            mapOutputDTO.put(ConstanCollection.FIELD_CERTIFICATE_ID, null);
            mapOutputDTO.put(ConstanCollection.FIELD_CERTIFICATE_TYPE, null);
            mapOutputDTO.put(ConstanCollection.FIELD_VALIDITY_DATE, null);
            mapOutputDTO.put(ConstanCollection.FIELD_PASSENGER_NAME, null);
        }
    }

    public void manualCheckIn(ShipManualCheckin checkin, String idCard,int type){
        ShipPassenger shipPassenger = null;
        try{
            if(CertificateTypeEnum.CERTIFICATE_TYPE_ID_CARD.getTypeInt() == type){
                shipPassenger = shipPassengerImpl.findByIdNumber(idCard);
            }else {
                //验票改为验证证件号
                shipPassenger = shipPassengerImpl.findByPassportId(idCard);
            }
            if (shipPassenger != null) {
                //获取航班信息
                ShipFlight flightShip = shipFlightImpl.findById(shipPassenger.getFlightId());
                //旅客信息
                checkin.setPassengerId(shipPassenger.getId());
                checkin.setPassengerName(PassengerController.getPassengerName(shipPassenger));
                checkin.setPassportId(shipPassenger.getPassportId());
                checkin.setIdNumber(shipPassenger.getIdNumber());
                checkin.setContact(shipPassenger.getContact());
                checkin.setCountry(shipPassenger.getCountry());
                checkin.setCertificateType(shipPassenger.getCertificateType());
                checkin.setBirthDay(shipPassenger.getBirthDay());
                checkin.setSex(shipPassenger.getSex());


                //航班信息
                if(flightShip != null){
                    checkin.setShipNo(flightShip.getShipNo());
                    checkin.setFlightId(flightShip.getId());
                }
            }

            judgeShipInfo(checkin);

        }catch (Exception e){
            log.error("人工放行接口出错",e);
        }
    }



    /**
     * @param checkin
     * @param data
     * @param operationType
     */
    public void InitShipManualCheckin(ShipManualCheckin checkin, TicketCheckingInputDTO data, int operationType) {
        checkin.setId(data.getCheckinId());
        checkin.setCheckingTime(data.getCheckingTime());
        checkin.setOperationType(operationType);
        checkin.setUploadTime(new Date());
        checkin.setCheckDeviceNo(data.getCheckDeviceNo());
        checkin.setBarcode(data.getUserBarcode());
        checkin.setManualPass(data.getManualPass());
        checkin.setPassportId(data.getUserBarcode());
        checkin.setVerifyResult(ConstanCollection.CHECK_SUCCESS);
        //判断是否有图片
        if(!StringUtils.isEmpty(data.getLivePhoto())){
            byte[] livePhoto = SecretUtils.base64DecodeByte(data.getLivePhoto());
            if(livePhoto.length>0){
                UploadResultBean uploadBean = GoFastUtils.uploadImgOkHttp(livePhoto,uploadImgUrl);
                if(uploadBean != null){
                    String url = uploadBean.getPath();
                    checkin.setLivePhoto(url);
                }else {
                    checkin.setLivePhoto(null);
                }
            }
        }
    }

    public boolean isInCheckTicketTime(String flightPlanId){
        //如果计划通关时间和计划截关时间都不为空则进行时间判断，否则默认不进行判断自动返回true
        ShipFlightPlan shipFlightPlan = flightPlanDaoImpl.findFlightPlanById(flightPlanId);
        if (shipFlightPlan == null){
            return true;
        }else {
            Date now = new Date();
            if(shipFlightPlan.getPlanPassTime() != null && shipFlightPlan.getPlanCloseTime() != null ){
                if(now.before(shipFlightPlan.getPlanCloseTime()) && now.after(shipFlightPlan.getPlanPassTime())){
                    return true;
                }else{
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 使用上一个航班的航班信息
     */

    public void judgeShipInfo(ShipManualCheckin shipManualCheckin){
        if(StringUtils.isEmpty(shipManualCheckin.getShipNo()) && !StringUtils.isEmpty(CheckController.preShipManualCheckIn.getShipNo())){
            synchronized (CheckController.preShipManualCheckIn){
                shipManualCheckin.setShipNo(CheckController.preShipManualCheckIn.getShipNo());
                shipManualCheckin.setFlightId(CheckController.preShipManualCheckIn.getFlightId());
            }
        }else{
            synchronized (CheckController.preShipManualCheckIn){
                CheckController.preShipManualCheckIn.setShipNo(shipManualCheckin.getShipNo());
                CheckController.preShipManualCheckIn.setFlightId(shipManualCheckin.getFlightId());
            }
        }
    }
}
