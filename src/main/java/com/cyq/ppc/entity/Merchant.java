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
 * 商户信息表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_merchant")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 归属用户id
     */
    private Integer userId;

    /**
     * 代理用户ID
     */
    private Integer agentId;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户名字
     */
    private String merchantName;

    /**
     * 秘钥
     */
    private String secretKey;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 资金密码（暂时不用）
     */
    private String payPassword;

    /**
     * 状态（1-正常，2-锁定，3-删除）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
