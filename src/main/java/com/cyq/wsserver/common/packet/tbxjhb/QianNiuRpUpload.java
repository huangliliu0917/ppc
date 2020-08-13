package com.cyq.wsserver.common.packet.tbxjhb;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QianNiuRpUpload extends WsMessage {


    /**
     * senderName : 前边
     * amount : 1
     * hongbaoId : 21281452_6542_1596383456
     * shortUrl : https://mclient.alipay.com/cashierRoutePay.htm?route_pay_from=h5&init_from=SDKLite&session=RZ55MhAasWXxayG3u8TLwCYOgDXat1mobilecashierRZ55&utdid=BJP/ueY0YUMDAIqK0X2Kyf5K&service=alipay.fund.stdtrustee.order.create.pay&tid=41fead7f9c328c8e909cc4fdfb34e84e5407cd50024f328df55e0ffa76c3f4b8&ltAndroidSdkLoadingVersion=Y&cc=y
     * aliPayUrl : https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttps%253A%252F%252Fwww.alipay.com%252F%253FappId%253D20000125%2526orderSuffix%253Dh5_route_token%25253D%252522RZ55MhAasWXxayG3u8TLwCYOgDXat1mobilecashierRZ55%252522%252526is_h5_route%25253D%252522true%252522%252526bizcontext%25253D%25257B%252522appkey%252522%25253A%2525222014052600006128%252522%25252C%252522ty%252522%25253A%252522and_lite%252522%25252C%252522sv%252522%25253A%252522h.a.3.5.3%252522%25252C%252522an%252522%25253A%252522com.taobao.qianniu%252522%25252C%252522av%252522%25253A%2525221.3.12%252522%25252C%252522sdk_start_time%252522%25253A1596383457466%25257D
     */
    private String senderName;
    private int amount;
    private String hongbaoId;
    private String shortUrl;
    private String aliPayUrl;
    private String sdkUrl;

    public QianNiuRpUpload(){
        this("qianNiuRpUpload");
    }

    public QianNiuRpUpload(String action){
        super(action);
    }

}
