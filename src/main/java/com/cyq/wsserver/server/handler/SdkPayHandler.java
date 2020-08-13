package com.cyq.wsserver.server.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.entity.OrderExt;
import com.cyq.ppc.helper.SpringContextHolder;
import com.cyq.ppc.listener.event.MsgLogEvent;
import com.cyq.ppc.service.OrderExtService;
import com.cyq.ppc.service.OrderService;
import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandHandler;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.ImSessionContext;
import com.cyq.wsserver.common.packet.WsMessage;
import com.cyq.wsserver.common.packet.unicom.CastPayUrlCallbackNtf;
import com.cyq.wsserver.server.ImUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

@Slf4j
@Component
@CommandHandler(Command.SDK支付插件)
public class SdkPayHandler implements PacketHandler {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderExtService orderExtService;

    @Override
    public Command getCommand() {
        return Command.SDK支付插件;
    }

    @Override
    public void handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        ImSessionContext imSessionContext = ImUtils.getImSessionContext(channelContext);
        log.info("【{}】消息报文：{}", getCommand(), packet.getBodyStr());
        WsMessage wsMessage = packet.getBodyObj();
        String action = wsMessage.getAction();
        if ("castPayUrlCallback".equals(action)) {
            CastPayUrlCallbackNtf castPayUrlCallbackNtf = JSON.parseObject(packet.getBodyStr(), CastPayUrlCallbackNtf.class);
            OrderExt orderExt = orderExtService.getByOrderId(castPayUrlCallbackNtf.getOrderId());
            orderExt.setAliAppPay(castPayUrlCallbackNtf.getAliAppPay());
            orderExt.setAliWebPay(castPayUrlCallbackNtf.getAliWebPay());
            orderExtService.updateById(orderExt);
            orderService.update(Wrappers.<Order>lambdaUpdate()
                    .set(Order::getOrderStatus, OrderStatus.待支付.getValue())
                    .eq(Order::getOrderId, castPayUrlCallbackNtf.getOrderId()));
        }
        SpringContextHolder.publishEvent(new MsgLogEvent(this, channelContext.getBsId(), packet.getBodyStr()));
    }
}
