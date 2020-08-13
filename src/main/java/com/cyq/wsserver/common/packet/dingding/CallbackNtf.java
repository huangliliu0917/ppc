package com.cyq.wsserver.common.packet.dingding;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 回调通知
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CallbackNtf extends WsMessage {
    /**
     * outerOrderId : 3i7eXb5X_1904937608100
     * uid : 1904937608
     * money : 0.01
     * account : 123
     * channelCode : dingdingred
     */
    private String outerOrderId;
    private String uid;
    private BigDecimal money;
    private String account;

    public CallbackNtf(String action) {
        super(action);
    }

}
