package com.irsec.harbour.ship.data.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OutputDTOBuilder {

    private String reqId;


    public OutputDTOBuilder(String reqId) {
        this.reqId = reqId;
    }


    public ResponseEntity<String> OK() {
        BaseOutputDTO baseOutputDTO = new BaseOutputDTO();
        baseOutputDTO.setReqId(reqId);
        baseOutputDTO.setMsg("OK");
        baseOutputDTO.setStatus(0);

        return new ResponseEntity(baseOutputDTO, HttpStatus.OK);
    }

    public ResponseEntity<String> OK(String msg) {
        BaseOutputDTO baseOutputDTO = new BaseOutputDTO();
        baseOutputDTO.setReqId(reqId);
        baseOutputDTO.setMsg(msg);
        baseOutputDTO.setStatus(0);

        return new ResponseEntity(baseOutputDTO, HttpStatus.OK);
    }

    public ResponseEntity<String> OK(BaseOutputDTO baseOutputDTO) {
        baseOutputDTO.setReqId(reqId);
        baseOutputDTO.setMsg("OK");
        baseOutputDTO.setStatus(0);

        return new ResponseEntity(baseOutputDTO, HttpStatus.OK);
    }


    public ResponseEntity<String> BadRequest(String msg) {
        BaseOutputDTO baseOutputDTO = new BaseOutputDTO();
        baseOutputDTO.setMsg(msg);
        baseOutputDTO.setStatus(-1);
        baseOutputDTO.setReqId(reqId);

        return new ResponseEntity(baseOutputDTO, HttpStatus.OK);
    }

    public ResponseEntity<String> Error(String msg) {
        BaseOutputDTO baseOutputDTO = new BaseOutputDTO();
        baseOutputDTO.setMsg(msg);
        baseOutputDTO.setStatus(-1);
        baseOutputDTO.setReqId(reqId);

        return new ResponseEntity(baseOutputDTO, HttpStatus.OK);
    }

}
