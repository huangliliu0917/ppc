package com.cyq.wsserver.server.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.service.OrderService;
import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandHandler;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.packet.TransferCardOrderCallback;
import com.cyq.wsserver.common.packet.WsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
@CommandHandler(Command.转银行卡)
public class TransferCardHandler implements PacketHandler {
    @Autowired
    private OrderService orderService;

    @Override
    public Command getCommand() {
        return Command.转银行卡;
    }

    @Override
    public void handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        log.info("【{}】消息报文：{}", getCommand(), packet.getBodyStr());
        WsMessage wsMessage = packet.getBodyObj();
        String action = wsMessage.getAction();
        if ("transferCardOrderCallback".equals(action)) {
            TransferCardOrderCallback orderCallback = JSON.parseObject(packet.getBodyStr(), TransferCardOrderCallback.class);
            Order order = orderService.getOne(Wrappers.<Order>lambdaQuery()
                    .eq(Order::getAmount, orderCallback.getAmount())
                    .eq(Order::getOrderStatus, OrderStatus.待支付.getValue())
                    .eq(Order::getAccount, orderCallback.getPhone())
                    .eq(Order::getArgs, orderCallback.getBankName())
                    .orderByDesc(Order::getId)
                    .last("limit 1"));
            if (Objects.nonNull(order)) {
                order.setOrderStatus(OrderStatus.支付成功.getValue());
                order.setPayTime(LocalDateTime.now());
                orderService.updateById(order);
            }
        }
    }
}
