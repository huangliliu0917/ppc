package com.cyq.wsserver.server.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandHandler;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.ImSessionContext;
import com.cyq.wsserver.common.packet.WsMessage;
import com.cyq.wsserver.server.ImUtils;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.helper.SpringContextHolder;
import com.cyq.ppc.listener.event.MsgLogEvent;
import com.cyq.ppc.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@CommandHandler(Command.闲鱼转账)
public class IdleFishHandler implements PacketHandler {

    @Autowired
    private OrderService orderService;

    @Override
    public Command getCommand() {
        return Command.闲鱼转账;
    }

    @Override
    public void handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        ImSessionContext imSessionContext = ImUtils.getImSessionContext(channelContext);
        WsMessage wsMessage = packet.getBodyObj();
        log.info("【{}】消息报文：{}", getCommand(), packet.getBodyStr());
        String action = wsMessage.getAction();
        if ("orderCallback".equals(action)) {
            JSONObject jsonObject = JSON.parseObject(packet.getBodyStr());
            String outerOrderId = jsonObject.getString("outerOrderId");
            String alipayUid = jsonObject.getString("userId");
            BigDecimal amount = jsonObject.getBigDecimal("money");
            List<Order> list = orderService.list(Wrappers.<Order>lambdaQuery()
                    .eq(Order::getUid, alipayUid)
                    .eq(Order::getAmount, amount)
                    .eq(Order::getOrderStatus, OrderStatus.待支付.getValue())
                    .gt(Order::getCreateTime, LocalDateTime.now().minusMinutes(3)));
            if (list.size() == 1) {
                Order order = list.get(0);
                if (!OrderStatus.支付成功.getValue().equals(order.getOrderStatus()) && !OrderStatus.支付失败.getValue().equals(order.getOrderStatus())) {
                    order.setOuterOrderId(outerOrderId);
                    order.setOrderStatus(OrderStatus.支付成功.getValue());
                    orderService.updateById(order);
                }
            }
        }
        SpringContextHolder.publishEvent(new MsgLogEvent(this, channelContext.getBsId(), packet.getBodyStr()));
    }
}
