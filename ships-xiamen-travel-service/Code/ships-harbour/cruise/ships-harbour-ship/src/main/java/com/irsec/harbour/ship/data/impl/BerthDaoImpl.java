package com.irsec.harbour.ship.data.impl;

import com.irsec.harbour.ship.data.dao.BerthDao;
import com.irsec.harbour.ship.data.dao.FlightPlanDao;
import com.irsec.harbour.ship.data.dto.BerthConditionDTO;
import com.irsec.harbour.ship.data.dto.BerthDTO;
import com.irsec.harbour.ship.data.dto.BerthSearchDTO;
import com.irsec.harbour.ship.data.entity.ShipBerth;
import com.irsec.harbour.ship.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 14:49
 * @Description: 泊位的数据库操作实现层
 */
@Service
public class BerthDaoImpl {


    @Autowired
    BerthDao berthDao;

    @Autowired
    FlightPlanDao flightPlanDao;

    public ShipBerth save(ShipBerth shipBerth){
        return berthDao.save(shipBerth);
    }

    public ShipBerth update(ShipBerth shipBerth) {
        ShipBerth berth = null;
        Optional<ShipBerth> shipBerthOptional = berthDao.findById(shipBerth.getId());
        if(!shipBerthOptional.isPresent()){
            throw new EntityNotFoundException();
        }else{
            berth = shipBerthOptional.get();
            BeanUtils.copyProperties(shipBerth,berth,"createUser","createTime");
            berthDao.save(berth);
            return berth;
        }
    }

    public ShipBerth delete(String berthId){
        ShipBerth tempShipBerth = berthDao.getOne(berthId);
        if(tempShipBerth != null){
            berthDao.deleteById(berthId);
            return tempShipBerth;
        }else{
            return null;
        }
    }

    public List<BerthSearchDTO> getAll(){
        List<BerthSearchDTO> result = new ArrayList<>();
        List<ShipBerth> list = berthDao.getAll();
        List<String> berthIds = new ArrayList<>();
        for(ShipBerth shipBerth : list){
            berthIds.add(shipBerth.getId());
        }
        List<String> hasBeenUsedBerIds = flightPlanDao.getBerIdByBerthId(berthIds);
        for(ShipBerth shipBerth : list){
            BerthSearchDTO berthSearchDTO = new BerthSearchDTO();
            berthSearchDTO.setId(shipBerth.getId());
            berthSearchDTO.setBerthName(shipBerth.getBerthName());
            berthSearchDTO.setStatus(0);

            if(hasBeenUsedBerIds.contains(shipBerth.getId())){
                berthSearchDTO.setStatus(1);
            }
            result.add(berthSearchDTO);
        }

        return result;
    }

    public Page<BerthDTO> queryByCondition(int page, int pageSize, BerthConditionDTO condition){
        //Page<BerthDTO> result = new PageImpl<>()
        Page<BerthDTO> result = null;

        if(condition != null){
            final String berthName = condition.getBerthName();
            final String berthWeight = condition.getBerthWeight();
            Specification<ShipBerth> spec = new Specification<ShipBerth>() {
                @Override
                public Predicate toPredicate(Root<ShipBerth> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();
                    if(!StringUtils.isEmpty(berthName)){
                        list.add(cb.like(root.get("berthName"),"%"+berthName+"%"));
                    }
                    if(!StringUtils.isEmpty(berthWeight)){
                        list.add(cb.equal(root.get("berthWeight"),berthWeight));
                    }
                    return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
                }
            };
            Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");
            Page<ShipBerth> list = berthDao.findAll(spec,pageable);
            result = getListBerthDTO(list);
        }else{
            Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");
            Page<ShipBerth> list = berthDao.findAll(pageable);
            result = getListBerthDTO(list);
        }

       return result;
    }


    private Page<BerthDTO> getListBerthDTO(Page<ShipBerth> list){
        if(list == null || list.isEmpty()){
            return new PageImpl<>(new ArrayList<>(),list.getPageable(),list.getTotalElements());
        }
        List<BerthDTO> result = new ArrayList<BerthDTO>();
        for(ShipBerth shipBerth : list.getContent()){
            BerthDTO berthDTO = new BerthDTO();
            BeanUtils.copyProperties(shipBerth, berthDTO);
            berthDTO.setComment(shipBerth.getBerthComment());
            berthDTO.setCreateTime(DateUtil.dateToStr(shipBerth.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            result.add(berthDTO);
        }
        return new PageImpl<BerthDTO>(result,list.getPageable(),list.getTotalElements());
    }
}
