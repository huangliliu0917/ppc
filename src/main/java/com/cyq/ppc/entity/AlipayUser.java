package com.cyq.ppc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_alipay_user")
public class AlipayUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 支付宝账户
     */
    private String username;

    /**
     * 实名手机号
     */
    private String phone;

    /**
     * 支付宝UID
     */
    private String alipayUid;

    /**
     * 是否可用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
