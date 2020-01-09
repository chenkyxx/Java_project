package com.irsec.harbour.ship.service;

import com.irsec.harbour.ship.data.dto.JobQueryDto;
import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import com.irsec.harbour.ship.utils.BeanExtUtil;
import com.irsec.harbour.ship.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/10/28 17:47
 * @Description:
 */
@Service
public class ShipFlightService {


    public List<JobQueryDto> shipFlightPlan2JobQueryDto(List<ShipFlightPlan> pageResult) throws Exception{
        List<JobQueryDto> list = new ArrayList<>();
        for(ShipFlightPlan shipFlightPlan : pageResult){

            JobQueryDto jobQueryDto = new JobQueryDto();
            jobQueryDto.setLanes(shipFlightPlan.getShipLaneList());
            String[] shipFlightPlanNullProperty = BeanExtUtil.getNullProperty(shipFlightPlan);

            if(shipFlightPlan.getShipBoat() != null){
                String[] boatNullProperty = BeanExtUtil.getNullProperty(shipFlightPlan.getShipBoat());
                BeanUtils.copyProperties(shipFlightPlan.getShipBoat(),jobQueryDto, boatNullProperty);
                jobQueryDto.setShipNameZh(shipFlightPlan.getShipBoat().getShipZhName());
            }
            if(shipFlightPlan.getShipBerth() != null){
                String[] berthNullProperty = BeanExtUtil.getNullProperty(shipFlightPlan.getShipBerth());
                BeanUtils.copyProperties(shipFlightPlan.getShipBerth(),jobQueryDto, berthNullProperty);
            }

            BeanUtils.copyProperties(shipFlightPlan,jobQueryDto, shipFlightPlanNullProperty);
            jobQueryDto.setShipAgent(shipFlightPlan.getShipAgent());
            jobQueryDto.setPortType(shipFlightPlan.getPortType());

            String planArriveTime = DateUtil.dateToStr(shipFlightPlan.getPlanArriveTime(),"yyyy-MM-dd HH:mm:ss");
            String planDepartTime = DateUtil.dateToStr(shipFlightPlan.getPlanDepartTime(),"yyyy-MM-dd HH:mm:ss");
            String planPassTime = DateUtil.dateToStr(shipFlightPlan.getPlanPassTime(),"yyyy-MM-dd HH:mm:ss");
            String planCloseTime = DateUtil.dateToStr(shipFlightPlan.getPlanCloseTime(),"yyyy-MM-dd HH:mm:ss");

            jobQueryDto.setPlanArriveTime(planArriveTime == null ?"暂无":planArriveTime);
            jobQueryDto.setPlanDepartTime(planDepartTime == null ?"暂无":planDepartTime);
            jobQueryDto.setPlanPassTime(planPassTime == null ?"暂无":planPassTime);
            jobQueryDto.setPlanCloseTime(planCloseTime == null ?"暂无":planCloseTime);
            jobQueryDto.setCapacity(shipFlightPlan.getCapacity()==null?-1:shipFlightPlan.getCapacity());
            //jobQueryDto.setPlanPilotageTime(DateUtil.dateToStr(shipFlightPlan.getPlanPilotageTime(),"yyyyMMddHHmmss"));
            jobQueryDto.setCreateUser(shipFlightPlan.getCreateUser());
            jobQueryDto.setStatus(shipFlightPlan.getPlanStatus());



            list.add(jobQueryDto);
        }
        return list;
    }

