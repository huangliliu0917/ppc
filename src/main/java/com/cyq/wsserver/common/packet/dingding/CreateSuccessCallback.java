package com.cyq.wsserver.common.packet.dingding;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 收款信息异常通知
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreateSuccessCallback extends WsMessage {
    /**
     * uid : 1904937608
     * orderId : 1179728188
     * money : 0.01
     * sender : 1904937608
     * outerOrderId : 3i7eTNUx_1904937608100
     * redId : 3i7eTNUx
     * type : dingding
     * webPayUrl : https://mclient.alipay.com/h5/cashierSwitchAccountSel.htm?session=RZ555dEi5SZ7dGonh8MDhc6p5MKJFhmobilecashierRZ55&cc=y&logonId=otherAccount&userIdLdc=
     * iosPayUrl : alipay://alipayclient/?%7B%22requestType%22%3A%22SafePay%22%2C%22fromAppUrlScheme%22%3A%22dingtalk%22%2C%22dataString%22%3A%22alipay_sdk%3Dalipay-sdk-java-4.9.220.DEV%26app_id%3D2018101061618854%26biz_content%3DXF%252FVh%252BEFtpRoyrgmTphGq%252Fe%252F8i9L7sSEFrqql5b%252FLRLS3dHocpuqJ5A2Bf%252F1BeeSfKr5dhdnnQKp8IQURa%252Fw6srO%252FED2L%252BBj8Z%252FqL3pg7vCqs%252F50Ea%252BXUzBJBbIAHZwhP5cYNrDtqbArMFN1Un65t0La1WJolQXUhe4WuaMfF3mOX5FSIl97MJjy8J5nYDHhCUINVpEUTFudBNphNgjzIdFFxckB%252FI2t63mkm8oke1n8TSPWOk18wJFwVdBkNADZ8Ey4FCDvucma%252BD2pmGNi9hD4m3gBhw6Nrak7l14UXshpKSgoHKoz%252F56%252FXqR0fXXeZCtSYynlhp2yDbzYIUGmBwE%252Fr1Lt4cqxhDP1pxoqrRu7lywMPHRvlg7dGPJS9laZ%252Fl6OvBhp%252FyNkfLodvDwudVvaIycf7AqzHpCs6Q5YJF5xn3bNkf3Cs5Rrg0rk2qEX1rnMgebjlIVO%252FtSWgx9jEmJz3gVAZdWNAQPKLOSrcuhqr7VzU%252F75TclIERJ5Tq2qUxnQ8KM2jTbbbk5O5noqlbFSXVche1g3Lkl4W1p%252B%252FrCR3ixTbvZN0GfWNFJaf2vUMkeh%252Fqmqvqb0%252F%252BKAMlqmGlznZP55Zpy1pZSZq%252FGdqfLxyxNrCZ7KKGEiByLgFjKqR%252F%252B87oEOD3w7WgceD8ygcII3LSrCSSltBCpBR2%252FRtpuN1UEVZlJoP06e%252Fje4JuOsA3Z%252Fnj9Iq3tnLDCe55eukOJ8%252FyXCJoyTKbvQFhc2f0YMnmlgOnsxC8u78d%252FXswWMTuXPzHUloBbYczf%252B8EZ2y6JEMMv%252BsBhi6Y2i1%252BE1VYCcmh47fkDAVvBd0F2r7w7yYqggsC6vYsmBfDDsWMbvgHtIqSahDAFvSL0UgVEZgFZT7XmhZaV5z7xpel27D0Jj0TFoP6h2%252FIJixNX8TL%252BYVlPL2yo3fyNxwH5BapeT6%252Fk1%252F2BApV2TVbd7TngfQ0nBQp7TVjbhXLJfw4Ca7WilsGnvd9BXL2vKDs5H1syHW8JIvkWdk%252BKoTBkPZ5dQljWeWZsjplZ9ZoHmJLihF%252FD3MBM6vxUio1o%252BRP1rNmuKtzrYjYNqqpAH9VvSOamMzlB%252BpYTVqfaINPH2wtrtN8KNL9C38QC6ph5TRrT7%252F1ZLwXbaXYvF8MXLf%252B5OztRvEeG0%26charset%3DUTF-8%26encrypt_type%3DAES%26format%3Djson%26method%3Dalipay.fund.trans.app.pay%26sign%3DEe9%252FEd%252FsOBnhYH0njECQm205G0rZGQ6jAxrkXNeEZcq7B8TkGYnpNaTTUIwHx3R9uZwgoT56eisTfcbZ8Z%252Fw57HnUf%252FXuH5YArdCXcX05ogmwAt9AUp6Qz3TGXq92aUnW3aOyKOnEsLPVyVmKI5iY180z1GaBamc6bRz4MPC21%252FfDgIRiR7iJ8IZIHiYg5%252BP1rUAnLQs9hGE2OFMj07FhpnBGp1dMsfGGiH0iA2Stn%252BsVEAHpiIi3n4%252Bw2cm1PJjOZJ0ZBzz3tEyU8FoPGr8eS3ASDVTD3M%252BpXChE8qFIsUsw8rTtdpoqWSRRy8anhX%252FAHqjJPjb23x64NYKZQT9QA%253D%253D%26sign_type%3DRSA2%26timestamp%3D2020-07-05%2B00%253A45%253A54%26version%3D1.0%22%7D
     * account : 123
     * channelCode : dingdingred
     */
    private String uid;
    private String orderId;
    private BigDecimal money;
    private String sender;
    private String outerOrderId;
    private String redId;
    private String type;
    private String webPayUrl;
    private String iosPayUrl;
    private String account;

    public CreateSuccessCallback(String action) {
        super(action);
    }
}
