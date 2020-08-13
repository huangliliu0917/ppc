package com.cyq.wsserver.common.packet.taobaoblackgrouphook;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderCallbackHook extends WsMessage {
    /**
     * amount : 10
     * status : 2
     * groupId : 0_G_2207968155933#3_1595598581625_0_2207968155933#3
     * account : 2207968155933
     * hongbaoId : 144151597010623000
     */

    private String amount;
    private int status;
    private String groupId;
    private String account;
    private String hongbaoId;

    public OrderCallbackHook(){
        this("orderCallbackHook");
    }

    public OrderCallbackHook(String action) {
        super(action);
    }

}
