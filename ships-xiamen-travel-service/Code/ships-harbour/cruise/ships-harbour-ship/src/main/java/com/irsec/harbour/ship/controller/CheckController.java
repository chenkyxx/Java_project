package com.irsec.harbour.ship.controller;

import cn.hutool.core.util.StrUtil;
import com.irsec.harbour.ship.data.bean.CertificateTypeEnum;
import com.irsec.harbour.ship.data.bean.ConstanCollection;
import com.irsec.harbour.ship.data.bean.UploadResultBean;
import com.irsec.harbour.ship.data.dto.*;
import com.irsec.harbour.ship.data.entity.*;
import com.irsec.harbour.ship.data.group.ValidatedGroup1;
import com.irsec.harbour.ship.data.group.ValidatedGroup3;
import com.irsec.harbour.ship.data.impl.ShipFlightImpl;
import com.irsec.harbour.ship.data.impl.ShipManualCheckinImpl;
import com.irsec.harbour.ship.data.impl.ShipPassengerImpl;
import com.irsec.harbour.ship.service.CheckRecoderPusher;
import com.irsec.harbour.ship.service.CheckService;
import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.GoFastUtils;
import com.irsec.harbour.ship.utils.SecretUtils;
import com.irsec.harbour.ship.utils.UUIDTool;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.*;

/**
 * @Auther: Jethro
 * @Date: 2019/9/20 17:12
 * @Description: 接口文档 6.* 相关接口
 */

@RestController
@RequestMapping("/api/v2/check")
@Slf4j
public class CheckController {

    @Autowired
    ShipPassengerImpl shipPassengerDao;

    @Autowired
    ShipManualCheckinImpl shipManualCheckinImpl;

    @Autowired
    CheckService checkService;

    @Autowired
    CheckRecoderPusher checkRecoderPusher;

    @Autowired
    ShipFlightImpl shipFlightDao;

    @Value("${uploadImg.url}")
    private String uploadImgUrl;

    public static ShipManualCheckin preShipManualCheckIn = new ShipManualCheckin();

