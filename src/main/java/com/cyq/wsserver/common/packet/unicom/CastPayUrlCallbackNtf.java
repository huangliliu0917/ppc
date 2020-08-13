package com.cyq.wsserver.common.packet.unicom;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CastPayUrlCallbackNtf extends WsMessage {
    /**
     * msg : success
     * aliAppPay : alipays://platformapi/startApp?appId=20000125&orderSuffix=h5_route_token%3D%22RZ55aOgBf8bM7zvoTQcGKZakiCREnsmobilecashierRZ55%22%26is_h5_route%3D%22true%22%23Intent%3Bscheme%3Dalipays%3Bpackage%3Dcom.eg.android.AlipayGphone%3Bend
     * orderId : test
     * account : 1
     * aliWebPay : https://mclient.alipay.com/cashierRoutePay.htm?route_pay_from=h5&init_from=SDKLite&session=RZ55aOgBf8bM7zvoTQcGKZakiCREnsmobilecashierRZ55&utdid=BJP/ueY0YUMDAIqK0X2Kyf5K&tid=928fc0198de551bd4fda9a4d7798fe396082ddfb907caa3b0de13f97effd5c0a&ltAndroidSdkLoadingVersion=Y&cc=y
     * code : 0
     */
    private String msg;
    private String aliAppPay;
    private String orderId;
    private String account;
    private String aliWebPay;
    private String code;

    public CastPayUrlCallbackNtf(String action) {
        super(action);
    }
}
