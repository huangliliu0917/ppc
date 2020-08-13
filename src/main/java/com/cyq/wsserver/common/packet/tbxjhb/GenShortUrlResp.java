package com.cyq.wsserver.common.packet.tbxjhb;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenShortUrlResp extends WsMessage {
    /**
     * orderId : 888661592050992985
     * url :
     */

    private String orderId;
    private String h5Url;

    public GenShortUrlResp(){
        this("getH5PayCallback");
    }

    public GenShortUrlResp(String action){
        super(action);
    }


}
