package com.cyq.wsserver.common.packet.unicom;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CastPayUrlReq extends WsMessage {
    private String orderId;
    private String payUrl;

    public CastPayUrlReq(){
        this("castPayUrl");
    }

    public CastPayUrlReq(String action) {
        super(action);
    }
}
