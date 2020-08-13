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
public class OrderCallback extends WsMessage {

    /**
     * uid : 2207968155933
     * sid : 10de08fcd82791862fd3eccd4c503540
     * groupId : 0_G_2207968155933_1595537989081
     * hongbao_id : 171991595541491000
     * from : 1711098475
     * status : 2
     * amount : 10
     * account : test
     */

    private String uid;
    private String sid;
    private String groupId;
    private String hongbaoId;
    private String from;
    private int status;
    private String amount;
    private String account;

    public OrderCallback(String action) {
        super(action);
    }

}
