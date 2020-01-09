package com.irsec.harbour.ship.fliter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter(filterName = "verifyApiKeyFilter", urlPatterns = "/api/*")
public class Verify3ApiKeyFilter extends BaseFilter {

    Map<String, List<String>> authorizationMap = new HashMap();

    //公司内部
    private final String internal = "48d534fc6e7f451ca376baaa750729fc";
    //单一窗口匹配
    private final String singleWindow = "3a8a427582004ce7a30cadb3e13cf057";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        //所有的api
        authorizationMap.put(internal, Arrays.asList("api/*"));

        //新增删除查询旅客, 航班查询
        authorizationMap.put(singleWindow, Arrays.asList(
                "api/v1/passenger/*",  //旅客
                "api/v1/flight/*", //航班查询
                "api/v1/sys/time", //服务器时间
                "api/v1/ticket/checking/query", //验票查询接口
                "/api/v1/ticket/luggage/*", //行李条验证//行李条离线上传//行李条查询
                "/api/v1/berth/*", //泊位增删改查
                "/api/v1/ship/*",//船舶增删改查
                "/api/v1/flightPlan/*"//靠离泊计划相关接口
        ));
    }

    //验证apiKey访问当前方法是否有效
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String apiKey = httpServletRequest.getHeader("apiKey");
        String url = httpServletRequest.getRequestURI().substring(1);



        if(url.compareTo("api/v2/download/updateFile") == 0 ){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (authorizationMap.containsKey(apiKey)) {
            for (String reg : authorizationMap.get(apiKey)) {
                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(url);
                if (matcher.find()) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            }

            //没有匹配的
            returnError(httpServletRequest, servletResponse, "对当前接口没有操作权限");
            return;

        } else {
            returnError(httpServletRequest, servletResponse, "apiKey没有授权");
            return;
        }


    }

    @Override
    public void destroy() {
        authorizationMap.clear();
        authorizationMap = null;
    }
}
