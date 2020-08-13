package com.cyq.wsserver.common.packet.dingding;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 收款信息异常通知
 */
@Getter
@Setter
@ToString
public class CreateReq extends WsMessage {
    private BigDecimal money;
    /**
     * 订单号
     */
    private String orderId;

    public CreateReq(){
        this("create");
    }

    public CreateReq(String action) {
        super(action);
    }
}
