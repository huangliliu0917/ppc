package com.cyq.wsserver.server.handler;

import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandHandler;
import com.cyq.wsserver.common.ImPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

@Slf4j
@Component
@CommandHandler(Command.心跳请求)
public class HeartbeatReqHandler implements PacketHandler {

    @Override
    public Command getCommand() {
        return Command.心跳请求;
    }

    @Override
    public void handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        // 心跳包是不需要做任何业务处理的
        //log.info("【{}】来自客户端BsId[{}]的心跳请求，不做处理...", getCommand(), channelContext.getBsId());
    }
}
