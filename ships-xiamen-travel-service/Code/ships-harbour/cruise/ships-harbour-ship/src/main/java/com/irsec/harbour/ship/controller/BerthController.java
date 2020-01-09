package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.bean.LogConstantEnum;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.ShipBerth;
import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import com.irsec.harbour.ship.data.group.ValidatedGroupLog;
import com.irsec.harbour.ship.data.impl.BerthDaoImpl;
import com.irsec.harbour.ship.data.impl.FlightPlanDaoImpl;
import com.irsec.harbour.ship.data.impl.LogDaoImpl;
import com.irsec.harbour.ship.utils.IpUtil;
import com.irsec.harbour.ship.utils.PageCheckUtil;
import com.irsec.harbour.ship.utils.UUIDTool;
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
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 09:32
 * @Description: 泊位相关接口
 */
@RestController
@RequestMapping("/api/v1/berth")
public class BerthController {

    @Autowired
    private BerthDaoImpl berthDao;
    @Autowired
    private LogDaoImpl logDao;

    @Autowired
    private FlightPlanDaoImpl flightPlanDao;

    Logger logger = LoggerFactory.getLogger(BerthController.class);
    /**
     * 泊位增加接口
     * @param params
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<String> berthAdd(@RequestBody  @Validated({ValidatedGroup1.class,ValidatedGroup3.class,ValidatedGroupLog.class}) OneInputWithLogDTO<BerthDTO> params, BindingResult result, HttpServletRequest request){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        BerthDTO berthDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(berthDTO == null){
                //数据为空
                return responseBuilder.BadRequest("数据有误，请重试");
            }

            ShipBerth shipBerth = new ShipBerth();
            shipBerth.setId(UUIDTool.newUUID());
            shipBerth.setBerthLength(berthDTO.getBerthLength());
            shipBerth.setBerthName(berthDTO.getBerthName());
            shipBerth.setBerthWeight(berthDTO.getBerthWeight());
            shipBerth.setBerthComment(berthDTO.getComment());
            shipBerth.setCreateUser(params.getOpt().getOptUserName());
            shipBerth = berthDao.save(shipBerth);
            if (shipBerth.getCreateTime() == null ){
                return responseBuilder.Error("添加失败，请重试。");
            }

            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_ADD_BERTH.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_ADD_BERTH.getOptdetail()+ shipBerth.getBerthName());
        }catch (Exception e){
            logger.error("泊位增加数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK();
    }

    /**
     * 泊位修改接口
     * @param params
     * @return
     */
    @PostMapping("/edit")
    public ResponseEntity<String> berthEdit(@RequestBody  @Validated({ValidatedGroup2.class,ValidatedGroup3.class,ValidatedGroupLog.class})  OneInputWithLogDTO<BerthDTO> params, BindingResult result, HttpServletRequest request){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        BerthDTO berthDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(berthDTO == null){
                //数据为空
                return responseBuilder.BadRequest("数据有误，请重试");
            }
            ShipBerth shipBerth = new ShipBerth();
            shipBerth.setId(berthDTO.getId());
            shipBerth.setBerthLength(berthDTO.getBerthLength());
            shipBerth.setBerthName(berthDTO.getBerthName());
            shipBerth.setBerthWeight(berthDTO.getBerthWeight());
            shipBerth.setBerthComment(berthDTO.getComment());
            shipBerth = berthDao.update(shipBerth);
            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_EDIT_BERTH.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_EDIT_BERTH.getOptdetail()+ shipBerth.getBerthName());
        }catch (EntityNotFoundException e){
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (EmptyResultDataAccessException e){
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception e){
            logger.error("泊位修改数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK();
    }

    /**
     * 泊位删除接口
     * @param params
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntity<String> berthDelete(@RequestBody @Validated({ValidatedGroup2.class,ValidatedGroupLog.class}) OneInputWithLogDTO<BerthDTO> params, BindingResult result, HttpServletRequest request){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        BerthDTO berthDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(berthDTO == null){
                //数据为空
                return responseBuilder.BadRequest("数据有误，请重试");
            }else if(StringUtils.isEmpty(berthDTO.getId())){
                //泊位id为空
                return responseBuilder.BadRequest("泊位Id不能为空");
            }
            //先去检查是否有靠离泊计划选择了该泊位
//            if(!flightPlanDao.findAllByBerId(berthDTO.getId())){
//                return responseBuilder.OK("该船舶关联的靠离泊计划中有未反馈的靠离泊计划, 不能删除");
//            }

            ShipBerth shipBerth =  berthDao.delete(berthDTO.getId());
            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_DELETE_BERTH.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_DELETE_BERTH.getOptdetail()+ shipBerth.getBerthName());
        }catch (EmptyResultDataAccessException e){
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception e){
            logger.error("泊位删除数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK();
    }


    /**
     * 泊位查询接口
     * @param params
     * @return
     */
    @PostMapping("/query")
    public ResponseEntity<String> berthquery(@RequestBody @Validated({ValidatedGroupLog.class}) QueryInputWithLogDTO<BerthConditionDTO> params, BindingResult result, HttpServletRequest request){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        BerthConditionDTO condition = params.getCondition();
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
            Page<BerthDTO> pageResult = berthDao.queryByCondition(params.getPageIndex(), params.getPageSize(), condition);
            rowsResponseParams.setRows(pageResult.getContent());
            rowsResponseParams.setTotal((int) pageResult.getTotalElements());
            rowsResponseParams.setSubtotal(pageResult.getContent().size());
            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_QUERY_BERTH.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_QUERY_BERTH.getOptdetail()+condition.toString());

        }catch (Exception e){
            logger.error("泊位查询数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }


    /**
     * 所有泊位查询接口
     * 用于靠离泊计划查询\新增靠离泊计划\生产作业管理\作业历史管理中的泊位选择下拉框数据
     * @param params
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<String> berthList(@RequestBody BaseInputDTO params){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        try{
            List<BerthSearchDTO> list = berthDao.getAll();
            rowsResponseParams.setRows(list);
            rowsResponseParams.setTotal(list.size());
            rowsResponseParams.setSubtotal(list.size());
        }catch (Exception e){
            logger.error("所有泊位查询接口处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }
}
