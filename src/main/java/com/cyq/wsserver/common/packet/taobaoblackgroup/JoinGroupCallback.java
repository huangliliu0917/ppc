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
public class JoinGroupCallback extends WsMessage {

    /**
     * uid : 2207968155933
     * sid : 10de08fcd82791862fd3eccd4c503540
     * groupId : 0_G_2207968155933_1595537989081
     * sendTime : 1595538124544
     * code : 1
     * account : test
     */

    private String uid;
    private String sid;
    private String groupId;
    private String sendTime;
    private String code;
    private String account;

    public JoinGroupCallback(String action) {
        super(action);
    }

}
