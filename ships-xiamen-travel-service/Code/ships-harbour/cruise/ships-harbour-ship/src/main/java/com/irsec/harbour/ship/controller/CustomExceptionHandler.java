package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.dto.BaseOutputDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    private final String noBodyError = "Required request body is missing";

    //参数不能读
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        //发送的参数异常
        logger.error("发送的参数异常");
        logger.error(ex.getMessage());

        BaseOutputDTO baseOutputDTO = new BaseOutputDTO();

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (ex.getMessage().startsWith(noBodyError)) {
            baseOutputDTO.setMsg("没有发送参数");
        } else {
            baseOutputDTO.setMsg(ex.getMessage());
        }

        baseOutputDTO.setStatus(-1);
/*        if(ex.getMessage().contains("JSON parse error")){
            return this.handleExceptionInternal(ex, baseOutputDTO, headers, , request);
        }*/
        return this.handleExceptionInternal(ex, baseOutputDTO, headers, HttpStatus.OK, request);

    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }

        if (body == null) {
            logger.error("未捕获异常");
            logger.error(ex.getMessage());

            BaseOutputDTO baseOutputDTO = new BaseOutputDTO();
            baseOutputDTO.setMsg(ex.getMessage());
            baseOutputDTO.setStatus(-1);

            return new ResponseEntity(baseOutputDTO, headers, status);

        } else {
            return new ResponseEntity(body, headers, status);
        }


    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {


        BindingResult bindingResult = ex.getBindingResult();

        List<String> errorList = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[")
                    .append(fieldError.getField())
                    .append("] ")
                    .append(fieldError.getDefaultMessage());
            errorList.add(sb.toString());
        }

        String msg = String.join(",", errorList);

        BaseOutputDTO baseOutputDTO = new BaseOutputDTO();
        baseOutputDTO.setMsg(msg);
        baseOutputDTO.setStatus(-1);

        return new ResponseEntity(baseOutputDTO, headers, status);
    }
}
