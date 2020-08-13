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
 * 工具包
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_toolkit")
public class Toolkit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名字
     */
    private String name;

    private String code;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 0禁用，1启用
     */
    private Integer status;

    /**
     * 添加时间
     */
    private LocalDateTime createTime;


}
