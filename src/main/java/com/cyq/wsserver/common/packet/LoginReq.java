package com.cyq.wsserver.common.packet;

import cn.hutool.core.util.IdUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 登录请求参数
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoginReq extends WsMessage {

    private String account = IdUtil.objectId();

    private String qnAccount;

    public LoginReq(String action) {
        super(action);
    }

}