    public List<JobQueryDto> shipFlightPlan2JobHistoryQueryDto(List<ShipFlightPlan> pageResult) throws Exception{
        List<JobQueryDto> list = new ArrayList<>();
        //List<String> shipFlightPlanIds = new ArrayList<>();
        for(ShipFlightPlan shipFlightPlan : pageResult){
            JobQueryDto jobQueryDto = new JobQueryDto();
            jobQueryDto.setLanes(shipFlightPlan.getShipLaneList());
            if(shipFlightPlan.getShipBoat() != null){
                BeanUtils.copyProperties(shipFlightPlan.getShipBoat(),jobQueryDto);
                jobQueryDto.setShipNameZh(shipFlightPlan.getShipBoat().getShipZhName());
            }
            if(shipFlightPlan.getShipBerth() != null){
                BeanUtils.copyProperties(shipFlightPlan.getShipBerth(),jobQueryDto);
            }
            BeanUtils.copyProperties(shipFlightPlan,jobQueryDto);
            jobQueryDto.setShipAgent(shipFlightPlan.getShipAgent());
            jobQueryDto.setPortType(shipFlightPlan.getPortType());

            String planArriveTime = DateUtil.dateToStr(shipFlightPlan.getPlanArriveTime(),"yyyy-MM-dd HH:mm:ss");
            String planDepartTime = DateUtil.dateToStr(shipFlightPlan.getPlanDepartTime(),"yyyy-MM-dd HH:mm:ss");
            String planPassTime = DateUtil.dateToStr(shipFlightPlan.getPlanPassTime(),"yyyy-MM-dd HH:mm:ss");
            String planCloseTime = DateUtil.dateToStr(shipFlightPlan.getPlanCloseTime(),"yyyy-MM-dd HH:mm:ss");
            String actualArriveTime = DateUtil.dateToStr(shipFlightPlan.getActualArriveTime(),"yyyy-MM-dd HH:mm:ss");
            String actualDepartTime = DateUtil.dateToStr(shipFlightPlan.getActualDepartTime(),"yyyy-MM-dd HH:mm:ss");
            String actualPassTime = DateUtil.dateToStr(shipFlightPlan.getActualPassTime(),"yyyy-MM-dd HH:mm:ss");
            String actualCloseTime = DateUtil.dateToStr(shipFlightPlan.getActualCloseTime(),"yyyy-MM-dd HH:mm:ss");

            jobQueryDto.setPlanArriveTime(planArriveTime == null ?"暂无":planArriveTime);
            jobQueryDto.setPlanDepartTime(planDepartTime == null ?"暂无":planDepartTime);
            jobQueryDto.setPlanPassTime(planPassTime == null ?"暂无":planPassTime);
            jobQueryDto.setPlanCloseTime(planCloseTime == null ?"暂无":planCloseTime);
            jobQueryDto.setActualArriveTime(actualArriveTime == null ?"暂无":actualArriveTime);
            jobQueryDto.setActualDepartTime(actualDepartTime == null ?"暂无":actualDepartTime);
            jobQueryDto.setActualPassTime(actualPassTime == null ?"暂无":actualPassTime);
            jobQueryDto.setActualCloseTime(actualCloseTime == null ?"暂无":actualCloseTime);

            jobQueryDto.setLuggageNumber(shipFlightPlan.getInboundLuggage()==null?-1:shipFlightPlan.getInboundLuggage());
            jobQueryDto.setPassNumber(shipFlightPlan.getInboundNumber()==null?-1:shipFlightPlan.getInboundNumber());
            jobQueryDto.setCapacity(shipFlightPlan.getCapacity()==null?-1:shipFlightPlan.getCapacity());


            jobQueryDto.setCreateTime(DateUtil.dateToStr(shipFlightPlan.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            long parkingHour = DateUtil.dateSubtract(shipFlightPlan.getActualArriveTime(), shipFlightPlan.getActualDepartTime());
            jobQueryDto.setParkingHour(parkingHour);

            //反馈新增8字段 by2020.1.2.
            String leavePassTime = DateUtil.dateToStr(shipFlightPlan.getLeavePassTime(),"yyyy-MM-dd HH:mm:ss");
            String leaveCloseTime = DateUtil.dateToStr(shipFlightPlan.getLeaveCloseTime(),"yyyy-MM-dd HH:mm:ss");

            jobQueryDto.setLeavePassTime(leavePassTime == null ?"暂无":leavePassTime);
            jobQueryDto.setLeaveCloseTime(leaveCloseTime == null ?"暂无":leaveCloseTime);
            jobQueryDto.setGarbageNum(shipFlightPlan.getGarbageNum());
            jobQueryDto.setAddWaterNum(shipFlightPlan.getAddWaterNum());
            jobQueryDto.setCraneNum(shipFlightPlan.getCraneNum());
            jobQueryDto.setForkliftNum(shipFlightPlan.getForkliftNum());
            jobQueryDto.setHelpWorkerNum(shipFlightPlan.getHelpWorkerNum());
            jobQueryDto.setSailorNum(shipFlightPlan.getSailorNum());


            list.add(jobQueryDto);
        }
        return list;
    }
}
