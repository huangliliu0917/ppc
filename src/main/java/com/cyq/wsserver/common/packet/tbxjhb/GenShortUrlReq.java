package com.cyq.wsserver.common.packet.tbxjhb;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenShortUrlReq extends WsMessage {
    /**
     * orderId : 888661592050992985
     * url :
     */

    private String orderId;
    private String sdkUrl;

    public GenShortUrlReq(){
        this("getH5PayUrl");
    }

    public GenShortUrlReq(String action){
        super(action);
    }


}
