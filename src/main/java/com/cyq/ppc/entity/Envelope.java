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
 * 红包信息
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_envelope")
public class Envelope implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 红包所属账号
     */
    private String account;

    /**
     * 淘宝uid
     */
    private String uid;

    private Integer amount;

    /**
     * 红包ID
     */
    private String envelopeId;

    /**
     * 淘口令URL
     */
    private String tklUrl;

    private String sdkUrl;

    /**
     * 红包使用状态：0未使用，1已使用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
