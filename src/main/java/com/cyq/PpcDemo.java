package com.cyq;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Slf4j
public class PpcDemo {

    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) throws Exception {
        String text = "_input_charset=\"utf-8\"&body=\"购买50元充值卡1张（您预留的手机号码是：13270049663）\"&it_b_pay=\"2d\"&notify_url=\"https://unipay.10010.com/udpNewPortal/miniwappayment/alipayAppServiceCallback\"&out_trade_no=\"U000345959529191309312\"&partner=\"2088201747196380\"&payment_type=\"1\"&seller_id=\"hq-dqykc@chinaunicom.cn\"&subject=\"购买50元充值卡1张（您预留的手机号码是：13270049663）\"&total_fee=\"50\"&sign=\"G%2BVPfX6pxHu0swXl1tZpQESdwC3fUxvrqhe1KsfJVhyZ5P4CcaBm6S3gdfpCcsYzLq5uUszGg2DJJirp79Ii94KCDNbOLayNWfRARakwTDhoYT9eOFrAXCceVT1I8C5tZVSRVVeWeJeGaY2tsgTJTGaNjPa7jFgGv1qMxgbb3KU%3D\"&sign_type=\"RSA\"&bizcontext=\"{\"appkey\":\"2014052600006128\",\"ty\":\"and_lite\",\"sv\":\"h.a.3.6.8\",\"an\":\"com.sinovatech.unicom.ui\",\"av\":\"7.4.1\",\"sdk_start_time\":1597219179474}\"";
        String out_trade_no = StringUtils.substringBetween(text, "out_trade_no=", "&").replace("\"", "");
        System.out.println(out_trade_no);

        System.out.println(formateMoney(BigDecimal.valueOf(1)));
        String bodyStr = "loginStyle=0&isRemberPwd=true&password=vJ3beOoozvYushoIN0TCDC8J3C7fsdha3niEPUZSSyx8X3QBNepZECmsh2udkuw%2BdCmTqPpcIS7iiWVKTIUWiidGnV%2FhmxGcrRRQchS8VAEEetXGkejuOWawghJ72h0zJJKBz4v1dGcYnzghErugGyzYnGcILOF5sRKVhU%2FPWk0%3D&deviceId=864590037847758&netWay=Wifi&phone=XcvdutdpsXEbIYgvCtp%2FIQy8oUHeFtgzwgBBVA%2BGIFvT7C1nFJl8XPdP1LwYSUhnO%2BaOwZGkbf8QyB9BDn2tuHe0LQxXQpNTLXWM9ZgEjvfIztkPwF2yuPsPaLmfla86h%2BeROOpcMe%2Bv5Ow5rUXnhNmc4xbq3XkgFWV5GGdGHP0%3D&yw_code=&timestamp=20200713232107&appId=ChinaunicomMobileBusiness&keyVersion=&deviceBrand=HUAWEI&voiceoff_flag=1&pip=192.168.1.105&voice_code=&provinceChanel=general&platformToken=&version=android%7.0402&deviceModel=HUAWEI%20CAZ-AL10&deviceOS=android7.0&deviceCode=864590037847758&pushPlatform=HUAWEI";
        //splitParams(bodyStr);
    }

    public static void splitParams(String bodyStr) {
        System.out.println("MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();");
        String[] split = StringUtils.split(bodyStr, "&");
        for (String vk : split) {
            String[] paramArray = StringUtils.split(vk, "=");
            if (paramArray.length == 1) {
                System.out.println("body.add(\"" + paramArray[0] + "\", null);");
            } else {
                System.out.println("body.add(\"" + paramArray[0] + "\",\"" + paramArray[1] + "\");");
            }

        }
    }

    /**
     * 金额格式化
     *
     * @param money
     * @return
     */
    public static String formateMoney(BigDecimal money) {
        String urlMoney = "";
        DecimalFormat df = new DecimalFormat("#.00");
        if (money.compareTo(BigDecimal.ONE) >= 0) {
            urlMoney = df.format(money);
        } else {
            urlMoney = "0" + df.format(money);
        }
        return urlMoney;
    }

}
