package com.irsec.harbour.ship.fliter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irsec.harbour.ship.data.dto.BaseOutputDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public abstract class BaseFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(BaseFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    protected void returnError(HttpServletRequest httpServletRequest, ServletResponse servletResponse, String msg) {
        try {
            BaseOutputDTO baseOutputDTO = new BaseOutputDTO();

            try {
                baseOutputDTO.setReqId(getReqId(httpServletRequest));
            } catch (Exception ex) {
                logger.error("获取reqId出错");
                logger.error(ex.getMessage());
            }


            baseOutputDTO.setStatus(-1);
            baseOutputDTO.setMsg(msg);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(baseOutputDTO);

            if (!StringUtils.isEmpty(json)) {

                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.setContentType("application/json; charset=utf-8");
                PrintWriter printWriter = servletResponse.getWriter();

                printWriter.append(json);
            }
        } catch (Exception e) {
            logger.error("传回给调用端出错");
            logger.error(e.getMessage());
        }
    }

    //RequestBody只能拿一次
    private String getRequestBody(HttpServletRequest request) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    protected String getReqId(HttpServletRequest httpServletRequest) throws IOException {
        String body = getRequestBody(httpServletRequest);

        if (!StringUtils.isEmpty(body)) {
            ObjectMapper objectMapper = new ObjectMapper();
            HashMap<String, Object> bodyMap = objectMapper.readValue(body, HashMap.class);

            if (bodyMap.containsKey("reqId")) {
                Object reqId = bodyMap.get("reqId");

                if (reqId instanceof String) {
                    return reqId.toString();
                }
            }

        }

        return "";

    }
}
