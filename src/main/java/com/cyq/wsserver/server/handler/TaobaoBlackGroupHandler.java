package com.cyq.wsserver.server.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandHandler;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.packet.WsMessage;
import com.cyq.wsserver.common.packet.taobaoblackgroup.*;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.constant.PayChannel;
import com.cyq.ppc.constant.YesNoStatus;
import com.cyq.ppc.entity.Account;
import com.cyq.ppc.entity.BlackGroup;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.helper.RedisHelper;
import com.cyq.ppc.helper.SpringContextHolder;
import com.cyq.ppc.listener.event.MsgLogEvent;
import com.cyq.ppc.service.AccountService;
import com.cyq.ppc.service.BlackGroupService;
import com.cyq.ppc.service.OrderService;
import com.cyq.ppc.strategy.ChannelStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@CommandHandler(Command.淘宝小黑群)
public class TaobaoBlackGroupHandler implements PacketHandler {
    @Autowired
    private OrderService orderService;
    @Autowired
    private BlackGroupService blackGroupService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private Map<PayChannel, ChannelStrategy> channelStrategyMap;
    @Override
    public Command getCommand() {
        return Command.淘宝小黑群;
    }

    @Override
    public void handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        log.info("【{}】消息报文：{}", getCommand(), packet.getBodyStr());
        WsMessage wsMessage = packet.getBodyObj();
        String action = wsMessage.getAction();
        if ("createGroupCallback".equals(action)) {
            // 创建小黑群结果回调
            CreateGroupCallback createGroupCallback = JSON.parseObject(packet.getBodyStr(), CreateGroupCallback.class);
            if ("0".equals(createGroupCallback.getCode()) || packet.getBodyStr().contains("最多创建10次群")) {
                Order order = orderService.getByOrderId(createGroupCallback.getOrderId());
                order.setOrderStatus(OrderStatus.进单失败.getValue());
                order.setUpdateTime(LocalDateTime.now());
                order.setRemark(createGroupCallback.getError());
                orderService.updateById(order);
                if (packet.getBodyStr().contains("Session过期")) {
                    boolean update = accountService.update(Wrappers.<Account>lambdaUpdate().
                            set(Account::getStatus, YesNoStatus.NO.getValue())
                            .eq(Account::getChannelCode, PayChannel.淘宝小黑群.getChannelCode())
                            .eq(Account::getAccount, order.getAccount()));
                    channelStrategyMap.get(PayChannel.淘宝小黑群).removeAccount(order.getMerchantId());
                }
            } else {
                Order order = orderService.getByOrderId(createGroupCallback.getOrderId());
                order.setGroupId(createGroupCallback.getGroupId());
                order.setOrderStatus(OrderStatus.待支付.getValue());
                order.setExpireTime(LocalDateTime.now().plusMinutes(3));
                order.setExpireDesc("3分钟");
                orderService.updateById(order);
                BlackGroup blackGroup = new BlackGroup();
                BeanUtils.copyProperties(createGroupCallback, blackGroup);
                blackGroup.setAccount(null);
                blackGroup.setStatus(BlackGroupStatus.已配单.getValue());
                blackGroup.setLastOrderId(order.getOrderId());
                blackGroup.setLastMatchTime(LocalDateTime.now());
                blackGroupService.save(blackGroup);
            }
        } else if ("addGroupCallback".equals(action)) {
            // 用户加群成功回调
            JoinGroupCallback joinGroupCallback = JSON.parseObject(packet.getBodyStr(), JoinGroupCallback.class);
            blackGroupService.updateStatus(joinGroupCallback.getGroupId(), BlackGroupStatus.已进群.getValue());
        } else if ("orderCallback".equals(action)) {
            // 红包领取成功回调
            OrderCallback orderCallback = JSON.parseObject(packet.getBodyStr(), OrderCallback.class);
            if (RedisHelper.hasKey(orderCallback.getHongbaoId())) {
                return;
            }
            Order order = orderService.getOne(Wrappers.<Order>lambdaQuery()
                    .eq(Order::getGroupId, orderCallback.getGroupId())
                    .orderByDesc(Order::getId)
                    .last("limit 1"));
            if (Objects.nonNull(order)) {
                if (String.valueOf(order.getAmount().multiply(BigDecimal.valueOf(100)).intValue()).equals(orderCallback.getAmount())) {
                    order.setOrderStatus(OrderStatus.支付成功.getValue());
                    order.setPayTime(LocalDateTime.now());
                    order.setOuterOrderId(orderCallback.getHongbaoId());
                    orderService.updateById(order);
                    RedisHelper.setEx(orderCallback.getHongbaoId(), "1", 3, TimeUnit.MINUTES);
                }
                blackGroupService.update(Wrappers.<BlackGroup>lambdaUpdate()
                        .set(BlackGroup::getStatus, BlackGroupStatus.空闲.getValue())
                        .eq(BlackGroup::getGroupId, orderCallback.getGroupId()));
            }
        } else if ("clearGroupMemberCallback".equals(action)) {
            // 红包领后自动踢用户出群，更新群状态为空闲
            ClearGroupMemberCallback clearGroupMemberCallback = JSON.parseObject(packet.getBodyStr(), ClearGroupMemberCallback.class);
            blackGroupService.update(Wrappers.<BlackGroup>lambdaUpdate()
                    .set(BlackGroup::getStatus, BlackGroupStatus.空闲.getValue())
                    .eq(BlackGroup::getGroupId, clearGroupMemberCallback.getGroupId()));
        }
        SpringContextHolder.publishEvent(new MsgLogEvent(this, channelContext.getBsId(), packet.getBodyStr()));
    }

}
