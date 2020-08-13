package com.cyq.wsserver.server.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.constant.PayChannel;
import com.cyq.ppc.constant.YesNoStatus;
import com.cyq.ppc.entity.Account;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.entity.OrderExt;
import com.cyq.ppc.helper.SpringContextHolder;
import com.cyq.ppc.listener.event.MsgLogEvent;
import com.cyq.ppc.service.AccountService;
import com.cyq.ppc.service.OrderExtService;
import com.cyq.ppc.service.OrderService;
import com.cyq.ppc.strategy.ChannelStrategy;
import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandHandler;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.ImSessionContext;
import com.cyq.wsserver.common.packet.WsMessage;
import com.cyq.wsserver.common.packet.dingding.CallbackNtf;
import com.cyq.wsserver.common.packet.dingding.CreateErrorCallback;
import com.cyq.wsserver.common.packet.dingding.CreateSuccessCallback;
import com.cyq.wsserver.server.ImUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@CommandHandler(Command.钉钉红包)
public class DingDingRedHandler implements PacketHandler {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderExtService orderExtService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private Map<PayChannel, ChannelStrategy> channelStrategyMap;

    @Override
    public Command getCommand() {
        return Command.钉钉红包;
    }

    @Override
    public void handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        ImSessionContext imSessionContext = ImUtils.getImSessionContext(channelContext);
        log.info("【{}】消息报文：{}", getCommand(), packet.getBodyStr());
        WsMessage wsMessage = packet.getBodyObj();
        String action = wsMessage.getAction();
        if ("callback".equals(action)) {
            CallbackNtf callbackNtf = JSON.parseObject(packet.getBodyStr(), CallbackNtf.class);
            Order order = orderService.getByOuterOrderIdAndChannelCode(callbackNtf.getOuterOrderId(), callbackNtf.getChannelCode());
            if (Objects.isNull(order)) {
                log.error("外部订单号为【{}】，通道为【{}】的订单不存在！", callbackNtf.getOuterOrderId(), callbackNtf.getChannelCode());
                return;
            }
            order.setOrderStatus(OrderStatus.支付成功.getValue());
            order.setPayTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderService.updateById(order);
        } else if ("upload".equals(action)) {
            CreateSuccessCallback createSuccessCallback = JSON.parseObject(packet.getBodyStr(), CreateSuccessCallback.class);
            Order order = orderService.getByOrderId(createSuccessCallback.getOrderId());
            OrderExt orderExt = orderExtService.getByOrderId(createSuccessCallback.getOrderId());
            if (Objects.isNull(order)) {
                log.error("外部订单号为【{}】，通道为【{}】的订单不存在！", createSuccessCallback.getOuterOrderId(), createSuccessCallback.getChannelCode());
                return;
            }
            order.setOuterOrderId(createSuccessCallback.getOuterOrderId());
            order.setOrderStatus(OrderStatus.待支付.getValue());
            order.setExpireTime(LocalDateTime.now().plusMinutes(5));
            order.setUpdateTime(LocalDateTime.now());
            orderService.updateById(order);
            orderExt.setAndroidPayUrl(createSuccessCallback.getWebPayUrl());
            orderExt.setIosPayUrl(createSuccessCallback.getWebPayUrl());
            orderExtService.updateById(orderExt);
        } else if ("createError".equals(action)) {
            CreateErrorCallback createErrorCallback = JSON.parseObject(packet.getBodyStr(), CreateErrorCallback.class);
            Order order = orderService.getByOrderId(createErrorCallback.getOrderId());
            order.setRemark(createErrorCallback.getCode() + "/" + createErrorCallback.getMsg());
            order.setOrderStatus(OrderStatus.进单失败.getValue());
            order.setUpdateTime(LocalDateTime.now());
            orderService.updateById(order);
            /**
             * 创建红包异常 code:
             * 1000013 你的账号可能存在安全问题，禁止红包发送（死号）
             * 1000086 发红包单日上限（按已发出的个数计算有效，目前100个）
             * 1000060 当前时段发送红包过多
             * 其他异常code暂未遇到，显示msg即可  msg：异常具体原因
             */
            if ("1000013".equals(createErrorCallback.getCode()) || "1000086".equals(createErrorCallback.getCode())) {
                channelStrategyMap.get(PayChannel.钉钉红包).removeAccount(order.getMerchantId());
                String account = channelContext.getBsId().split("#")[0];
                accountService.update(Wrappers.<Account>lambdaUpdate()
                        .set(Account::getStatus, YesNoStatus.NO.getValue())
                        .eq(Account::getAccount, account)
                        .eq(Account::getChannelCode, PayChannel.钉钉红包.getChannelCode()));
            }
        }
        SpringContextHolder.publishEvent(new MsgLogEvent(this, channelContext.getBsId(), packet.getBodyStr()));
    }
}
