package com.cyq.wsserver.common.packet.dingding;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 收款信息异常通知
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreateErrorCallback extends WsMessage {
    /**
     * uid : 1904937608
     * orderId : 1179728188
     * code : 1000086
     * msg : 今日发红包数已达上限
     * account : 123
     * channelCode : dingdingred
     */

    private String uid;
    private String orderId;
    private String code;
    private String msg;
    private String account;

    public CreateErrorCallback(String action) {
        super(action);
    }
}
