package com.cyq.wsserver.common.packet.taobaoblackgrouphook;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClearGroupMemberCallbackHook extends WsMessage {
    /**
     * groupId : 0_G_2207968155933#3_1595598581625_0_2207968155933#3
     * account : 2207968155933
     */

    private String groupId;
    private String account;

    public ClearGroupMemberCallbackHook(){
        this("clearGroupMemberCallbackHook");
    }

    public ClearGroupMemberCallbackHook(String action) {
        super(action);
    }

}
