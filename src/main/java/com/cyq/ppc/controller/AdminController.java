package com.cyq.ppc.controller;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.cyq.framework.common.utils.IpUtils;
import com.cyq.framework.common.utils.ParamsUtils;
import com.cyq.modules.security.AuthUser;
import com.cyq.modules.security.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * App 基类 控制器
 */
@Slf4j
public class AdminController extends ApiController {

    private static String NULL_VALUE = null;

    /**
     * 请求成功
     *
     * @param <T> 对象泛型
     * @return ignore
     */
    protected <T> R<T> success() {
        return R.ok(null);
    }

    /**
     * 获取request
     *
     * @return HttpServletRequest
     */
    public HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取Response
     *
     * @return HttpServletRequest
     */
    public HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getResponse();
    }

    /**
     * 获取参数
     *
     * @param name
     * @return
     */
    public String getParam(String name) {
        return getParam(name, NULL_VALUE);
    }

    /**
     * 参数不存在，取默认值
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public String getParam(String name, String defaultValue) {
        String value = getRequest().getParameter(name);
        if (StringUtils.isBlank(value) || "null".equals(value) || "undefined".equals(value)) {
            return defaultValue;
        } else {
            return value;
        }
    }

    /**
     * 获取请求参数 Map
     *
     * @return
     */
    public Map<String, String> getParameterMap() {
        return ParamsUtils.paramsToMap(getRequest());
    }

    /**
     * 获取查询参数
     *
     * @param url
     * @return
     */
    public Map<String, String> getQueryParameterMap(String url) {
        return ParamsUtils.getQueryParams(url);
    }


    /**
     * 获取客户端的IP地址
     *
     * @return
     */
    public String getIpAddr() {
        return IpUtils.getIpAddr(getRequest());
    }

    /**
     * 获取请求报文
     *
     * @return
     * @throws IOException
     */
    public String readRequest() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = getRequest().getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * 输出到响应流
     *
     * @param response
     * @param s
     */
    public void write(HttpServletResponse response, String s) {
        try (PrintWriter out = response.getWriter()) {
            out.print(s);
        } catch (IOException e) {
            log.error("返回结果错误", e);
        }
    }

    /**
     * 是否是苹果
     *
     * @param request
     * @return
     */
    public boolean isIOS(HttpServletRequest request) {
        String userAgentStr = request.getHeader(HttpHeaders.USER_AGENT);
        UserAgent userAgent = UserAgentUtil.parse(request.getHeader(HttpHeaders.USER_AGENT));
        return userAgent.isMobile() && (userAgentStr.contains("iPhone") || userAgentStr.contains("iPod") || userAgentStr.contains("iPad"));
    }

    public Integer getUserId() {
        return AuthUtils.getUserId();
    }

    public String getUsername() {
        return AuthUtils.getUsername();
    }

    public Integer getUserType(){
        return AuthUtils.getUserType();
    }

    public AuthUser getAuthUser() {
        return AuthUtils.getAuthUser();
    }

    public String getMerchantId() {
        return AuthUtils.getMerchantId();
    }


}
