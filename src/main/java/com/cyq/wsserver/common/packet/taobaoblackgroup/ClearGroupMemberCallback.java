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
public class ClearGroupMemberCallback extends WsMessage {

    /**
     * sid : 10de08fcd82791862fd3eccd4c503540
     * groupId : 0_G_2207968155933_1595537989081
     * uid : 2207968155933
     * code : 1
     * account : test
     */

    private String sid;
    private String groupId;
    private String uid;
    private String code;
    private String account;

    public ClearGroupMemberCallback(String action) {
        super(action);
    }

}
