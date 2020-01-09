package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.bean.FlightPlanStatusEnum;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import com.irsec.harbour.ship.data.impl.FlightPlanDaoImpl;
import com.irsec.harbour.ship.data.impl.LogDaoImpl;
import com.irsec.harbour.ship.data.impl.ShipManualCheckinImpl;
import com.irsec.harbour.ship.service.ShipFlightService;
import com.irsec.harbour.ship.utils.BeanExtUtil;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.PageCheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Jethro
 * @Date: 2019/8/26 10:44
 * @Description: 靠离泊计划，生产作业，生产作业历史的导出接口
 */

@Controller
@RequestMapping("/api/v1/export")
public class ExportController {
    Logger logger = LoggerFactory.getLogger(ExportController.class);


    @Autowired
    FlightPlanDaoImpl flightPlanDao;

    @Autowired
    LogDaoImpl logDao;

    @Autowired
    ShipFlightService shipFlightService;

    @Autowired
    ShipManualCheckinImpl shipManualCheckinImpl;
    /**
     * 靠离泊计划导出
     * @param params
     * @param request
     * @param result
     * @param response
     */
    @RequestMapping("/flightPlan")
    public ResponseEntity<String > exportFlightPlan(@RequestBody QueryInputWithLogDTO<FlightPlanConditionDTO> params, HttpServletRequest request, BindingResult result, HttpServletResponse response){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FlightPlanConditionDTO condition = params.getCondition();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();

        try{
            Page<FlightPlanQueryDTO> list = flightPlanDao.queryFlightPlanByCondition(condition,0,0,true);
            if(list != null ){
                rowsResponseParams.setRows(list.getContent());
                rowsResponseParams.setTotal((int) list.getTotalElements());
                rowsResponseParams.setSubtotal(list.getContent().size());
            }else{
                rowsResponseParams.setRows(null);
                rowsResponseParams.setTotal(0);
                rowsResponseParams.setSubtotal(0);
            }

            logger.info("靠离泊计划导出成功");
            return responseBuilder.OK(rowsResponseParams);
        }catch(Exception e){
            logger.error("导出靠离泊计划时，数据处理出错", e);
            return responseBuilder.Error("导出靠离泊计划时，数据处理出错");
        }
    }

    private List<FlightPlanExportDTO> getListFlightPlanExportDTO(List<FlightPlanQueryDTO> list){
        List<FlightPlanExportDTO> result = new ArrayList<>();

        for(FlightPlanQueryDTO flightPlanQueryDTO : list){
            FlightPlanExportDTO flightPlanExportDTO = new FlightPlanExportDTO();
            BeanUtils.copyProperties(flightPlanQueryDTO,flightPlanExportDTO);
            flightPlanExportDTO.setIsLead(flightPlanQueryDTO.getIsLead() == 0?"是":"否");
            flightPlanExportDTO.setStatus(FlightPlanStatusEnum.getStatusZh(flightPlanQueryDTO.getStatus()));
            if (flightPlanQueryDTO.getStatus() == ConstanCollection.STATUS_WAIT_TO_PLAN) {
                flightPlanExportDTO.setStatus("待作业");
            }

            result.add(flightPlanExportDTO);
        }

        return result;
    }



