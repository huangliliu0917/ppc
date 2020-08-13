package com.cyq.wsserver.server.handler;

import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.ImPacket;
import org.tio.core.ChannelContext;

public interface PacketHandler {

    Command getCommand();

    void handler(ImPacket packet, ChannelContext channelContext) throws Exception;

}
