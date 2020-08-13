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
@TableName("t_order_ext")
public class OrderExt implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单号
     */
    private String orderId;

    private String sdkPayUrl;

    private String aliAppPay;

    private String aliWebPay;

    private String androidPayUrl;

    private String iosPayUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
