package com.cyq.wsserver.common;

import org.tio.utils.time.Time;

public interface WsConsts {

    String WS_TOKEN_NAME = "uid";
    /**
     * 服务绑定的 IP 地址，默认不绑定
     */
    String ip = null;
    /**
     * 服务绑定的APP端口
     */
    int wsPort = 18181;
    /**
     * 心跳超时时间，超时会自动关闭连接
     */
    //long heartbeatTimeout = 30 * 1000;
    long heartbeatTimeout = 0;
    /**
     * 添加监控时段，不要添加过多的时间段，因为每个时间段都要消耗一份内存，一般加一个时间段就可以了
     */
    Long[] ipStatDurations = {Time.MINUTE_1};

    String IM_SESSION_CONTEXT_KEY = "w8Ir5Pxvl1Eqsk5D";

}
