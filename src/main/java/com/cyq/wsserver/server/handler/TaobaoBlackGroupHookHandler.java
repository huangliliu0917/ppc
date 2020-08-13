package com.cyq.wsserver.server.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.constant.PayChannel;
import com.cyq.ppc.entity.BlackGroup;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.helper.RedisHelper;
import com.cyq.ppc.helper.SpringContextHolder;
import com.cyq.ppc.listener.event.MsgLogEvent;
import com.cyq.ppc.service.AccountService;
import com.cyq.ppc.service.BlackGroupService;
import com.cyq.ppc.service.OrderService;
import com.cyq.ppc.strategy.ChannelStrategy;
import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandHandler;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.packet.WsMessage;
import com.cyq.wsserver.common.packet.taobaoblackgroup.BlackGroupStatus;
import com.cyq.wsserver.common.packet.taobaoblackgrouphook.ClearGroupMemberCallbackHook;
import com.cyq.wsserver.common.packet.taobaoblackgrouphook.CreateGroupCallbackHook;
import com.cyq.wsserver.common.packet.taobaoblackgrouphook.OrderCallbackHook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@CommandHandler(Command.淘宝小黑群HOOK)
public class TaobaoBlackGroupHookHandler implements PacketHandler {
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
        return Command.淘宝小黑群HOOK;
    }

    @Override
    public void handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        log.info("【{}】消息报文：{}", getCommand(), packet.getBodyStr());
        WsMessage wsMessage = packet.getBodyObj();
        String action = wsMessage.getAction();
        if ("createGroupCallbackHook".equals(action)) {
            // 创建小黑群结果回调
            CreateGroupCallbackHook createGroupCallbackHook = JSON.parseObject(packet.getBodyStr(), CreateGroupCallbackHook.class);
            BlackGroup blackGroup = blackGroupService.getByGroupId(createGroupCallbackHook.getGroupId());
            if (Objects.isNull(blackGroup)) {
                List<BlackGroup> list = blackGroupService.list(Wrappers.<BlackGroup>lambdaQuery()
                        .eq(BlackGroup::getAccount, createGroupCallbackHook.getAccount())
                        .orderByAsc(BlackGroup::getId));
                if (list.size() >= 3) {
                    blackGroupService.removeById(list.get(0).getId());
                }
                blackGroup = new BlackGroup();
                blackGroup.setStatus(BlackGroupStatus.空闲.getValue());
                // "https://market.m.taobao.com/apps/market/groupbase/saohuo.html?wh_weex=true&groupId="+ blackGroup.getGroupId()
                String groupId = createGroupCallbackHook.getGroupId().split("#")[0] + "_" + createGroupCallbackHook.getGroupId().split("#")[1].split("_")[1];
                blackGroup.setGroupId(createGroupCallbackHook.getGroupId());
                blackGroup.setAccount(createGroupCallbackHook.getAccount());
                blackGroup.setAddGroupUrl("https://market.m.taobao.com/apps/market/groupbase/saohuo.html?wh_weex=true&groupId=" + groupId);
                blackGroupService.save(blackGroup);
            }
        } else if ("orderCallbackHook".equals(action)) {
            // 红包领取成功回调
            OrderCallbackHook orderCallbackHook = JSON.parseObject(packet.getBodyStr(), OrderCallbackHook.class);
            if (RedisHelper.hasKey(orderCallbackHook.getHongbaoId())) {
                return;
            }
            Order order = orderService.getOne(Wrappers.<Order>lambdaQuery()
                    .eq(Order::getGroupId, orderCallbackHook.getGroupId())
                    .eq(Order::getOrderStatus, OrderStatus.待支付.getValue())
                    .orderByDesc(Order::getId)
                    .last("limit 1"));
            if (Objects.nonNull(order)) {
                if (String.valueOf(order.getAmount().multiply(BigDecimal.valueOf(100)).intValue()).equals(orderCallbackHook.getAmount())) {
                    order.setOrderStatus(OrderStatus.支付成功.getValue());
                    order.setPayTime(LocalDateTime.now());
                    order.setOuterOrderId(orderCallbackHook.getHongbaoId());
                    orderService.updateById(order);
                    RedisHelper.setEx(orderCallbackHook.getHongbaoId(), "1", 3, TimeUnit.MINUTES);
                }
                blackGroupService.update(Wrappers.<BlackGroup>lambdaUpdate()
                        .set(BlackGroup::getStatus, BlackGroupStatus.空闲.getValue())
                        .eq(BlackGroup::getAccount, orderCallbackHook.getAccount())
                        .eq(BlackGroup::getGroupId, orderCallbackHook.getGroupId()));
            }
        } else if ("clearGroupMemberCallbackHook".equals(action)) {
            // 红包领后自动踢用户出群，更新群状态为空闲
            ClearGroupMemberCallbackHook clearGroupMemberCallbackHook = JSON.parseObject(packet.getBodyStr(), ClearGroupMemberCallbackHook.class);
            blackGroupService.update(Wrappers.<BlackGroup>lambdaUpdate()
                    .set(BlackGroup::getStatus, BlackGroupStatus.空闲.getValue())
                    .eq(BlackGroup::getAccount, clearGroupMemberCallbackHook.getAccount())
                    .eq(BlackGroup::getGroupId, clearGroupMemberCallbackHook.getGroupId()));
        }
        SpringContextHolder.publishEvent(new MsgLogEvent(this, channelContext.getBsId(), packet.getBodyStr()));
    }

}
