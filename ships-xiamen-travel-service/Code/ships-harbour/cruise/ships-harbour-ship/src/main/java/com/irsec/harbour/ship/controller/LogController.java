package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.bean.LogConstantEnum;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.impl.LogDaoImpl;
import com.irsec.harbour.ship.utils.PageCheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/23 09:53
 * @Description:  日志的相关接口
 */
@RestController
@RequestMapping("/api/v1/log")
public class LogController {
    Logger logger = LoggerFactory.getLogger(LogController.class);
    @Autowired
    LogDaoImpl logDao;

    @PostMapping("/list")
    public ResponseEntity<String > logList(@RequestBody QueryInputWithLogDTO<LogConditionDTO> params){
        //日志查询接口
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        //检查分页参数
        String cm = PageCheckUtil.checkPageQueryParams(params);
        if (!StringUtils.isEmpty(cm)) {
            return responseBuilder.BadRequest(cm);
        }
        try{
            Page<LogQueryDTO> pageResult = logDao.getLog(params.getPageIndex(),params.getPageSize(),params.getCondition(),false);
            if(pageResult != null ){
                rowsResponseParams.setRows(pageResult.getContent());
                rowsResponseParams.setTotal((int) pageResult.getTotalElements());
                rowsResponseParams.setSubtotal(pageResult.getContent().size());
            }else{
                rowsResponseParams.setRows(null);
                rowsResponseParams.setTotal(0);
                rowsResponseParams.setSubtotal(0);
            }
        }catch (Exception e){
            logger.error("日志查询数据处理出错",e);
            return responseBuilder.Error("数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }

    @PostMapping("/optType/list")
    public ResponseEntity<String > optTypeList(@RequestBody BaseInputDTO params){
        //操作类型返回接口
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();
        try{
            List<HashMap<String, Object>> list = LogConstantEnum.getList();
            rowsResponseParams.setRows(list);
            rowsResponseParams.setTotal(list.size());
            rowsResponseParams.setSubtotal(list.size());
        }catch (Exception e){
            logger.error("日志操作类型查询，数据处理出错",e);
            return responseBuilder.Error("日志操作类型查询，数据处理出错");
        }
        return responseBuilder.OK(rowsResponseParams);
    }

}
