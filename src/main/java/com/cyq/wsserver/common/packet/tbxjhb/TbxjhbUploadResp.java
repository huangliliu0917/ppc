package com.cyq.wsserver.common.packet.tbxjhb;

import com.cyq.wsserver.common.packet.WsMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TbxjhbUploadResp extends WsMessage {
    /**
     * account : 123
     * amount : 1
     * hongbao_id : 888661592050992985
     * out_request_no : 888661592050992985_dc1627d688beab021b41f48458255ab7_1_p
     * shortUrl : alipays://platformapi/startApp?appId=20000125&orderSuffix=h5_route_token%3D%22RZ55Rr7PJAC8azbocxF550YGJTnlNamobilecashierRZ55%22%26is_h5_route%3D%22true%22%23Intent%3Bscheme%3Dalipays%3Bpackage%3Dcom.eg.android.AlipayGphone%3Bend
     * tklUrl : https://m.tb.cn/h.VLBCxzA
     * url :
     */
    private String orderId;
    private String account;
    private String uid;
    private Integer amount;
    private String hongbaoId;
    private String outOrderId;
    private String shortUrl;
    private String tklUrl;
    private String sdkUrl;
    private String error;
    private String senderName;

    public TbxjhbUploadResp(){
        this("upload");
    }

    public TbxjhbUploadResp(String action){
        super(action);
    }

}
