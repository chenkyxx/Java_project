package com.irsec.harbour.ship.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.dto.BaseInputDTO;
import com.irsec.harbour.ship.data.dto.MapOutputDTO;
import com.irsec.harbour.ship.data.dto.OutputDTOBuilder;
import com.irsec.harbour.ship.data.dto.TicketCheckingConditionDTO;
import com.irsec.harbour.ship.data.entity.ShipManualCheckin;
import com.irsec.harbour.ship.data.impl.ShipManualCheckinImpl;
import com.irsec.harbour.ship.service.CheckRecoderPusher;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.FileUtils;
import com.irsec.harbour.ship.utils.JsonUtil;
import com.irsec.harbour.ship.utils.SecretUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = "api/v1/sys/")
@Slf4j
public class SysController {

    @Autowired
    ShipManualCheckinImpl shipManualCheckinDao;

    @Autowired
    CheckRecoderPusher checkRecoderPusher;
    @Value("${singleWindow.checkRecordUrl}")
    private String checkRecordUrl;


    @PostMapping(value = "time")
    public ResponseEntity getTime(@RequestBody BaseInputDTO params) {

        MapOutputDTO sysResponseParams = new MapOutputDTO();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sysResponseParams.put("time", simpleDateFormat.format(new Date()));

        OutputDTOBuilder responseDtoBuilder = new OutputDTOBuilder(params.getReqId());
        return responseDtoBuilder.OK(sysResponseParams);
    }

    @PostMapping(value = "resend")
    public ResponseEntity resend(@RequestBody Map<String,String> params) throws InterruptedException {
        String start = params.get("start");
        String end = params.get("end");

        Date startDate = DateUtil.strToDate(start,"yyyy-MM-dd HH:mm:ss");
        Date endDate = DateUtil.strToDate(end, "yyyy-MM-dd HH:mm:ss");
        if(startDate != null && endDate != null){
            List<ShipManualCheckin> manualCheckInRecordByCondition = shipManualCheckinDao.getAllResend(startDate,endDate);
            if(!CollectionUtils.isEmpty(manualCheckInRecordByCondition)){
                for(ShipManualCheckin shipManualCheckin : manualCheckInRecordByCondition){
                    if(shipManualCheckin.getIsCancel() == 0){
                        checkRecoderPusher.sendToSingleWindow(shipManualCheckin,false);
                    }else{
                        checkRecoderPusher.sendToSingleWindow(shipManualCheckin,true);
                    }

                    Thread.sleep(500);
                }
            }
        }

        OutputDTOBuilder responseDtoBuilder = new OutputDTOBuilder("success");
        return responseDtoBuilder.OK();
    }

    @PostMapping(value = "resend2")
    public ResponseEntity resend2(){

        String path = "./msg";
        Map<String , String> map = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<Map<String,Object>> paramsList = new ArrayList<>();
        log.info("start read data");
        //1.先把所有数据读取出来
        //2.在根据idNumber或者passportId生成ticketId
        //3.组装数据
        try {
            List<File> list = FileUtils.loopFile(path);
            if(list != null && !list.isEmpty()){
                for(File file : list){
                    if(file.isFile()){

                        try {
                            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
                            BufferedReader bf = new BufferedReader(inputReader);
                            // 按行读取字符串
                            String str;
                            while ((str = bf.readLine()) != null) {
                                arrayList.add(str);
                            }
                            bf.close();
                            inputReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        }catch (Exception e){
            log.error("读取数据出错",e);
            OutputDTOBuilder responseDtoBuilder = new OutputDTOBuilder("failed");
            return responseDtoBuilder.OK();
        }finally {
            log.info("end read data");
        }

        //组装数据
        log.info("start load data");
        try {
            for(String msgInfo : arrayList){
                if(!StringUtils.isEmpty(msgInfo)){
                    Map<String,Object> params = JsonUtil.JsonToBean(msgInfo,Map.class);
                    if(params != null){
                        paramsList.add(params);

                    }

                }

            }
        }catch (Exception e){
            log.error("读取数据出错",e);
            OutputDTOBuilder responseDtoBuilder = new OutputDTOBuilder("failed");
            return responseDtoBuilder.OK();
        }

        log.info("end load data");
        log.info("begin request send data");
        int index = 0;
        try {
            for (Map<String, Object> paramsMap : paramsList) {
                try {
                    HttpResponse response = HttpRequest.post(checkRecordUrl).form(paramsMap).timeout(10000).execute();
                    Thread.sleep(200);
                    try
                    {Map<String, Object> result = JsonUtil.JsonToBean(response.body(), Map.class);
                    log.info("http response : {}", response.body());
                    if (result.getOrDefault("success", null) != null) {
                        if (!(boolean) result.get("success")) {
                            log.error("send failed, reson is {}", response.body());
                        }else{
                            index++;
                        }
                        log.trace("success {}",index);
                    }}
                    catch (Exception e){
                        log.error("解析返回的数据出现错误, 返回的数据为",response.body()==null?"null":response.body());
                    }
                }
                catch (Exception e){
                    log.error("发送过程中出现问题",e);
                    Thread.sleep(2000);
                    continue;

                }


            }
        }catch (Exception e){
            log.error("error",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        log.info("end send data");

        OutputDTOBuilder responseDtoBuilder = new OutputDTOBuilder("success");
        return responseDtoBuilder.OK();
    }
}
