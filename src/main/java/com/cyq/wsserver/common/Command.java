package com.cyq.wsserver.common;

import com.cyq.ppc.constant.ChannelCodeConst;
import lombok.Getter;

import java.util.Objects;

/**
 * 以通道来创建处理器
 */
@Getter
public enum Command {
    心跳请求("ping"),
    登录请求("login"),
    钉钉红包(ChannelCodeConst.钉钉红包),
    闲鱼转账(ChannelCodeConst.闲鱼转账),
    SDK支付插件(ChannelCodeConst.SDK支付插件),
    上海交通卡(ChannelCodeConst.上海交通卡),
    联通卡密(ChannelCodeConst.联通卡密),
    话费直充(ChannelCodeConst.话费直充),
    淘宝小黑群(ChannelCodeConst.淘宝小黑群),
    淘宝小黑群HOOK(ChannelCodeConst.淘宝小黑群HOOK),
    淘宝现金红包(ChannelCodeConst.淘宝现金红包),
    淘宝现金红包PLUS(ChannelCodeConst.淘宝现金红包PLUS),
    转银行卡(ChannelCodeConst.转银行卡),


    WS消息测试("999");

    public static Command from(String action) {
        Command[] values = Command.values();
        for (Command v : values) {
            if (Objects.equals(v.action, action)) {
                return v;
            }
        }
        return null;
    }

    private String action;

    Command(String action) {
        this.action = action;
    }


}
