package com.irsec.harbour.ship.data.impl;

import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.bean.LogConstantEnum;
import com.irsec.harbour.ship.data.dao.LogDao;
import com.irsec.harbour.ship.data.dto.LogConditionDTO;
import com.irsec.harbour.ship.data.dto.LogQueryDTO;
import com.irsec.harbour.ship.data.entity.ShipLog;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.UUIDTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 11:22
 * @Description:
 */
@Service
public class LogDaoImpl {

    @Autowired
    LogDao logDao;

    //@Async
    public void addLog(String userName, String userId, String ip, int optType, String optContent){
        ShipLog shipLog = new ShipLog();
        shipLog.setId(UUIDTool.newUUID());
        shipLog.setDevice(ConstanCollection.DEVICE_WEB);
        shipLog.setOptType(optType);
        shipLog.setOptContent(optContent);
        shipLog.setOptUserId(userId);
        shipLog.setOptUserName(userName);
        shipLog.setIpAddress(ip);
        logDao.save(shipLog);
    }


    public Page<LogQueryDTO> getLog(int pageIndex, int pageSize,final LogConditionDTO conditionDTO,boolean isExport){

        Specification<ShipLog> spec = new Specification<ShipLog>() {
            @Override
            public Predicate toPredicate(Root<ShipLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                //19是检票记录的推送内容
                list.add(cb.notEqual(root.get("optType"),19));
                if(conditionDTO != null){
                    if(!StringUtils.isEmpty(conditionDTO.getIpaddress())){
                        list.add(cb.equal(root.get("ipAddress"),conditionDTO.getIpaddress()));
                    }
                    if(!StringUtils.isEmpty(conditionDTO.getOptUser())){
                        list.add(cb.like(root.get("optUserName"),"%"+conditionDTO.getOptUser()+"%"));
                    }
                    if(!StringUtils.isEmpty(conditionDTO.getOptType())){
                        list.add(cb.equal(root.get("optType"),conditionDTO.getOptType()));
                    }

                    if(!StringUtils.isEmpty(conditionDTO.getStartTime())){
                        list.add(cb.greaterThanOrEqualTo(root.get("createTime"),DateUtil.strToDate(conditionDTO.getStartTime(),"yyyyMMddHHmmss")));
                    }
                    if(!StringUtils.isEmpty(conditionDTO.getEndTime())){
                        list.add(cb.lessThanOrEqualTo(root.get("createTime"),DateUtil.strToDate(conditionDTO.getEndTime(),"yyyyMMddHHmmss")));
                    }
                }
                return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
            }
        };
        if(isExport){
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
            List<ShipLog> result = logDao.findAll(spec,sort);
            List<LogQueryDTO> list = getListLogDTO(result);
            if(list == null ){
                list = new ArrayList<>();
                LogQueryDTO logQueryDTO = new LogQueryDTO();
                list.add(logQueryDTO);
            }
            return new PageImpl<>(list,PageRequest.of(0,list.size()),list.size());
        }else{
            Page<ShipLog> result = null;
            Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime");
            result = logDao.findAll(spec,pageable);
            List<LogQueryDTO> list = getListLogDTO(result.getContent());
            if(list == null){
                list = new ArrayList<>();
            }

            return new PageImpl<>(list,result.getPageable(),result.getTotalElements());
        }
    }

    private List<LogQueryDTO> getListLogDTO(List<ShipLog> page){
        if(page == null || page.size() == 0){
            return null;
        }
        List<LogQueryDTO> list = new ArrayList<>();
        for(ShipLog shipLog : page){
            LogQueryDTO logQueryDTO = new LogQueryDTO();
            logQueryDTO.setCreateTime(DateUtil.dateToStr(shipLog.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            logQueryDTO.setDevice(shipLog.getDevice());
            logQueryDTO.setId(shipLog.getId());
            logQueryDTO.setIpaddress(shipLog.getIpAddress());
            logQueryDTO.setOptDetail(shipLog.getOptContent());
            logQueryDTO.setOptType(LogConstantEnum.getOptContent(shipLog.getOptType()));
            logQueryDTO.setOptUser(shipLog.getOptUserName());
            list.add(logQueryDTO);
        }
        return list;
    }
}
