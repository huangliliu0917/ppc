package com.cyq.wsserver.server.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyq.ppc.constant.PayChannel;
import com.cyq.ppc.constant.YesNoStatus;
import com.cyq.ppc.entity.Account;
import com.cyq.ppc.helper.SpringContextHolder;
import com.cyq.ppc.listener.event.MsgLogEvent;
import com.cyq.ppc.service.AccountService;
import com.cyq.wsserver.common.Command;
import com.cyq.wsserver.common.CommandHandler;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.ImSessionContext;
import com.cyq.wsserver.common.packet.LoginReq;
import com.cyq.wsserver.server.ImUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

@Slf4j
@Component
@CommandHandler(Command.登录请求)
public class LoginReqHandler implements PacketHandler {
    @Autowired
    private AccountService accountService;

    @Override
    public Command getCommand() {
        return Command.登录请求;
    }

    @Override
    public void handler(ImPacket packet, ChannelContext channelContext) throws Exception {
        ImSessionContext imSessionContext = ImUtils.getImSessionContext(channelContext);
        log.info("【{}】消息报文：{}", getCommand(), packet.getBodyStr());
        LoginReq loginReq = JSON.parseObject(packet.getBodyStr(), LoginReq.class);
        if (PayChannel.SDK支付插件.getChannelCode().equals(loginReq.getChannelCode())) {
            ImUtils.addSdkConnection(channelContext);
        } else if (PayChannel.淘宝小黑群.getChannelCode().equals(loginReq.getChannelCode())) {
            ImUtils.addBlackGroupConnection(channelContext);
        } else if (PayChannel.淘宝小黑群HOOK.getChannelCode().equals(loginReq.getChannelCode())) {
            accountService.update(Wrappers.<Account>lambdaUpdate()
                    .set(Account::getWsStatus, YesNoStatus.YES.getValue())
                    .eq(Account::getAccount, loginReq.getAccount())
                    .eq(Account::getChannelCode, loginReq.getChannelCode()));
        } else if (PayChannel.淘宝现金红包.getChannelCode().equals(loginReq.getChannelCode())) {
            if (StringUtils.isBlank(loginReq.getQnAccount())) {
                if (Integer.parseInt(loginReq.getAccount()) <= 5) {
                    ImUtils.addTbxjhbGenQrConnection(channelContext);
                } else {
                    ImUtils.addTbxjhbConnection(channelContext);
                }
            }
        } else if (PayChannel.淘宝现金红包PLUS.getChannelCode().equals(loginReq.getChannelCode())) {
            ImUtils.addTbxjhbPlusConnection(channelContext);
        }
        String bsId = ImUtils.getBsId(loginReq.getAccount(), loginReq.getChannelCode());
        Tio.unbindBsId(channelContext);
        Tio.bindBsId(channelContext, bsId);
        imSessionContext.setLoginReq(loginReq);
        imSessionContext.setLogin(true);
        SpringContextHolder.publishEvent(new MsgLogEvent(this, channelContext.getBsId(), packet.getBodyStr()));
    }
}