    /**
     * 生产作业计划导出接口
     * @return
     */
    @PostMapping("/job")
    public ResponseEntity<String > exportJob(@RequestBody QueryInputWithLogDTO<JobConditionDTO> params, HttpServletRequest request, BindingResult result, HttpServletResponse response) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        JobConditionDTO condition = params.getCondition();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        try {
            Page<ShipFlightPlan> pageResult = flightPlanDao.queryJobByCondition(condition, params.getPageIndex(), params.getPageSize(), false, true);
            List<JobQueryDto> list = shipFlightService.shipFlightPlan2JobQueryDto(pageResult.getContent());

            if(pageResult != null ){
                rowsResponseParams.setRows(list);
                rowsResponseParams.setTotal(list.size());
                rowsResponseParams.setSubtotal(list.size());
            }else{
                rowsResponseParams.setRows(null);
                rowsResponseParams.setTotal(0);
                rowsResponseParams.setSubtotal(0);
            }
            logger.info("生产作业计划导出成功");
            return responseBuilder.OK(rowsResponseParams);
        } catch (Exception e) {
            logger.error("导出生产作业计划时，数据处理出错",e);
            return responseBuilder.Error("导出生产作业计划时，数据处理出错");
        }

    }

    /**
     * 生产作业计划历史导出接口
     * @return
     */
    @PostMapping("/jobHistory")
    public ResponseEntity<String > exportJobHistroy(@RequestBody QueryInputWithLogDTO<JobConditionDTO> params, HttpServletRequest request, BindingResult result, HttpServletResponse response){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        JobConditionDTO condition = params.getCondition();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        try{
            Page<ShipFlightPlan> pageResult = flightPlanDao.queryJobByCondition(condition, params.getPageIndex(),params.getPageSize(),true,true);
            List<JobQueryDto> list = shipFlightService.shipFlightPlan2JobHistoryQueryDto(pageResult.getContent());
            if(pageResult != null ){
                rowsResponseParams.setRows(list);
                rowsResponseParams.setTotal(list.size());
                rowsResponseParams.setSubtotal(list.size());
            }else{
                rowsResponseParams.setRows(null);
                rowsResponseParams.setTotal(0);
                rowsResponseParams.setSubtotal(0);
            }
            logger.info("生产作业计划历史导出成功");
            return responseBuilder.OK(rowsResponseParams);
        }catch (Exception e){
            logger.error("导出生产作业计划历史时，数据处理出错",e);
            return responseBuilder.Error("导出生产作业计划历史时，数据处理出错");
        }
    }


    /**
     *
     * @param time 在泊时间 ，单位秒
     * @return xxH XXMin
     */
    public String calcParkingHour(long time){
        StringBuffer sb = new StringBuffer();
        if(time < 3600){
            //表示靠泊时间在1小时内
            //则只计算分秒
            int min = (int)time/60;
            int sec = (int)time%60;
            sb.append(min+"分钟"+sec+"秒");
        }else{
            //表示靠泊时间超过了1小时
            int hour = (int)time/3600;
            int leftSec = (int)time%3600;
            sb.append(hour+"小时");
            if(leftSec<60){
                //表示小于了1分钟
                sb.append(leftSec+"秒");
            }else{
                int min  = leftSec/60;
                int sec = leftSec%60;
                sb.append(min+"分钟"+sec+"秒");
            }
        }
        return sb.toString();
    }


    @PostMapping("/log")
    public ResponseEntity<String > exportLog(@RequestBody QueryInputWithLogDTO<LogConditionDTO> params, HttpServletResponse response){
        //日志查询接口
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        try{
            Page<LogQueryDTO> pageResult = logDao.getLog(0,0,params.getCondition(),true);
            if(pageResult != null ){
                rowsResponseParams.setRows(pageResult.getContent());
                rowsResponseParams.setTotal((int) pageResult.getTotalElements());
                rowsResponseParams.setSubtotal(pageResult.getContent().size());
            }else{
                rowsResponseParams.setRows(null);
                rowsResponseParams.setTotal(0);
                rowsResponseParams.setSubtotal(0);
            }
            logger.info("日志导出成功");
            return responseBuilder.OK(rowsResponseParams);
        }catch (Exception e){
            logger.error("日志导出数据处理出错",e);
            return responseBuilder.Error("日志导出数据处理出错");
        }
    }

    /**
     * 导出验票记录
     */
    @RequestMapping(value = "checkin", method = {RequestMethod.POST, RequestMethod.GET})
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
            Page<Map<String,Object>>  reultMapList = shipManualCheckinImpl.getManualCheckInRecordByCondition(true,condition, queryRequestParams.getPageIndex(), queryRequestParams.getPageSize());
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
}
