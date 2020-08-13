package com.cyq.wsserver.common.packet.taobaoblackgroup;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 小黑群创建请求
 */
@Getter
@Setter
@ToString
public class CreateGroupReq extends WsMessage {
    private String orderId;
    private String uid;
    private String sid;

    public CreateGroupReq(){
        this("create");
    }

    public CreateGroupReq(String action) {
        super(action);
    }

}