    /**
     * @param params
     * @param result
     * @return verifyResult
     * 0 验证通过
     * -1 验证失败
     * -2 未购票 （在人脸验证情况下才会出现，指通过证件号查找不到相应的旅客信息）
     * -3 该票非当天的航班
     * -4 非验票时间段
     */
    @PostMapping("/faceinfo")
    public ResponseEntity checking(@RequestBody @Validated({ValidatedGroup1.class, ValidatedGroup3.class, FaceCheckDTO.ValidateGroupManual.class}) OneInputDTO<FaceCheckDTO> params, BindingResult result) {

        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        FaceCheckDTO faceCheckDTO = params.getData();
        if (result.hasErrors()) {
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }
        if (faceCheckDTO == null) {
            //数据为空
            return responseBuilder.BadRequest("数据有误，请重试");
        }
        //判断性别
        String sex = faceCheckDTO.getCertificateGender();
        if (StrUtil.isNotEmpty(sex) && !("男".equals(sex) || "女".equals(sex) || "F".equals(sex) || "M".equals(sex))) {
            return responseBuilder.BadRequest(StrUtil.format("数据中 CertificateGender 错误，{} 不是有效的性别", sex));
        }


        ShipManualCheckin shipManualCheckin = new ShipManualCheckin();
        initShipFaceCompareRecord(shipManualCheckin, faceCheckDTO, 0);
        MapOutputDTO mapOutputDTO = new MapOutputDTO();
        checkService.initMap(mapOutputDTO, true);
        //TicketCheckingOutputDTO responseParams = new TicketCheckingOutputDTO();
        ShipPassenger shipPassenger = null;
        ShipFlight flightShip = null;
        try {
            //根据证件类型和证件号查询出该旅客的信息
            if (faceCheckDTO.getCertificateType() == CertificateTypeEnum.CERTIFICATE_TYPE_ID_CARD.getTypeInt()) {
                //如果是身份证就查询旅客的身份证信息
                shipPassenger = shipPassengerDao.findByIdNumber(faceCheckDTO.getCertificateId());
            } else {
                shipPassenger = shipPassengerDao.findByPassportId(faceCheckDTO.getCertificateId());
            }
            if (shipPassenger != null) {
                flightShip = shipPassenger.getShipFlight();
            }

            if (faceCheckDTO.getManualPass() == 1) {
                //checkService.manualCheckIn(shipManualCheckin,faceCheckDTO.getCertificateId(),faceCheckDTO.getCertificateType());
                setManualCheckinValue(shipManualCheckin, shipPassenger, flightShip, faceCheckDTO);
                mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, ConstanCollection.CHECK_SUCCESS);
                return responseBuilder.OK(mapOutputDTO);
            }

            if (shipPassenger == null || StringUtils.isEmpty(shipPassenger.getId())) {
                //保存此次验票信息
                log.info("未查询到该旅客的信息，证件类型：{}，证件号：{}", faceCheckDTO.getCertificateType(), faceCheckDTO.getCertificateId());
                shipManualCheckin.setVerifyResult(ConstanCollection.CHECK_FAILED_NOT_FOUND_PASSENGER);
                //未购票
                mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, shipManualCheckin.getVerifyResult());
                mapOutputDTO.setMsg("没有找到该旅客的购票信息");
                return responseBuilder.OK(mapOutputDTO);
            } else {
                //获取航班信息

                if (flightShip == null) {
                    log.info("未查询到该旅客的航班信息，证件类型：{}，证件号：{}", faceCheckDTO.getCertificateType(), faceCheckDTO.getCertificateId());
                    shipManualCheckin.setVerifyResult(ConstanCollection.CHECK_FAILED_ERROR_CODE);
                    //未购票
                    mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, shipManualCheckin.getVerifyResult());
                    mapOutputDTO.setMsg("没有找到该旅客的购票信息");
                    return responseBuilder.OK(mapOutputDTO);
                }
                //判断是否在验票时间段内
                if (!checkService.isInCheckTicketTime(flightShip.getFlightPlanId())) {
                    judgeDate(flightShip, shipManualCheckin, mapOutputDTO);
                    shipManualCheckin.setVerifyResult(ConstanCollection.CHECK_NOT_IN_CHECK_TIME);
                    mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, shipManualCheckin.getVerifyResult());
                    mapOutputDTO.setMsg("当前时间不在验票时间内");
                    return responseBuilder.OK(mapOutputDTO);
                }

                //判断日期是今天
                judgeDate(flightShip, shipManualCheckin, mapOutputDTO);
                setManualCheckinValue(shipManualCheckin, shipPassenger, flightShip, faceCheckDTO);
                //先查询当日该证件是否有验证通过的记录，如果有则直接返回-5
                if (shipManualCheckinImpl.findPassportIdIsPassInToday(shipPassenger.getPassportId(), shipPassenger.getFlightId())) {
                    log.info("证件号：{} ,航班id:{} 旅客当日该航班已经验证通过,不能重复通过", shipPassenger.getPassportId(), shipPassenger.getFlightId());
                    shipManualCheckin.setVerifyResult(ConstanCollection.CHECK_TICKET_HAS_CHECKED);
                    mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, shipManualCheckin.getVerifyResult());
                    return responseBuilder.OK(mapOutputDTO);
                }
                //获取到指定格式的返回信息
                //HashMap<String,Object> resultMap = shipPassengerDao.getPassengerAndFlightAndLuggages(shipPassenger);
                mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, shipManualCheckin.getVerifyResult());

                return responseBuilder.OK(mapOutputDTO);
            }
        } catch (Exception e) {
            log.error("验证人脸信息过程中出现错误", e);
            return responseBuilder.Error("验证人脸信息过程中出现错误");
        } finally {
            //保存验证记录
            shipManualCheckinImpl.saveCheckin(shipManualCheckin, shipPassenger);
            if (shipManualCheckin.getVerifyResult() == ConstanCollection.CHECK_SUCCESS) {
                checkRecoderPusher.sendToSingleWindow(shipManualCheckin, false);
            }
            //faceCompareRecordDao.save(shipFaceCompareRecord);
            long total = shipManualCheckinImpl.getTodayPassNumber(faceCheckDTO.getDeviceNo());
            mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_TOTAL, total);
        }
    }

    private void setManualCheckinValue(ShipManualCheckin shipManualCheckin, ShipPassenger shipPassenger, ShipFlight flightShip, FaceCheckDTO data) {

        if (data != null && shipPassenger != null) {
            //判断名字是否一致，进行修改
            if (!StringUtils.isEmpty(data.getCertificateName())) {
                if (!StringUtils.isEmpty(shipPassenger.getPassengerNameCh())) {
                    if (shipPassenger.getPassengerNameCh().compareTo(data.getCertificateName()) != 0) {
                        shipPassenger.setPassengerNameCh(data.getCertificateName());
                    }
                } else {
                    shipPassenger.setShipNameCh(data.getCertificateName());
                }
            }
            if (!StringUtils.isEmpty(data.getCertificateNameEN())) {
                if (!StringUtils.isEmpty(shipPassenger.getPassengerNameEn())) {
                    if (shipPassenger.getPassengerNameEn().compareTo(data.getCertificateNameEN()) != 0) {
                        shipPassenger.setPassengerNameEn(data.getCertificateNameEN());
                    }
                } else {
                    shipPassenger.setPassengerNameEn(data.getCertificateNameEN());
                }
            }


            if (!StringUtils.isEmpty(data.getCertificateGender())) {
                if ("男".compareTo(data.getCertificateGender()) == 0) {
                    data.setCertificateGender("M");
                } else if ("女".compareTo(data.getCertificateGender()) == 0) {
                    data.setCertificateGender("F");
                }

                if (StringUtils.isEmpty(shipPassenger.getSex())) {
                    shipPassenger.setSex(data.getCertificateGender());
                } else if (data.getCertificateGender().compareTo(shipPassenger.getSex()) != 0) {
                    shipPassenger.setSex(data.getCertificateGender());
                }
            }

            if (!StringUtils.isEmpty(data.getCertificateBirth())) {
                Date birthDay = DateUtil.strToDate(data.getCertificateBirth(), "yyyy-MM-dd");
                if (shipPassenger.getBirthDay() == null) {
                    shipPassenger.setBirthDay(birthDay);
                } else {
                    if (!DateUtil.dateEqual(shipPassenger.getBirthDay(), birthDay)) {
                        shipPassenger.setBirthDay(birthDay);
                    }
                }
            }

            shipManualCheckin.setPassengerId(shipPassenger.getId());
            shipManualCheckin.setIdNumber(shipPassenger.getIdNumber());
            shipManualCheckin.setContact(shipPassenger.getContact());
            shipManualCheckin.setCountry(shipPassenger.getCountry());
        }

        shipManualCheckin.setBarcode(data.getCertificateId());

        if (StringUtils.isEmpty(shipManualCheckin.getPassengerName())) {
            if (StringUtils.isEmpty(data.getCertificateName())) {
                shipManualCheckin.setPassengerName(data.getCertificateNameEN());
            } else {
                shipManualCheckin.setPassengerName(data.getCertificateName());
            }
        }

        //如果此时名字还是为空
        if (StringUtils.isEmpty(shipManualCheckin.getPassengerName())) {
            shipManualCheckin.setPassengerName("未知");
        }

        //有可能是身份证
        //身份证不属于边检证件类型
        if (data.getCertificateType() == CertificateTypeEnum.CERTIFICATE_TYPE_ID_CARD.getTypeInt() && shipPassenger != null) {
            //刷人脸的人用的身份证，且查到了这个旅客
            shipManualCheckin.setCertificateType(shipPassenger.getCertificateType());
        } else {
            //
            shipManualCheckin.setCertificateType(data.getCertificateType());
        }

        if (data.getCertificateType() == CertificateTypeEnum.CERTIFICATE_TYPE_ID_CARD.getTypeInt()) {
            shipManualCheckin.setIdNumber(data.getCertificateId());
            if (shipPassenger != null) {
                shipManualCheckin.setPassportId(shipPassenger.getPassportId());
            }
        } else {
            shipManualCheckin.setPassportId(data.getCertificateId());
        }
        if (!StringUtils.isEmpty(data.getCertificateGender())) {
            if ("男".compareTo(data.getCertificateGender()) == 0) {
                data.setCertificateGender("M");
            } else if ("女".compareTo(data.getCertificateGender()) == 0) {
                data.setCertificateGender("F");
            }

            shipManualCheckin.setSex(data.getCertificateGender());
        }

        if (!StringUtils.isEmpty(data.getCertificateBirth())) {
            shipManualCheckin.setBirthDay(DateUtil.strToDate(data.getCertificateBirth(), "yyyy-MM-dd"));
        }
        if (!StringUtils.isEmpty(data.getCertificateNation())) {
            shipManualCheckin.setCountry(data.getCertificateNation());
        }


        if (flightShip != null) {
            shipManualCheckin.setShipNo(flightShip.getShipNo());
            shipManualCheckin.setFlightId(flightShip.getId());
        } else {
            Date start = DateUtil.getToday0dian();
            Date end = DateUtil.getToday24dian();

            Specification<ShipFlight> specification = new Specification<ShipFlight>() {
                @Override
                public Predicate toPredicate(Root<ShipFlight> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<>();
                    list.add(cb.greaterThanOrEqualTo(root.get("sailDate"), start));
                    list.add(cb.lessThanOrEqualTo(root.get("sailDate"), end));
                    Predicate array[] = new Predicate[list.size()];
                    list.toArray(array);

                    return query.where(array).getRestriction();
                }
            };

            Page<ShipFlight> flightList = shipFlightDao.findAll(specification, PageRequest.of(0, 10, Sort.Direction.DESC, "sailDate"));
            if (flightList.getTotalElements() == 1) {
                flightShip = flightList.getContent().get(0);
                shipManualCheckin.setShipNo(flightShip.getShipNo());
                shipManualCheckin.setFlightId(flightShip.getId());
            }
        }


        checkService.judgeShipInfo(shipManualCheckin);
    }

    @PostMapping("/faceinfo/offline")
    public ResponseEntity checking(@RequestBody @Validated({ValidatedGroup1.class, ValidatedGroup3.class}) ManyInputDTO<FaceCheckDTO> params, BindingResult result) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        if (result.hasErrors()) {
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        try {
            //一次最多上传50条
            //验证输入的信息
            List<FaceCheckDTO> datas = params.getDatas();
            if (datas == null || datas.isEmpty()) {
                return responseBuilder.BadRequest("datas不能为空");
            }
            if (datas.size() > 50) {
                return responseBuilder.BadRequest("datas长度不能超过50条");
            }
            for (FaceCheckDTO faceCheckDTO : datas) {
                ShipManualCheckin shipManualCheckin = new ShipManualCheckin();
                initShipFaceCompareRecord(shipManualCheckin, faceCheckDTO, 1);
                try {
                    ShipPassenger shipPassenger = null;
                    //根据证件类型和证件号查询出该旅客的信息
                    if (faceCheckDTO.getCertificateType() == CertificateTypeEnum.CERTIFICATE_TYPE_ID_CARD.getTypeInt()) {
                        //如果是身份证就查询旅客的身份证信息
                        shipPassenger = shipPassengerDao.findByIdNumber(faceCheckDTO.getCertificateId());
                    } else {
                        shipPassenger = shipPassengerDao.findByCertificateTypeAndCertifiId(faceCheckDTO.getCertificateType(), faceCheckDTO.getCertificateId());
                    }


                    if (shipPassenger == null || StringUtils.isEmpty(shipPassenger.getId())) {
                        //保存此次验票信息
                        log.info("未查询到该旅客的信息，证件类型：{}，证件号：{}", faceCheckDTO.getCertificateType(), faceCheckDTO.getCertificateId());
                        shipManualCheckin.setVerifyResult(ConstanCollection.CHECK_FAILED_NOT_FOUND_PASSENGER);
                        continue;
                    } else {
                        //获取航班信息
                        ShipFlight flightShip = shipPassenger.getShipFlight();
                        if (flightShip == null) {
                            log.info("未查询到该旅客的航班信息，证件类型：{}，证件号：{}", faceCheckDTO.getCertificateType(), faceCheckDTO.getCertificateId());
                            shipManualCheckin.setVerifyResult(ConstanCollection.CHECK_FAILED_ERROR_CODE);
                            continue;
                        }
                        judgeDate(flightShip, shipManualCheckin, null);
                        setManualCheckinValue(shipManualCheckin, shipPassenger, flightShip, null);

                    }
                } catch (Exception e) {
                    log.error("验证人脸信息过程中出现错误", e);
                    continue;
                } finally {
                    //保存验证记录
                    shipManualCheckinImpl.saveCheckin(shipManualCheckin, null);
                    if (shipManualCheckin.getVerifyResult() == ConstanCollection.CHECK_SUCCESS) {
                        checkRecoderPusher.sendToSingleWindow(shipManualCheckin, false);
                    }
                }
            }

        } catch (Exception e) {
            log.error("验证人脸信息过程中出现错误", e);
            return responseBuilder.Error("验证人脸信息过程中出现错误");
        }
        return responseBuilder.OK();
    }

    private void judgeDate(ShipFlight flightShip, ShipManualCheckin shipManualCheckin, MapOutputDTO mapOutputDTO) {
        //判断日期是今天
        boolean isFill = false;
        if (DateUtil.dateEqual(flightShip.getSailDate(), new Date())) {
            //验票成功
            shipManualCheckin.setVerifyResult(ConstanCollection.CHECK_SUCCESS);
            isFill = true;
        } else if (flightShip.getSailDate().after(new Date())) {
            shipManualCheckin.setVerifyResult(ConstanCollection.CHECK_FAILED_NOT_TODAY);
            isFill = true;
        } else {
            shipManualCheckin.setVerifyResult(ConstanCollection.CHECK_FAILED_ERROR_CODE);
        }
        if (isFill && mapOutputDTO != null) {
            mapOutputDTO.put(ConstanCollection.FIELD_SHIP_NO, flightShip.getShipNo());
            mapOutputDTO.put(ConstanCollection.FIELD_SHIP_NAME_CH, flightShip.getShipNameCh());
            mapOutputDTO.put(ConstanCollection.FIELD_SAILDATE, DateUtil.dateToStr(flightShip.getSailDate(), "yyyy-MM-dd HH:mm:ss"));
        }
    }


    private void initShipFaceCompareRecord(ShipManualCheckin shipManualCheckin, FaceCheckDTO faceCheckDTO, int optType) {
        if (faceCheckDTO.getCertificateType() != CertificateTypeEnum.CERTIFICATE_TYPE_ID_CARD.getTypeInt()) {
            //如果不等于0且不等于1，2，3就默认赋值为14护照
            faceCheckDTO.setCertificateType(ConstanCollection.getFaceCheckCertificateTypeMap().getOrDefault(faceCheckDTO.getCertificateType(), 14));
        }
        // 上传证件照和现场照
        byte[] livePhoto = SecretUtils.base64DecodeByte(faceCheckDTO.getLivePhoto());
        if (livePhoto != null) {
            UploadResultBean upload = GoFastUtils.uploadImgOkHttp(livePhoto, uploadImgUrl);
            if (upload != null) {
                String url = upload.getPath();
                faceCheckDTO.setLivePhoto(url);
            } else {
                faceCheckDTO.setLivePhoto(null);
                log.error("证件号：{} 的现场照上传失败", faceCheckDTO.getCertificateId());
            }
        }

        byte[] certificarePhoto = SecretUtils.base64DecodeByte(faceCheckDTO.getCertificatePhoto());
        if (certificarePhoto != null) {
            UploadResultBean upload = GoFastUtils.uploadImgOkHttp(certificarePhoto, uploadImgUrl);
            if (upload != null) {
                String url = upload.getPath();
                faceCheckDTO.setCertificatePhoto(url);
            } else {
                faceCheckDTO.setCertificatePhoto(null);
                log.error("证件号：{} 的证件照上传失败", faceCheckDTO.getCertificateId());
            }
        }

        //将所有初始值设置好
        shipManualCheckin.setUploadTime(new Date());
        shipManualCheckin.setCheckDeviceNo(faceCheckDTO.getDeviceNo());
        shipManualCheckin.setScore(faceCheckDTO.getScore());
        if (faceCheckDTO.getCertificateType() == CertificateTypeEnum.CERTIFICATE_TYPE_ID_CARD.getTypeInt()) {
            //如果是身份证，那么将这个字段保存到身份证字段中
            shipManualCheckin.setIdNumber(faceCheckDTO.getCertificateId());
        } else {
            shipManualCheckin.setPassportId(faceCheckDTO.getCertificateId());
        }
        shipManualCheckin.setCheckingTime(faceCheckDTO.getCheckTime());
        shipManualCheckin.setLivePhoto(faceCheckDTO.getLivePhoto());
        shipManualCheckin.setCertificatePhoto(faceCheckDTO.getCertificatePhoto());
        shipManualCheckin.setCompareResult(faceCheckDTO.getVerifyResult());
        shipManualCheckin.setId(UUIDTool.newUUID());
        shipManualCheckin.setOperationType(optType);
        shipManualCheckin.setManualPass(faceCheckDTO.getManualPass());
        shipManualCheckin.setBarcode(faceCheckDTO.getCertificateId());
    }

    /**
     * 验票
     */
    @PostMapping("/ticket/checking")
    //@Transactional
    public ResponseEntity checkTicket(@RequestBody @Validated({ValidatedGroup1.class, FaceCheckDTO.ValidateGroupManual.class}) OneInputDTO<TicketCheckingInputDTO> params, BindingResult result) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        TicketCheckingInputDTO data = params.getData();
        //String checkInfo = checkTicketChecking(data);
        if (result.hasErrors()) {
            return responseBuilder.Error(result.getFieldError().getDefaultMessage());
        }

        ShipManualCheckin checkin = new ShipManualCheckin();

        checkService.InitShipManualCheckin(checkin, data, 0);
        try {
            if (data.getManualPass() == 1) {
                MapOutputDTO mapOutputDTO = new MapOutputDTO();
                CheckService.initMap(mapOutputDTO, false);
                checkService.manualCheckIn(checkin, data.getUserBarcode(), -1);
                shipManualCheckinImpl.saveCheckin(checkin, null);
                checkRecoderPusher.sendToSingleWindow(checkin, false);

                int passNum = shipManualCheckinImpl.getTodayPassNumber(data.getCheckDeviceNo());
                mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_TOTAL, passNum);
                mapOutputDTO.put(ConstanCollection.FIELD_VERIFY_RESULT, ConstanCollection.CHECK_SUCCESS);
                return responseBuilder.OK(mapOutputDTO);
            }


            Map<String, Object> resultMap = checkService.check(checkin, data, true);
            MapOutputDTO mapOutputDTO = (MapOutputDTO) resultMap.get(ConstanCollection.TERMINAL);
            return responseBuilder.OK(mapOutputDTO);
        } catch (Exception ex) {
            log.error("数据处理出错，错误二维码.barcode=" + data.getUserBarcode());
            log.error(ex.getMessage());
            return responseBuilder.Error("数据处理出错.");
        }
    }

    /**
     * 离线上传
     *
     * @return
     */
    @PostMapping("/ticket/officeUpload")
    public ResponseEntity officeUpload(@RequestBody @Validated({ValidatedGroup1.class}) ManyInputDTO<TicketCheckingInputDTO> params, BindingResult result) {
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        try {
            //一次最多上传50条
            //验证输入的信息
            List<TicketCheckingInputDTO> datas = params.getDatas();
            if (result.hasErrors()) {
                return responseBuilder.Error(result.getFieldError().getDefaultMessage());
            }
            if (datas.size() > 50) {
                return responseBuilder.Error("每次最多上传50条验证数据");
            }

            //数据初始化
            for (TicketCheckingInputDTO data : datas) {
                ShipManualCheckin checkin = new ShipManualCheckin();
                checkService.InitShipManualCheckin(checkin, data, 1);
                Map<String, Object> resultMap = checkService.check(checkin, data, true);
            }
            return responseBuilder.OK();
        } catch (Exception ex) {
            log.error("离线上传数据处理出错");
            log.error(ex.getMessage());
            return responseBuilder.Error("离线上传数据处理出错.");
        }
    }
}
