package com.cyq.wsserver.common.packet.tbxjhb;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TbxjhbQueryReq extends WsMessage {

    /**
     * hongbaoId : 152381583682524597
     * senderName : 旺仔牛奶
     * sid : 53f3930426adfe7ad36558294ef67780
     * uid : 2521099723
     */
    private String orderId;
    private String hongbaoId;
    private String senderName;
    private String sid;
    private String uid;

    public TbxjhbQueryReq(){
        this("pick");
    }

    public TbxjhbQueryReq(String action){
        super(action);
    }

}
