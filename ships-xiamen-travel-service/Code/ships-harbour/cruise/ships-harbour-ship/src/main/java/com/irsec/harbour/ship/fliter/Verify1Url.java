package com.irsec.harbour.ship.fliter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


@WebFilter
public class Verify1Url extends BaseFilter {


    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    private Set<String> urls = new HashSet<>();

    Logger logger = LoggerFactory.getLogger(Verify1Url.class);


    @Override
    public void init(FilterConfig filterConfig) {

        Map map = this.handlerMapping.getHandlerMethods();
        Iterator<?> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            PatternsRequestCondition patternsCondition = ((RequestMappingInfo) entry.getKey()).getPatternsCondition();
            urls.addAll(patternsCondition.getPatterns());
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
            IOException, ServletException {
        HttpServletRequest httpServletRequest;
        if (servletRequest instanceof HttpServletRequest) {
            httpServletRequest = (HttpServletRequest) servletRequest;
        } else {
            logger.error("servletRequest 不是 HttpServletRequest");
            return;
        }

        String url = httpServletRequest.getRequestURI();
        //增加对download 的url的支持
        if(url.compareTo("/api/v1/sys/time") == 0){
            logger.info("获取系统时间，跳过验证。IP: {}",servletRequest.getRemoteAddr());
            filterChain.doFilter(httpServletRequest, servletResponse);
            return;
        }

        if (urls.contains(url) || url.contains("/api/v2/download/") ) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            logger.info("验证url出错,url=" + url);
            returnError(httpServletRequest, servletResponse, "404 资源不存在.");
        }

    }

    @Override
    public void destroy() {
        urls.clear();
        urls = null;
    }
}
