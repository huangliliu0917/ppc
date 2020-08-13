package com.cyq.ppc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * VIEW
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("v_merchant_account_stat")
public class MerchantAccountStat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 产码账号
     */
    private String account;

    /**
     * 通道
     */
    private String channelCode;

    private BigDecimal yesterdayTradeAmount;

    private BigDecimal yesterdayPaidOrderNum;

    private BigDecimal yesterdayOrderNum;

    private BigDecimal todayTradeAmount;

    private BigDecimal todayPaidOrderNum;

    private BigDecimal todayOrderNum;


}
