package com.cyq.wsserver.server.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.constant.PayChannel;
import com.cyq.ppc.constant.YesNoStatus;
import com.cyq.ppc.entity.Account;
import com.cyq.ppc.entity.Envelope;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.entity.OrderExt;
import com.cyq.ppc.helper.SpringContextHolder;
import com.cyq.ppc.listener.event.MsgLogEvent;
import com.cyq.ppc.service.AccountService;
import com.cyq.ppc.service.EnvelopeService;
import com.cyq.ppc.service.OrderExtService;
import com.cyq.ppc.service.OrderService;
import com.cyq.ppc.strategy.ChannelStrategy;
import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandHandler;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.ImSessionContext;
import com.cyq.wsserver.common.packet.WsMessage;
import com.cyq.wsserver.common.packet.tbxjhb.GenShortUrlResp;
import com.cyq.wsserver.common.packet.tbxjhb.QianNiuRpUpload;
import com.cyq.wsserver.common.packet.tbxjhb.TbxjhbCallback;
import com.cyq.wsserver.common.packet.tbxjhb.TbxjhbUploadResp;
import com.cyq.wsserver.server.ImUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@CommandHandler(Command.淘宝现金红包)
public class TbxjhbHandler implements PacketHandler {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderExtService orderExtService;
    @Autowired
    private EnvelopeService envelopeService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private Map<PayChannel, ChannelStrategy> channelStrategyMap;

    private Cache<String, Account> accountMap = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .concurrencyLevel(10) // 设置并发级别为10
            .build();

    @Override
    public Command getCommand() {
        return Command.淘宝现金红包;
    }

    @Override
    public void handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        ImSessionContext imSessionContext = ImUtils.getImSessionContext(channelContext);
        log.info("【{}】消息报文：{}", getCommand(), packet.getBodyStr());
        WsMessage wsMessage = packet.getBodyObj();
        String action = wsMessage.getAction();
        if ("upload".equals(action)) {
            TbxjhbUploadResp tbxjhbUploadResp = JSON.parseObject(packet.getBodyStr(), TbxjhbUploadResp.class);
            if (StringUtils.isBlank(tbxjhbUploadResp.getOrderId())) {
                if (StringUtils.isNotBlank(tbxjhbUploadResp.getError())) {
                    log.error(tbxjhbUploadResp.getError());
                    if (tbxjhbUploadResp.getError().contains("过期")) {
                        accountService.update(Wrappers.<Account>lambdaUpdate()
                                .set(Account::getStatus, YesNoStatus.NO.getValue())
                                .eq(Account::getAccount, tbxjhbUploadResp.getSenderName())
                                .eq(Account::getChannelCode, tbxjhbUploadResp.getChannelCode()));
                    }
                    return;
                }
                Envelope envelope = new Envelope();
                envelope.setAccount(tbxjhbUploadResp.getSenderName());
                envelope.setEnvelopeId(tbxjhbUploadResp.getHongbaoId());
                envelope.setUid(tbxjhbUploadResp.getUid());
                envelope.setAmount(tbxjhbUploadResp.getAmount());
                envelope.setTklUrl(tbxjhbUploadResp.getTklUrl());
                envelope.setSdkUrl(tbxjhbUploadResp.getSdkUrl());
                envelopeService.save(envelope);
            } else {
                Order order = orderService.getByOrderId(tbxjhbUploadResp.getOrderId());
                OrderExt orderExt = orderExtService.getByOrderId(tbxjhbUploadResp.getOrderId());
                order.setOrderStatus(OrderStatus.待支付.getValue());
                if (StringUtils.isNotBlank(tbxjhbUploadResp.getError())) {
                    order.setOrderStatus(OrderStatus.进单失败.getValue());
                    order.setRemark(tbxjhbUploadResp.getError());
                    log.error(tbxjhbUploadResp.getError());
                }
                order.setUpdateTime(LocalDateTime.now());
                order.setOuterOrderId(tbxjhbUploadResp.getOutOrderId());
                orderService.updateById(order);
                orderExt.setSdkPayUrl(tbxjhbUploadResp.getTklUrl());
                orderExtService.updateById(orderExt);
            }
        } else if ("callback".equals(wsMessage.getAction())) {
            TbxjhbCallback tbxjhbCallback = JSON.parseObject(packet.getBodyStr(), TbxjhbCallback.class);
            Order order = orderService.getByOrderId(tbxjhbCallback.getOrderId());
            if (Objects.nonNull(order)) {
                if (tbxjhbCallback.getStatus() == 2) {
                    order.setOrderStatus(OrderStatus.支付成功.getValue());
                    order.setPayTime(LocalDateTime.now());
                    orderService.updateById(order);
                } else if (tbxjhbCallback.getError().contains("过期")){
                    channelStrategyMap.get(PayChannel.淘宝现金红包).removeAccount(order.getMerchantId());
                    order.setRemark("淘宝cookie已过期");
                    orderService.updateById(order);
                }
            }
        } else if ("qianNiuRpUpload".equals(wsMessage.getAction())) {
            QianNiuRpUpload qianNiuRpUpload = JSON.parseObject(packet.getBodyStr(), QianNiuRpUpload.class);
            Account account = accountMap.getIfPresent(qianNiuRpUpload.getSenderName());
            if (Objects.isNull(account)) {
                account = accountService.getOne(Wrappers.<Account>lambdaQuery()
                        .eq(Account::getAccount, qianNiuRpUpload.getSenderName())
                        .eq(Account::getChannelCode, PayChannel.淘宝现金红包.getChannelCode()));
            }
            Envelope envelope = new Envelope();
            envelope.setAccount(qianNiuRpUpload.getSenderName());
            envelope.setUid(account.getUid());
            envelope.setEnvelopeId(qianNiuRpUpload.getHongbaoId());
            envelope.setAmount(qianNiuRpUpload.getAmount());
            envelope.setSdkUrl(qianNiuRpUpload.getSdkUrl());
            envelopeService.save(envelope);
        } else if ("getH5PayCallback".equals(wsMessage.getAction())) {
            GenShortUrlResp genShortUrlResp = JSON.parseObject(packet.getBodyStr(), GenShortUrlResp.class);
            Order order = orderService.getByOrderId(genShortUrlResp.getOrderId());
            order.setOrderStatus(OrderStatus.待支付.getValue());
            order.setExpireTime(LocalDateTime.now().plusMinutes(3));
            order.setLink(genShortUrlResp.getH5Url());
            orderService.updateById(order);
        }
        SpringContextHolder.publishEvent(new MsgLogEvent(this, channelContext.getBsId(), packet.getBodyStr()));
    }
}
