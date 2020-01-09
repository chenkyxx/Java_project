package com.irsec.harbour.ship.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.bean.LogConstantEnum;
import com.irsec.harbour.ship.data.entity.ShipFlight;
import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import com.irsec.harbour.ship.data.entity.ShipManualCheckin;
import com.irsec.harbour.ship.data.impl.FlightPlanDaoImpl;
import com.irsec.harbour.ship.data.impl.LogDaoImpl;
import com.irsec.harbour.ship.data.impl.ShipFlightImpl;
import com.irsec.harbour.ship.data.impl.ShipManualCheckinImpl;
import com.irsec.harbour.ship.utils.FileUtils;
import com.irsec.harbour.ship.utils.JsonUtil;
import com.irsec.harbour.ship.utils.SecretUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/***
 * 负责验票记录的推送，
 * 发送失败时自动保存本地
 * 定时任务自动读取本地文件夹，进行重发，重发成功后删除文件
 */
@Component
public class CheckRecoderPusher{

    Logger log = LoggerFactory.getLogger(CheckRecoderPusher.class);
    @Autowired
    ShipFlightImpl shipFlightImpl;
    @Autowired
    ShipManualCheckinImpl shipManualCheckinImpl;
    @Autowired
    FlightPlanDaoImpl flightPlanDaoImpl;
    @Autowired
    LogDaoImpl logDao;


    @Value("${singleWindow.checkRecordUrl}")
    private String checkRecordUrl;
    private String pushFailedSavePath = "./checkMsg";

    @Async("taskExecutor")
    public void sendToSingleWindow(ShipManualCheckin  shipManualCheckin,boolean isCancel){
        try{
            Map<String, Object> params = shipManualCheckinImpl.getOneTicketRecord(shipManualCheckin);

            if(params != null){
                String flightId = (String) params.getOrDefault(ConstanCollection.FIELD_FLIGHT_ID, "");
                if(StringUtils.isEmpty(flightId)){
                    params.put(ConstanCollection.FIELD_SHIP_AGENT, "");
                }else {
                    ShipFlight shipFlight = shipFlightImpl.findById(flightId);
                    ShipFlightPlan shipFlightPlan = flightPlanDaoImpl.findFlightPlanById(shipFlight.getFlightPlanId());
                    if(shipFlightPlan != null){
                        params.put(ConstanCollection.FIELD_SHIP_AGENT, shipFlightPlan.getShipAgent());
                    }else {
                        params.put(ConstanCollection.FIELD_SHIP_AGENT, "");
                    }
                }

                params.put(ConstanCollection.FIELD_SHIP_LANE_LIST,null);

                if(isCancel){
                    params.put(ConstanCollection.FIELD_GUEST_STATE,0);
                }else{
                    params.put(ConstanCollection.FIELD_GUEST_STATE,1);
                }

                params.put(ConstanCollection.FIELD_TICKET_ID,shipManualCheckin.getTicketId());

                //船票打印状态，放行的就是未打印
                if(shipManualCheckin.getManualPass() == 1 && params.getOrDefault(ConstanCollection.FIELD_TICKET_STATE,null) == null){
                    params.put(ConstanCollection.FIELD_TICKET_STATE, 0);
                }else if(params.getOrDefault(ConstanCollection.FIELD_TICKET_STATE,null) == null){
                    //如果不满足上述情况，且此时船票打印状态仍然没有值。则默认赋值为已打印
                    params.put(ConstanCollection.FIELD_TICKET_STATE, 1);
                }




                String msgInfo = JsonUtil.BeanToJson(params);
                logDao.addLog("","","",LogConstantEnum.LOG_OPT_PUSH_CHECKIN.getOptType(), msgInfo);
                log.info("message content : {}", msgInfo);
                try {
                    HttpResponse response = HttpRequest.post(checkRecordUrl).form(params).timeout(3000).execute();
                    Map<String,Object> result = JsonUtil.JsonToBean(response.body(),Map.class);
                    if(result.getOrDefault("success",null) != null){
                        if(!(boolean)result.get("success")){
                            log.error("send {} failed, reson is {}", shipManualCheckin.getPassportId(),response.body());
                            FileUtils.writeTXT(pushFailedSavePath,SecretUtils.uuid32(),msgInfo);
                        }
                    }
                    log.info("push check id: {} success ，the response：{}", shipManualCheckin.getId(),response.body());
                }catch (Exception e){
                    log.error("push check  id : {} occur error",shipManualCheckin.getId());
                    log.error("occur error in push checkInfo", e);
                    FileUtils.writeTXT(pushFailedSavePath,SecretUtils.uuid32(),msgInfo);
                }
            }else{
                log.info("id : {} not find ",shipManualCheckin.getId());
            }
        }catch (Exception e){
            log.error("push check  id : {} occur error",shipManualCheckin.getId());
            log.error("occur error in push checkInfo", e);
        }
    }

    @Scheduled(fixedDelay= 60*1000)
    public void checkFailedMsg(){
        log.info("start sending  the push failed msg");
        try{
            List<File > list = FileUtils.loopFile(pushFailedSavePath);
            if(list != null && !list.isEmpty()){
                log.info("find files,the number is {}", list.size());
                for(File file : list){
                    if(file.isFile()){
                        String msgInfo = FileUtils.readFileContent(file);
                        if(!StringUtils.isEmpty(msgInfo)){
                            Map<String,Object> params = JsonUtil.JsonToBean(msgInfo,Map.class);
                            try{
                                HttpResponse response = HttpRequest.post(checkRecordUrl).form(params).timeout(3000).execute();
                                Map<String,Object> result = JsonUtil.JsonToBean(response.body(),Map.class);
                                if(result.getOrDefault("success",null) != null){
                                    if((boolean)result.get("success")){
                                        file.delete();
                                    }
                                }
                            }catch (Exception e){
                                log.error("msg push failed again",e);
                            }
                        }
                    }

                }
            }
        }catch (Exception e){
            log.error("msg push failed again",e);
        }
    }
}
