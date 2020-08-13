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
@TableName("v_merchant_order_stat")
public class MerchantOrderStat implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer agentId;

    /**
     * 总的成功率
     */
    private BigDecimal totalSuccessRate;
    /**
     * 本月成功率
     */
    private BigDecimal monthSuccessRate;
    /**
     * 昨日成功率
     */
    private BigDecimal yesterdaySuccessRate;
    /**
     * 今日成功率
     */
    private BigDecimal todaySuccessRate;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户名
     */
    private String merchantName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 总交易金额
     */
    private BigDecimal totalTradeAmount;
    /**
     * 成功订单数量
     */
    private BigDecimal totalPaidOrderNum;
    /**
     * 总订单数
     */
    private Long totalOrderNum;
    /**
     * 本月交易金额
     */
    private BigDecimal monthTradeAmount;
    /**
     * 本月成功订单数
     */
    private BigDecimal monthPaidOrderNum;
    /**
     * 本月订单数
     */
    private BigDecimal monthOrderNum;
    /**
     * 昨日交易金额
     */
    private BigDecimal yesterdayTradeAmount;
    /**
     * 昨日成功笔数
     */
    private BigDecimal yesterdayPaidOrderNum;
    /**
     * 昨日订单数
     */
    private BigDecimal yesterdayOrderNum;
    /**
     * 今日交易金额
     */
    private BigDecimal todayTradeAmount;
    /**
     * 今日成功笔数
     */
    private BigDecimal todayPaidOrderNum;
    /**
     * 今日订单总笔数
     */
    private BigDecimal todayOrderNum;


}
