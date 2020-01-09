package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.bean.LogConstantEnum;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import com.irsec.harbour.ship.data.entity.ShipLane;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import com.irsec.harbour.ship.data.group.ValidatedGroupLog;
import com.irsec.harbour.ship.data.impl.FlightPlanDaoImpl;
import com.irsec.harbour.ship.data.impl.LaneDaoImpl;
import com.irsec.harbour.ship.data.impl.LogDaoImpl;
import com.irsec.harbour.ship.service.ShipFlightService;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.IpUtil;
import com.irsec.harbour.ship.utils.PageCheckUtil;
import com.irsec.harbour.ship.utils.UUIDTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/20 09:39
 * @Description: 靠离泊计划的相关接口
 */

@RestController
@RequestMapping("/api/v1/flightPlan")
public class FlightPlanController {
    Logger logger = LoggerFactory.getLogger(FlightPlanController.class);

    @Autowired
    FlightPlanDaoImpl flightPlanDao;
    @Autowired
    LaneDaoImpl laneDao;
    @Autowired
    private LogDaoImpl logDao;

    @Autowired
    ShipFlightService shipFlightService;

    /**
     * 靠离泊计划新增接口
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<String> flightAdd(@RequestBody @Validated({ValidatedGroup1.class,ValidatedGroup3.class,LaneDTO.ValidatedGroupFlightPlan.class,ValidatedGroupLog.class}) OneInputWithLogDTO<FlightPlanDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FlightPlanDTO flightPlanDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(flightPlanDTO == null){
                return responseBuilder.BadRequest("数据格式有误");
            }

            ShipFlightPlan shipFlightPlan = new ShipFlightPlan();
            List<ShipLane> shipLanes = new ArrayList<>();

            BeanUtils.copyProperties(flightPlanDTO, shipFlightPlan);
            shipFlightPlan.setId(UUIDTool.newUUID());
            shipFlightPlan.setBerId(flightPlanDTO.getBerthId());
            shipFlightPlan.setShipAgent(flightPlanDTO.getShipAgentType());
            shipFlightPlan.setPlanArriveTime(DateUtil.strToDate(flightPlanDTO.getPlanArriveTime(),"yyyyMMddHHmmss"));
            shipFlightPlan.setPlanDepartTime(DateUtil.strToDate(flightPlanDTO.getPlanDepartTime(),"yyyyMMddHHmmss"));
            shipFlightPlan.setPlanArrivePortTime(DateUtil.strToDate(flightPlanDTO.getPlanArrivePortTime(), "yyyyMMddHHmmss"));
            shipFlightPlan.setPortType(flightPlanDTO.getPortType());
            shipFlightPlan.setCreateUser(params.getOpt().getOptUserName());
            shipFlightPlan.setPlanStatus(ConstanCollection.STATUS_APPLY);

            for(LaneDTO laneDTO : flightPlanDTO.getLane()){
                ShipLane shipLane = new ShipLane();
                shipLane.setId(UUIDTool.newUUID());
                shipLane.setFlightId(shipFlightPlan.getId());
                shipLane.setArriveTime(DateUtil.strToDate(laneDTO.getPlanArriveTime(),"yyyy-MM-dd HH:mm"));
                shipLane.setPlace(laneDTO.getPlace());
                shipLane.setPortType(laneDTO.getPortType());
                shipLane.setOrder(laneDTO.getOrder());
                shipLane.setPlaceCode(laneDTO.getPlaceCode());
                shipLanes.add(shipLane);
            }

            flightPlanDao.save(shipFlightPlan);
            laneDao.save(shipLanes);

            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_ADD_FLIGHT.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_ADD_FLIGHT.getOptdetail()+shipFlightPlan.getId());

        }catch (Exception e){
            return responseBuilder.Error("新增靠离泊计划时，数据处理出错");
        }
        return responseBuilder.OK();
    }


    /**
     * 靠离泊计划修改接口
     * @return
     */
    @PostMapping("/edit")
    public ResponseEntity<String> flightEdit(@RequestBody @Validated({ValidatedGroup2.class,ValidatedGroup3.class,ValidatedGroupLog.class})  OneInputWithLogDTO<FlightPlanDTO> params, HttpServletRequest request, BindingResult result){
        //靠离泊计划修改接口
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FlightPlanDTO flightPlanDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }
        try{
            if(flightPlanDTO == null){
                return responseBuilder.BadRequest("数据格式有误");
            }
            flightPlanDao.update(flightPlanDTO);

            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_EDIT_FLIGHT.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_EDIT_FLIGHT.getOptdetail()+flightPlanDTO.getId());
        }catch (EntityNotFoundException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (EmptyResultDataAccessException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception e){
            logger.error(e.getMessage());
            return responseBuilder.Error("修改靠离泊计划时，数据处理出错");
        }
        return responseBuilder.OK();
    }


    /**
     * 靠离泊计划状态修改接口（申请，删除，取消申请）
     * @return
     */
    @PostMapping("/status")
    public ResponseEntity<String> flightStatus(@RequestBody @Validated({ValidatedGroup2.class,ValidatedGroup3.class,ValidatedGroupLog.class})  OneInputWithLogDTO<FlightPlanDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FlightPlanDTO flightPlanDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(flightPlanDTO == null){
                return responseBuilder.BadRequest("数据格式有误");
            }else if(StringUtils.isEmpty(flightPlanDTO.getId())){
                return responseBuilder.BadRequest("记录id不能为空");
            }else if(StringUtils.isEmpty(flightPlanDTO.getJobStatus())){
                return responseBuilder.BadRequest("状态不能为空");
            }else if(flightPlanDTO.getJobStatus() != 0 && flightPlanDTO.getJobStatus() != 1 && flightPlanDTO.getJobStatus() != 2){
                return responseBuilder.BadRequest("状态值有误");
            }

            if(flightPlanDTO.getJobStatus() == 0){
                //删除该条记录
                flightPlanDao.delete(flightPlanDTO.getId());

                //本次操作的日志
                logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_DELETE_FLIGHT.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_DELETE_FLIGHT.getOptdetail()+flightPlanDTO.getId());
            }else if(flightPlanDTO.getJobStatus() == 1){
                //加入生产作业，把该条记录状态标为待计划
                flightPlanDao.updateStatusWaitToPlan(flightPlanDTO);
                //本次操作的日志
                logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_ADD_JOB.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_ADD_JOB.getOptdetail()+flightPlanDTO.getId());
            }else if(flightPlanDTO.getJobStatus() == 2){
                //取消作业,把该条记录状态标记为已申请
                flightPlanDao.updateStatusApply(flightPlanDTO);
                //本次操作的日志
                logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_CANCEL_JOB.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_CANCEL_JOB.getOptdetail()+flightPlanDTO.getId());
            }

        }catch (EntityNotFoundException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (EmptyResultDataAccessException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception e){
            logger.error(e.getMessage());
            return responseBuilder.Error("修改作业状态时，数据处理出错");
        }
        return responseBuilder.OK();
    }


    /**
     * 生产作业计划接口
     * @return
     */
    @PostMapping("/addJob")
    public ResponseEntity<String> flightAddJob(@RequestBody @Validated({ValidatedGroup2.class,FlightPlanDTO.ValidatedSubmitPlan.class,ValidatedGroupLog.class})  OneInputWithLogDTO<FlightPlanDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FlightPlanDTO flightPlanDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }
        try{
            if(flightPlanDTO == null){
                return responseBuilder.BadRequest("数据格式有误");
            }
            ShipFlightPlan shipFlightPlan = flightPlanDao.updateStatusWaitToFeedback(flightPlanDTO);
            if(shipFlightPlan.getPlanStatus() == ConstanCollection.STATUS_FEEDBACK){
                return responseBuilder.BadRequest("该计划状态为已反馈，不能重复提交计划");
            }

            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_SUBMIT_JOB.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_SUBMIT_JOB.getOptdetail()+flightPlanDTO.getId());
        }catch (EntityNotFoundException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (EmptyResultDataAccessException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception e){
            logger.error(e.getMessage());
            return responseBuilder.Error("加入生产计划时，数据处理出错");
        }
        return responseBuilder.OK();
    }

    /**
     * 完成作业接口( modify 2020.1.2 以前叫反馈，现在叫 完成作业）
     * @return
     */
    @PostMapping("/jobFeedback")
    public ResponseEntity<String> flightJobFeedback(@RequestBody @Validated({ValidatedGroup2.class,FlightPlanDTO.ValidatedFeedBack.class,ValidatedGroupLog.class,ValidatedGroup3.class})  OneInputWithLogDTO<FlightPlanDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FlightPlanDTO flightPlanDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(flightPlanDTO == null){
                return responseBuilder.BadRequest("数据格式有误");
            }
            flightPlanDao.updateStatusFeedback(flightPlanDTO);

            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_FEEDBACK_JOB.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_FEEDBACK_JOB.getOptdetail()+flightPlanDTO.getId());

        }catch (EntityNotFoundException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (EmptyResultDataAccessException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception e){
            logger.error(e.getMessage());
            return responseBuilder.Error("反馈作业时，数据处理出错");
        }
        return responseBuilder.OK();
    }

    /**
     * 生产作业计划驳回接口
     * @return
     */
    @PostMapping("/jobReject")
    public ResponseEntity<String> flightJobReject(@RequestBody @Validated({ValidatedGroup2.class,ValidatedGroupLog.class})  OneInputWithLogDTO<FlightPlanDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FlightPlanDTO flightPlanDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }
        try{
            if(flightPlanDTO == null){
                return responseBuilder.BadRequest("数据格式有误");
            }
            ShipFlightPlan shipFlightPlan = null;
            shipFlightPlan = flightPlanDao.updateStatusReject(flightPlanDTO);
            if(shipFlightPlan == null){
                return responseBuilder.BadRequest("不存在该条记录");
            }
            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_REJECT_JOB.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_REJECT_JOB.getOptdetail()+flightPlanDTO.getId());

        }catch (EntityNotFoundException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (EmptyResultDataAccessException e){
            logger.error(e.getMessage());
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception e){
            logger.error(e.getMessage());
            return responseBuilder.Error("驳回生产计划时，数据处理出错");
        }
        return responseBuilder.OK();
    }

    /**
     * 靠离泊计划查询接口
     * @return
     */
    @PostMapping("/query")
    public ResponseEntity<String> flightQuery(@RequestBody @Validated({ValidatedGroupLog.class})  QueryInputWithLogDTO<FlightPlanConditionDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FlightPlanConditionDTO condition = params.getCondition();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        //检查日志参数
        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }
        //检查分页参数
        String cm = PageCheckUtil.checkPageQueryParams(params);
        if (!StringUtils.isEmpty(cm)) {
            return responseBuilder.BadRequest(cm);
        }

        try{
            Page<FlightPlanQueryDTO> pageResult = flightPlanDao.queryFlightPlanByCondition(condition, params.getPageIndex(),params.getPageSize(), false);
            rowsResponseParams.setRows(pageResult.getContent());
            rowsResponseParams.setTotal((int) pageResult.getTotalElements());
            rowsResponseParams.setSubtotal(pageResult.getContent().size());

            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_FLIGHT_QUERY.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_FLIGHT_QUERY.getOptdetail()+condition.toString());
        }catch (Exception e){
            logger.error(e.getMessage());
            return responseBuilder.Error("查询靠离泊计划时，数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }

    /**
     * 生产作业计划查询接口
     * @return
     */
    @PostMapping("/jobQuery")
    public ResponseEntity<String> flightJobQuery(@RequestBody @Validated({ValidatedGroupLog.class})  QueryInputWithLogDTO<JobConditionDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        JobConditionDTO condition = params.getCondition();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        //检查日志参数
        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }
        if(!StringUtils.isEmpty(condition.getStatus())){
            if(condition.getStatus() != ConstanCollection.STATUS_WAIT_TO_PLAN  && condition.getStatus() != ConstanCollection.STATUS_WAIT_TO_FEEDBACK && condition.getStatus() != ConstanCollection.STATUS_FEEDBACK){
                return responseBuilder.BadRequest("请传入正确的状态值，1-待计划，3-待反馈，4-已反馈");
            }
        }

        //检查分页参数
        String cm = PageCheckUtil.checkPageQueryParams(params);
        if (!StringUtils.isEmpty(cm)) {
            return responseBuilder.BadRequest(cm);
        }

        try{

            Page<ShipFlightPlan> pageResult = flightPlanDao.queryJobByCondition(condition, params.getPageIndex(),params.getPageSize(),false,false);
            List<JobQueryDto> list = shipFlightService.shipFlightPlan2JobQueryDto(pageResult.getContent());

            if(pageResult != null ){
                rowsResponseParams.setRows(list);
                rowsResponseParams.setTotal((int) pageResult.getTotalElements());
                rowsResponseParams.setSubtotal(pageResult.getContent().size());
            }else{
                rowsResponseParams.setRows(null);
                rowsResponseParams.setTotal(0);
                rowsResponseParams.setSubtotal(0);
            }

            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_JOB_QUERY.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_JOB_QUERY.getOptdetail()+condition.toString());

        }catch (Exception e){
            logger.error(e.getMessage());
            return responseBuilder.Error("查询生产作业计划时，数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }

    /**
     * 生产作业计划历史查询接口
     * @return
     */
    @PostMapping("/jobHistory")
    public ResponseEntity<String> flightJobHistory(@RequestBody @Validated({ValidatedGroupLog.class})  QueryInputWithLogDTO<JobConditionDTO> params, BindingResult result, HttpServletRequest request){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        JobConditionDTO condition = params.getCondition();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        //检查日志参数
        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }
        //检查分页参数
        String cm = PageCheckUtil.checkPageQueryParams(params);
        if (!StringUtils.isEmpty(cm)) {
            return responseBuilder.BadRequest(cm);
        }

        try{

            Page<ShipFlightPlan> pageResult = flightPlanDao.queryJobByCondition(condition, params.getPageIndex(),params.getPageSize(),true,false);
            List<JobQueryDto> list = shipFlightService.shipFlightPlan2JobHistoryQueryDto(pageResult.getContent());
            if(pageResult != null ){
                rowsResponseParams.setRows(list);
                rowsResponseParams.setTotal((int) pageResult.getTotalElements());
                rowsResponseParams.setSubtotal(pageResult.getContent().size());
            }else{
                rowsResponseParams.setRows(null);
                rowsResponseParams.setTotal(0);
                rowsResponseParams.setSubtotal(0);
            }

            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_JOB_HISTORY_QUERY.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_JOB_HISTORY_QUERY.getOptdetail()+condition.toString());
        }catch (Exception e){
            logger.error(e.getMessage());
            return responseBuilder.Error("查询生产作业历史时，数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }
}