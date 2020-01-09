package com.irsec.harbour.ship.fliter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@WebFilter(filterName = "verifyFilter", urlPatterns = "/api/*")
public class Verify2CodeFilter extends BaseFilter {


    Logger logger = LoggerFactory.getLogger(Verify2CodeFilter.class);


    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String url = httpServletRequest.getRequestURI();
        if(url.compareTo("/api/v2/download/updateFile") == 0){
            logger.info("文件上传，跳过验证。IP: {}",servletRequest.getRemoteAddr());
            filterChain.doFilter(httpServletRequest, servletResponse);
            return;
        }
        if(url.compareTo("/api/v1/sys/time") == 0){
            logger.info("获取系统时间，跳过验证。IP: {}",servletRequest.getRemoteAddr());
            filterChain.doFilter(httpServletRequest, servletResponse);
            return;
        }

        logger.info(String.format("开启验证，RemoteAddr=%s,URL=%s", servletRequest.getRemoteAddr(), httpServletRequest.getRequestURI()));

        String sign = httpServletRequest.getHeader("sign");

        String timestamp = httpServletRequest.getHeader("timestamp");
        String apiKey = httpServletRequest.getHeader("apiKey");


        //判断sign、timestamp、apiKey是否为空
        if (StringUtils.isEmpty(sign)) {
            returnError(httpServletRequest, servletResponse, "http header中没有sign");
            return;
        }

        if (StringUtils.isEmpty(timestamp)) {
            returnError(httpServletRequest, servletResponse, "http header中没有timestamp");
            return;
        }

        if (StringUtils.isEmpty(apiKey)) {
            returnError(httpServletRequest, servletResponse, "http header中没有apiKey");
            return;
        }

        //判断时间戳
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date date = simpleDateFormat.parse(timestamp);
            Date now = new Date();
            //如果时间差异大于5分钟
            if (Math.abs(date.getTime() - now.getTime()) > 1000 * 300) {
                SimpleDateFormat nowSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                returnError(httpServletRequest, servletResponse, "http header中timestamp时间与服务器差异过大.服务器时间:" + nowSimpleDateFormat.format(now));
                return;
            }
        } catch (ParseException e) {
            logger.error("时间戳转换出错");
            logger.error(timestamp);
            returnError(httpServletRequest, servletResponse, "http header中timestamp不是一个有效的时间类型.");
            return;
        }

        //判断md5码是否一致
        url = url.substring(1);
        //排除对文件下载url限制
        if(url.contains("api/v2/download/terminal")){
            url = "api/v2/download/terminal";
        }
        String res2 = DigestUtils.md5DigestAsHex((url + timestamp + apiKey).getBytes());
        if (sign.equalsIgnoreCase(res2)) {
            filterChain.doFilter(httpServletRequest, servletResponse);
        } else {
            logger.error("签名算法不匹配，服务器计算结果=" + res2 + ";发送数据=" + sign);
            logger.error(String.format("url:=%s;timestamp=%s;apiKey=%s", url, timestamp, apiKey));
            returnError(httpServletRequest, servletResponse, "签名算法不匹配.");
            return;
        }
    }


    @Override
    public void destroy() {

    }
}
