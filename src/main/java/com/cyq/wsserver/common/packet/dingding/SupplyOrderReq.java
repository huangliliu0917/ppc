package com.cyq.wsserver.common.packet.dingding;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 补单请求
 */
@Getter
@Setter
@ToString
public class SupplyOrderReq extends WsMessage {

    public SupplyOrderReq(){
        this("suppleOrder");
    }

    public SupplyOrderReq(String action) {
        super(action);
    }
}
