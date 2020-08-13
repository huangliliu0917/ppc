package com.cyq.wsserver.common.packet.taobaoblackgroup;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QueryLastMsg extends WsMessage {

    /**
     * uid : 2207968155933
     * sid : 10de08fcd82791862fd3eccd4c503540
     * groupId : 0_G_2207968155933_1595537989081
     */

    private String uid;
    private String sid;
    private String groupId;
    private String msg;

    public QueryLastMsg(){
        this("queryLastMsg");
    }

    public QueryLastMsg(String action) {
        super(action);
    }

}
