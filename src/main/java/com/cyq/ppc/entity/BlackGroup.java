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
 * 淘宝小黑裙用户信息
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_black_group")
public class BlackGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String account;

    private String uid;

    private String sid;

    /**
     * 群ID
     */
    private String groupId;

    /**
     * 入群链接
     */
    private String addGroupUrl;

    /**
     * 发送红包链接
     */
    private String sendRedPkgUrl;

    /**
     * 群状态：0空闲，1已配单，2 已进群，3红包已领取
     */
    private Integer status;

    /**
     * 上一单订单号
     */
    private String lastOrderId;

    /**
     * 上一次配单时间 lastMatchTime
     */
    private LocalDateTime lastMatchTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
