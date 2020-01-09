package com.irsec.harbour.ship.data.impl;

import com.irsec.harbour.ship.data.dao.ShipDao;
import com.irsec.harbour.ship.data.dto.ShipConditionDTO;
import com.irsec.harbour.ship.data.dto.ShipDTO;
import com.irsec.harbour.ship.data.dto.ShipSearchDTO;
import com.irsec.harbour.ship.data.entity.ShipBoat;
import com.irsec.harbour.ship.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: Jethro
 * @Date: 2019/8/19 16:24
 * @Description:
 */
@Service
public class ShipDaoImpl {
    Logger logger = LoggerFactory.getLogger(ShipDaoImpl.class);
    @Autowired
    private ShipDao shipDao;

    public ShipBoat save(ShipBoat shipBoat){
        return shipDao.save(shipBoat);
    }

    public ShipBoat update(ShipDTO shipDTO){
        ShipBoat shipBoat = null;
        Optional<ShipBoat> shipBoatOptional = shipDao.findById(shipDTO.getId());
        if(!shipBoatOptional.isPresent()){
            throw new EntityNotFoundException();
        }else{
            shipBoat = shipBoatOptional.get();
            BeanUtils.copyProperties(shipDTO, shipBoat,"createUser","createTime");
            shipBoat.setShipType(shipDTO.getShipType());
            shipBoat.setShipComment(shipDTO.getComment());
            shipBoat.setShipNrt(shipDTO.getShipNRT());
            shipBoat.setShipDwt(shipDTO.getShipDWT());
            shipBoat.setShipEnName(shipDTO.getShipNameEn());
            shipBoat.setShipGrt(shipDTO.getShipGRT());
            shipBoat.setShipZhName(shipDTO.getShipNameZh());
            shipDao.save(shipBoat);
            return shipBoat;
        }
    }

    public ShipBoat delete(String shipId){
        ShipBoat tempShipBoat = null;
        tempShipBoat = shipDao.getOne(shipId);
        if(tempShipBoat == null){
            return null;
        }else{
            shipDao.deleteById(shipId);
            return tempShipBoat;
        }
    }
    public List<ShipSearchDTO> getListByName(String shipNameZh){
        return shipDao.getListByNameZh(shipNameZh);
    }
    public Page<ShipDTO> queryByCondition(int page, int pageSize, ShipConditionDTO condition){
        Page<ShipDTO> result = null;
        if(condition != null){
            final String shipCallSign = condition.getShipCallSign();
            final String shipType = condition.getShipType();
            Specification<ShipBoat> spec = new Specification<ShipBoat>() {
                @Override
                public Predicate toPredicate(Root<ShipBoat> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();
                    if(!StringUtils.isEmpty(shipCallSign)){
                        list.add(cb.like(root.get("shipCallSign"),"%"+shipCallSign+"%"));
                    }
                    if(!StringUtils.isEmpty(shipType)){
                        list.add(cb.equal(root.get("shipType"),shipType));
                    }
                    return query.where(list.toArray(new Predicate[list.size()])).getRestriction();
                }
            };
            Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");
            Page<ShipBoat> list = shipDao.findAll(spec,pageable);
            result = getListShipDTO(list);
        }else{
            Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");
            Page<ShipBoat> list = shipDao.findAll(pageable);
            result = getListShipDTO(list);
        }
        return result;
    }
    private Page<ShipDTO> getListShipDTO(Page<ShipBoat> list){
        if(list == null || list.isEmpty()){
            return new PageImpl<>(new ArrayList<>(),list.getPageable(),list.getTotalElements());
        }

        List<ShipDTO> result = new ArrayList<ShipDTO>();
        for(ShipBoat shipBoat : list.getContent()){
            ShipDTO tempShip = new ShipDTO();
            BeanUtils.copyProperties(shipBoat, tempShip);
            tempShip.setShipNameEn(shipBoat.getShipEnName());
            tempShip.setShipNameZh(shipBoat.getShipZhName());
            tempShip.setShipNRT(shipBoat.getShipNrt());
            tempShip.setShipGRT(shipBoat.getShipGrt());
            tempShip.setShipDWT(shipBoat.getShipDwt());
            tempShip.setComment(shipBoat.getShipComment());
            tempShip.setCreateTime(DateUtil.dateToStr(shipBoat.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            result.add(tempShip);
        }
        return new PageImpl<ShipDTO>(result,list.getPageable(),list.getTotalElements());
    }




}
