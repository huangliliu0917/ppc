package com.cyq.ppc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String merchantId;

    private String merchantName;

    /**
     * 平台订单号
     */
    private String orderId;

    /**
     * 外部订单号
     */
    private String outerOrderId;

    /**
     * 0待产码，1产码失败，2未支付，3支付失败，4支付成功，5补单成功
     */
    private Integer orderStatus;

    /**
     * 订单金额 保留两位小数
     */
    private BigDecimal amount;

    /**
     * 实际付款时间
     */
    private LocalDateTime payTime;

    /**
     * 订单过期时间
     */
    private LocalDateTime expireTime;

    private String expireDesc;

    /**
     * 备注
     */
    private String remark;

    /**
     * 产码账号
     */
    private String account;

    /**
     * 支付宝UID
     */
    private String uid;

    private String groupId;

    private String channelCode;

    /**
     * 商户订单号
     */
    private String merchantOrderId;
    /**
     * 支付成功回调通知地址
     */
    private String notifyUrl;

    /**
     * 同步跳转地址
     */
    private String returnUrl;

    /**
     * 通知状态：0未通知，1通知成功，2通知失败
     */
    private Integer notifyStatus;

    /**
     * 通知次数
     */
    private Integer notifyCount;

    /**
     * 最近一次通知时间
     */
    private LocalDateTime lastNotifyTime;

    /**
     * 回传参数，若值不为空，异步回调时按原样返回
     */
    private String passbackParams;

    /**
     * 代理用户id
     */
    private Integer agentId;

    /**
     * 额外参数
     */
    private String args;

    /**
     * 链接
     */
    private String link;

    /**
     * 补单时间
     */
    private LocalDateTime supplyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
