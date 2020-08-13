package com.cyq.ppc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户的 ip 信息
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_ip_info")
public class IpInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * IP
     */
    private String ip;
    /**
     * IP 字符串的长整型
     */
    private Long ipLong;

    /**
     * 国家
     */
    private String country;
    /**
     * 国家编码
     */
    private String countryCode;

    /**
     * 省份
     */
    private String province;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市
     */
    private String city;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 因特网服务提供商
     */
    private String isp;
    /**
     * 因特网服务提供商编码
     */
    private String ispCode;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
