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
 * 账户表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_account")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 账号
     */
    private String account;

    /**
     * 支付宝UID
     */
    private String uid;

    private String sid;

    /**
     * 密码，随机码
     */
    private String password;

    /**
     * 来源设备
     */
    private String channelCode;

    /**
     * 总额度
     */
    private BigDecimal totalAmount;

    /**
     * 0关闭，1开启
     */
    private Integer status;

    /**
     * websocket 连接状态
     */
    private Integer wsStatus;

    /**
     * 淘宝cookie字符串
     */
    private String cookie;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商品地址
     */
    private String productUrl;

    private String phone;

    private String bankCardNo;

    private String bankName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
