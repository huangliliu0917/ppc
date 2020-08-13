package com.cyq.wsserver.common;

import com.alibaba.fastjson.JSON;
import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.intf.Packet;
import org.tio.websocket.common.WsResponse;

import java.nio.charset.StandardCharsets;

@Slf4j
@Data
public class ImPacket extends Packet {

    private static final long serialVersionUID = 4910397844111262804L;

    private String bodyStr;

    private WsMessage bodyObj;

    private Command command = null;

    public ImPacket() {

    }

    public ImPacket(Command command) {
        this.setCommand(command);
    }

    public ImPacket(WsMessage bodyObj) {
        this.bodyObj = bodyObj;
    }


    public ImPacket(Command command, String bodyStr) {
        this(command);
        this.bodyStr = bodyStr;
    }


    public WsResponse toWs() {
        return WsResponse.fromText(JSON.toJSONString(this.getBodyObj()), StandardCharsets.UTF_8.name());
    }

}
