package com.cyq.wsserver.server;

import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandStat;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.ImSessionContext;
import com.cyq.wsserver.server.handler.PacketHandler;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class PacketDispatcher {

    public static Map<Command, PacketHandler> handlerMap = new TreeMap<>();

    /**
     * 分发消息到处理者
     *
     * @param packet
     * @throws Exception
     */
    public static void dispatch(ImPacket packet, ChannelContext channelContext) throws Exception {
        Command command = packet.getCommand();
        ImSessionContext imSessionContext = ImUtils.getImSessionContext(channelContext);
        // 检查登录情况
        if (!imSessionContext.isLogin() && command != Command.心跳请求) {
            if (command != Command.登录请求) {
                log.warn("{} 第一个业务包必须为 登录请求包，本次命令:{}", channelContext.toString(), command);
                //Tio.remove(channelContext, "第一个业务包必须为 登录请求包");
                //return;
            }
        }
        PacketHandler handler = handlerMap.get(command);
        if (handler != null) {
            handler.handler(packet, channelContext);
            CommandStat.getCount(command).handled.incrementAndGet();
        } else {
            log.warn("命令码[{}]没有对应的处理类", command);
            CommandStat.getCount(command).handled.incrementAndGet();
        }
    }

}
