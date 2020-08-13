package com.cyq.wsserver.common.packet.taobaoblackgrouphook;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClearGroupMemberHook extends WsMessage {
    /**
     * groupId : 0_U_2207968155933#3_2208612341318#3_1_2207968155933#3
     */

    private String groupId;

    public ClearGroupMemberHook(){
        this("clearGroupMemberHook");
    }

    public ClearGroupMemberHook(String action) {
        super(action);
    }

}
