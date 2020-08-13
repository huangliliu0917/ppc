package com.cyq.wsserver.common.packet.taobaoblackgroup;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreateGroupCallback extends WsMessage {

    /**
     * uid : 2207968155933
     * sid : 10de08fcd82791862fd3eccd4c503540
     * groupId : 0_G_2207968155933_1595543418981
     * addGroupUrl : taobao://m.tb.cn/h.VIsH7to
     * sendRedPkgUrl : taobao://m.tb.cn/h.VIsH7to
     * code : 1
     * account : test
     */
    private String orderId;
    private String uid;
    private String sid;
    private String groupId;
    private String addGroupUrl;
    private String sendRedPkgUrl;
    private String code;
    private String account;
    private String error;

    public CreateGroupCallback(String action) {
        super(action);
    }

}
