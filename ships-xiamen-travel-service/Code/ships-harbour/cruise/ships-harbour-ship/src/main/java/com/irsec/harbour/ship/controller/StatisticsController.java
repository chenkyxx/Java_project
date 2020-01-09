package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.ShipFlight;
import com.irsec.harbour.ship.data.entity.ShipFlightPlan;
import com.irsec.harbour.ship.data.entity.ShipManualCheckin;
import com.irsec.harbour.ship.data.entity.ShipPassenger;
import com.irsec.harbour.ship.data.impl.FlightPlanDaoImpl;
import com.irsec.harbour.ship.data.impl.ShipFlightImpl;
import com.irsec.harbour.ship.data.impl.ShipManualCheckinImpl;
import com.irsec.harbour.ship.data.impl.ShipPassengerImpl;
import com.irsec.harbour.ship.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther: Jethro
 * @Date: 2019/9/9 10:51
 * @Description:统计接口
 */


@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    ShipPassengerImpl shipPassengerDao;
    @Autowired
    ShipFlightImpl shipFlightDao;
    @Autowired
    FlightPlanDaoImpl flightPlanDao;
    @Autowired
    ShipManualCheckinImpl shipManualCheckinImpl;


    @PostMapping("/check/num")
    public ResponseEntity<String> checkNum(@RequestBody BaseInputDTO params) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        RowOutputDTO rowsResponseParams = new RowOutputDTO();

        try {
            Date startTime = DateUtil.getBeforeOneYear();
            //先近一年的查询通关人数
            List<ShipManualCheckin> shipManualCheckinList = shipManualCheckinImpl.findAllByCheckTime(startTime, new Date());

            List<ShipFlight> shipFlightList = shipFlightDao.findAllByCreateTime(startTime, new Date());
            //筛选出近一个月的通关人数
            Date oneMonthAgo = DateUtil.getBeforeOneMonth();
            List<ShipManualCheckin> oneMonthAgoShipManualCheckinList = shipManualCheckinList.stream().filter(m -> m.getCheckingTime().after(oneMonthAgo)).collect(Collectors.toList());

            List<ShipFlight> oneMonthAgoFlightList = shipFlightList.stream().filter(flight -> flight.getSailDate().after(oneMonthAgo)).collect(Collectors.toList());
            List<ShipManualCheckin> todayShipManualCheckinList = GetToday(oneMonthAgoShipManualCheckinList);


            //再筛选出当天的通关人数
            List<HashMap<String, Integer>> hourNumMapList = new ArrayList<>();
            int todayNum = statisticsToday(hourNumMapList, oneMonthAgoShipManualCheckinList);
            int passNumYear = shipManualCheckinList != null ? shipManualCheckinList.size() : 0;
            int passNumMonth = oneMonthAgoShipManualCheckinList != null ? oneMonthAgoShipManualCheckinList.size() : 0;
            int flightNumYear = shipFlightList != null ? shipFlightList.size() : 0;
            int flightNumMonth = oneMonthAgoFlightList != null ? oneMonthAgoFlightList.size() : 0;
            HashMap<String, Object> result = new HashMap<>();

            result.put("passNumYear", passNumYear);
            result.put("passNumMonth", passNumMonth);
            //当天的过检人数
            result.put("passNumToday", todayNum);
            result.put("passNumHour", hourNumMapList);
            result.put("flightNumYear", flightNumYear);
            result.put("flightNumMonth", flightNumMonth);

            //当天的数据按设备编号
            result.put("passNumTodayByDevice", GetTodayByChannel(oneMonthAgoShipManualCheckinList));

            //查询当天的游客人数
            String[] flightIds = GetTodayFlight(shipFlightList);
            int countPassengerOfToday = shipPassengerDao.countAllByFlightIdIn(flightIds);
            result.put("passengerToday", countPassengerOfToday);

            //当天游客人数-过检人数 = 未过检人数
            result.put("unPassToday", countPassengerOfToday - todayNum);

            //当天的人工通行人数
            long manualPassNumToday = todayShipManualCheckinList.stream().filter(s -> s.getManualPass() == 1).count();
            result.put("manualPassNumToday", manualPassNumToday);


            if (shipManualCheckinList != null) {
                shipManualCheckinList.clear();
            }
            if (oneMonthAgoShipManualCheckinList != null) {
                oneMonthAgoShipManualCheckinList.clear();
            }
            if (shipFlightList != null) {
                shipFlightList.clear();
            }
            if (oneMonthAgoFlightList != null) {
                oneMonthAgoFlightList.clear();
            }
            rowsResponseParams.setData(result);
        } catch (Exception e) {
            logger.error("数据查询出错", e);
            return responseBuilder.Error("数据查询出错");
        }

        return responseBuilder.OK(rowsResponseParams);
    }


    private List<ShipManualCheckin> GetToday(List<ShipManualCheckin> oneMonthAgoList) {
        List<ShipManualCheckin> collect = oneMonthAgoList.stream().filter(passenger -> passenger.getCheckingTime().after(DateUtil.getToday0dian())).collect(Collectors.toList());

        return collect;
    }

    private String[] GetTodayFlight(List<ShipFlight> flights) {
        Date date1 = DateUtil.getToday0dian();
        Date date2 = DateUtil.addDays(date1, 1);

        String[] shipFlights = flights.stream().filter(s -> s.getSailDate().before(date2) && s.getSailDate().after(date1)).map(ShipFlight::getId).toArray(String[]::new);

        return shipFlights;
    }


    private List<Map<String, String>> GetTodayByChannel(List<ShipManualCheckin> oneMonthAgoList) {
        List<ShipManualCheckin> list = GetToday(oneMonthAgoList);

        Map<String, Integer> map = new HashMap<>();

        for (ShipManualCheckin p : list) {
            String deviceNo = p.getCheckDeviceNo();

            if (map.containsKey(deviceNo)) {
                Integer val = map.get(deviceNo);
                map.put(deviceNo, val + 1);
            } else {
                map.put(deviceNo, 1);
            }
        }

        List<Map<String, String>> result = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Map<String, String> map2 = new HashMap<>();
            map2.put("device", entry.getKey());
            map2.put("num", entry.getValue().toString());
            result.add(map2);
        }

        return result;
    }


    private int statisticsToday(List<HashMap<String, Integer>> hourNumMapList, List<ShipManualCheckin> oneMonthAgoList) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        ConcurrentHashMap<Integer, Integer> hourNumMap = new ConcurrentHashMap<>();
        int hour = Integer.valueOf(sdf.format(new Date()));
        for (int i = 0; i <= hour; i++) {
            hourNumMap.put(i, 0);
        }
        List<ShipManualCheckin> collect = oneMonthAgoList.stream().filter(passenger -> passenger.getCheckingTime().after(DateUtil.getToday0dian())).collect(Collectors.toList());
        collect.stream().forEach(s -> {
            int h = Integer.valueOf(sdf.format(s.getCheckingTime()));
            int num = hourNumMap.getOrDefault(h, 0);
            hourNumMap.put(h, num + 1);
        });

        for (Integer h : hourNumMap.keySet()) {
            HashMap<String, Integer> result = new HashMap<>();
            result.put("time", h);
            result.put("passNum", hourNumMap.getOrDefault(h, 0));
            hourNumMapList.add(result);
        }
        return collect.size();
    }

    @PostMapping("/trip")
    public ResponseEntity<String> trip(@RequestBody BaseInputDTO params) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        RowsOutputDTO rowsResponseParams = new RowsOutputDTO();

        try {
            Date startTime = DateUtil.getBeforOneHour();
            Date endTime = DateUtil.getAfterOneHour();
            List<ShipFlightPlan> list = flightPlanDao.findAllByTime(startTime, endTime);

            List<JobQueryDto> result = new ArrayList<>();
            for (ShipFlightPlan shipFlightPlan : list) {
                JobQueryDto jobQueryDto = new JobQueryDto();
                jobQueryDto.setId(shipFlightPlan.getId());
                if (shipFlightPlan.getShipBoat() != null) {
                    jobQueryDto.setShipNameZh(shipFlightPlan.getShipBoat().getShipZhName());
                }
                if (shipFlightPlan.getShipBerth() != null) {
                    jobQueryDto.setBerthName(shipFlightPlan.getShipBerth().getBerthName());
                }

                jobQueryDto.setStatus(0);
                jobQueryDto.setCapacity(shipFlightPlan.getCapacity());

                //shipFlightPlan.getId() -> 关联的航班 -> 查询到通关的人数
                int passNum = flightPlanDao.findPassNumberByFlightPlanId(shipFlightPlan.getId());

                jobQueryDto.setPassNumber(passNum);

                String planPassTime = DateUtil.dateToStr(shipFlightPlan.getPlanPassTime(), "yyyy-MM-dd HH:mm:ss");
                String planCloseTime = DateUtil.dateToStr(shipFlightPlan.getPlanCloseTime(), "yyyy-MM-dd HH:mm:ss");
                String planArriveTime = DateUtil.dateToStr(shipFlightPlan.getPlanArriveTime(), "yyyy-MM-dd HH:mm:ss");
                String planDepartTime = DateUtil.dateToStr(shipFlightPlan.getPlanDepartTime(), "yyyy-MM-dd HH:mm:ss");
                jobQueryDto.setPlanPassTime(planPassTime == null ? "暂无" : planPassTime);
                jobQueryDto.setPlanCloseTime(planCloseTime == null ? "暂无" : planCloseTime);
                jobQueryDto.setPlanArriveTime(planArriveTime == null ? "暂无" : planArriveTime);
                jobQueryDto.setPlanDepartTime(planDepartTime == null ? "暂无" : planDepartTime);
                result.add(jobQueryDto);
            }
            rowsResponseParams.setRows(result);
            rowsResponseParams.setSubtotal(result.size());
            rowsResponseParams.setTotal(result.size());

        } catch (Exception e) {
            logger.error("数据查询出错", e);
            return responseBuilder.Error("数据查询出错");
        }

        return responseBuilder.OK(rowsResponseParams);
    }
}
