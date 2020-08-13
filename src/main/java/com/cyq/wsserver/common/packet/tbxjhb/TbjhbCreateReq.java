package com.cyq.wsserver.common.packet.tbxjhb;


import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TbjhbCreateReq extends WsMessage {
    /**
     * hongbao_id : 888661592050992985
     * amount : 1
     * sid : 53f3930426adfe7ad36558294ef67780
     * uid : 2521099723
     * senderName : 旺仔牛奶
     */
    private String orderId;
    private String hongbaoId;
    private int amount;
    private String sid;
    private String uid;
    private String senderName;
    private int num;

    public TbjhbCreateReq(){
        this("create");
    }

    public TbjhbCreateReq(String action){
        super(action);
    }

}
