package com.cyq.wsserver.common.packet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 转卡成功回调
 */
@Getter
@Setter
@ToString
public class TransferCardOrderCallback extends WsMessage {
    /**
     *   {
     *     "code": "95511",
     *     "moneyStart": "人民币",
     *     "bankName": "平安银行",
     *     "moneyEnd": "元",
     *     "kw": "转入","kw1": "转入"
     *   }
     */
    private String code;
    /**
     * 银行名字
     */
    private String bankName;
    /**
     * 转账金额
     */
    private BigDecimal amount;
    /**
     * 银行卡手机号
     */
    private String phone;
    /**
     * 短信内容
     */
    private String content;


    public TransferCardOrderCallback() {
        this("transferCardOrderCallback");
    }

    public TransferCardOrderCallback(String action) {
        super(action);
    }

}
