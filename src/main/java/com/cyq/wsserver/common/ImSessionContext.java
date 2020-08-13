package com.cyq.wsserver.common;

import com.cyq.wsserver.common.packet.LoginReq;
import com.cyq.ppc.entity.IpInfo;
import lombok.Data;
import org.tio.websocket.common.WsSessionContext;

/**
 * IM 会话上下文
 */
@Data
public class ImSessionContext {
    /**
     * 唯一标识
     */
    private String uniqid;
    /**
     * 如果是ws，则有此对象
     */
    private WsSessionContext wsSessionContext;
    /**
     * 客户端 IP 信息
     */
    private IpInfo ipInfo;
    /**
     * 是否登录
     */
    private boolean isLogin;
    /**
     * 登录请求包
     */
    private LoginReq loginReq;
}
