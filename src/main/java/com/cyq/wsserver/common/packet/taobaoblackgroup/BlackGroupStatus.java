package com.cyq.wsserver.common.packet.taobaoblackgroup;


import lombok.Getter;

/**
 * 群状态：0 空闲，1 已配单，2 已进群，3 红包已领取
 */
@Getter
public enum  BlackGroupStatus {
    空闲(0),
    已配单(1),
    已进群(2)

    ;

    private Integer value;

    BlackGroupStatus(Integer value) {
        this.value = value;
    }

}
