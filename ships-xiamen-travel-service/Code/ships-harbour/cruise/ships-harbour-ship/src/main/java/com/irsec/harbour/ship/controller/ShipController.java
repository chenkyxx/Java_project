package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.bean.LogConstantEnum;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.ShipBoat;
import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup2;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import com.irsec.harbour.ship.data.group.ValidatedGroupLog;
import com.irsec.harbour.ship.data.impl.FlightPlanDaoImpl;
import com.irsec.harbour.ship.data.impl.LogDaoImpl;
import com.irsec.harbour.ship.data.impl.ShipDaoImpl;
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
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 16:20
 * @Description: 船舶相关接口
 */
@RestController
@RequestMapping("/api/v1/ship")
public class ShipController {
    Logger logger = LoggerFactory.getLogger(ShipController.class);
    @Autowired
    private ShipDaoImpl shipDao;
    @Autowired
    private LogDaoImpl logDao;
    @Autowired
    private FlightPlanDaoImpl flightPlanDao;


    /**
     * 船舶增加接口
     * @param params
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<String> shipAdd(@RequestBody @Validated({ValidatedGroup1.class,ValidatedGroup3.class,ValidatedGroupLog.class}) OneInputWithLogDTO<ShipDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        ShipDTO shipDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(shipDTO == null){
                return responseBuilder.BadRequest("数据格式有误");
            }
            ShipBoat shipBoat = new ShipBoat();
            BeanUtils.copyProperties(shipDTO, shipBoat);
            shipBoat.setId(UUIDTool.newUUID());
            shipBoat.setShipType(shipDTO.getShipType());
            shipBoat.setCreateUser(params.getOpt().getOptUserName());
            shipBoat.setShipComment(shipDTO.getComment());
            shipBoat.setShipNrt(shipDTO.getShipNRT());
            shipBoat.setShipDwt(shipDTO.getShipDWT());
            shipBoat.setShipEnName(shipDTO.getShipNameEn());
            shipBoat.setShipGrt(shipDTO.getShipGRT());
            shipBoat.setShipZhName(shipDTO.getShipNameZh());


            shipBoat = shipDao.save(shipBoat);
            if (shipBoat.getCreateTime() == null ){
                return responseBuilder.Error("添加失败，请重试。");
            }
            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_ADD_SHIP.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_ADD_SHIP.getOptdetail()+ shipBoat.getShipZhName());
        }catch (Exception e){
            logger.error("船舶新增时，数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK();
    }


    /**
     * 船舶修改接口
     * @param params
     * @return
     */
    @PostMapping("/edit")
    public ResponseEntity<String> shipEdit(@RequestBody @Validated({ValidatedGroup2.class,ValidatedGroup3.class,ValidatedGroupLog.class}) OneInputWithLogDTO<ShipDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        ShipDTO shipDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(shipDTO == null){
                return responseBuilder.BadRequest("数据格式有误");
            }else if(StringUtils.isEmpty(shipDTO.getId())){
                return responseBuilder.BadRequest("船舶ID不能为空");
            }
            ShipBoat shipBoat = shipDao.update(shipDTO);

            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_EDIT_SHIP.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_EDIT_SHIP.getOptdetail()+ shipBoat.getShipZhName());
        }catch (EntityNotFoundException e){
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (EmptyResultDataAccessException e){
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception e){
            logger.error("船舶修改时，数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK();
    }

    /**
     * 船舶删除接口
     * @param params
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntity<String> shipDelete(@RequestBody @Validated({ValidatedGroup2.class,ValidatedGroupLog.class}) OneInputWithLogDTO<ShipDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        ShipDTO shipDTO = params.getData();

        if(result.hasErrors()){
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try{
            if(shipDTO == null){
                return responseBuilder.BadRequest("数据格式有误");
            }else if(StringUtils.isEmpty(shipDTO.getId())){
                return responseBuilder.BadRequest("船舶ID不能为空");
            }
            //先去检查是否有靠离泊计划选择了该船舶
//            if(!flightPlanDao.findAllByShipId(shipDTO.getId())){
//                return responseBuilder.OK("该船舶关联的靠离泊计划中有未反馈的靠离泊计划, 不能删除");
//            }
            ShipBoat shipBoat = shipDao.delete(shipDTO.getId());
            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_DELETE_SHIP.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_DELETE_SHIP.getOptdetail()+ shipBoat.getShipZhName());
        }catch (EntityNotFoundException e){
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (EmptyResultDataAccessException e){
            return responseBuilder.BadRequest("不存在该条记录");
        }catch (Exception e){
            logger.error("船舶删除时，数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK();
    }

    /**
     * 船舶查询接口
     * @param params
     * @return
     */
    @PostMapping("/query")
    public ResponseEntity<String> berthquery(@RequestBody @Validated({ValidatedGroupLog.class}) QueryInputWithLogDTO<ShipConditionDTO> params, HttpServletRequest request, BindingResult result){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        ShipConditionDTO condition = params.getCondition();
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
            Page<ShipDTO> pageResult = shipDao.queryByCondition(params.getPageIndex(), params.getPageSize(), condition);
            rowsResponseParams.setRows(pageResult.getContent());
            rowsResponseParams.setTotal((int) pageResult.getTotalElements());
            rowsResponseParams.setSubtotal(pageResult.getContent().size());
            //本次操作的日志
            logDao.addLog(params.getOpt().getOptUserName(), params.getOpt().getOptUserId(), IpUtil.getIpAddr(request), LogConstantEnum.LOG_OPT_QUERY_SHIP.getOptType(), params.getOpt().getOptUserName()+LogConstantEnum.LOG_OPT_QUERY_SHIP.getOptdetail()+condition.toString());

        }catch (Exception e){
            logger.error("船舶查询时，数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }


    /**
     * 船舶模糊查询接口
     * @param params
     * @return
     */
    @PostMapping("/search")
    public ResponseEntity<String> berthSearch(@RequestBody OneInputDTO<ShipDTO> params){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        ShipDTO shipDTO = params.getData();
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        try{
            List<ShipSearchDTO> list = shipDao.getListByName(shipDTO.getShipNameZh());
            rowsResponseParams.setRows(list);
            rowsResponseParams.setSubtotal(list.size());
            rowsResponseParams.setTotal(list.size());

        }catch (Exception e){
            logger.error("船舶模糊查询，数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }
}
