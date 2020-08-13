package com.cyq.wsserver.common.packet.tbxjhb;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: lill
 * @create: 2020-07-28 23:55
 */
@Getter
@Setter
@NoArgsConstructor
public class TbxjhbCallback extends WsMessage {

    /**
     * hongbao_id : 152381583682524597
     * senderName : cntaobao%E6%81%AD%E5%
     * sid : 53f3930426adfe7ad36558294ef67780
     * uid : 2521099723
     * status : 2
     * account : 123
     */
    private String orderId;
    private String hongbaoId;
    private String senderName;
    private String sid;
    private String uid;
    private int status;
    private String account;
    private String error;

}
