package com.cyq.ppc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.cyq.framework.common.utils.OkHttpClientUtils;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.param.TcPayNotifyParam;
import com.cyq.ppc.service.OrderService;
import com.cyq.ppc.strategy.tcpay.TcPayProperties;
import com.cyq.ppc.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 话费直充
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tcpay")
public class TcPayController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private TcPayProperties tcPayProperties;

    /**
     * 回调通知
     *
     * @param request
     * @return
     */
    @RequestMapping("/notify")
    public String notify(HttpServletRequest request) throws Exception {
        String returnMsg = "success";
        String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("【话费直充】回调参数：\n {}", body);
        Map<String, String> params = JSON.parseObject(body, new TypeReference<Map<String, String>>() {
        });
        if (!SignUtils.verifySign(params, tcPayProperties.getSecretKey())) {
            log.error("【话费直充】回调参数验签失败！");
        }
        TcPayNotifyParam tcPayNotifyParam = JSON.parseObject(body, TcPayNotifyParam.class);
        if ("2".equals(tcPayNotifyParam.getOrderStatus())) {
            Order order = orderService.getByOrderId(tcPayNotifyParam.getMOrderCode());
            if (Objects.nonNull(order)) {
                if (OrderStatus.支付成功.getValue().equals(order.getOrderStatus())) {
                    log.warn("重复回调了，订单状态已为成功状态，不予处理！");
                    return returnMsg;
                }
                order.setOuterOrderId(tcPayNotifyParam.getSysOrderCode());
                order.setOrderStatus(OrderStatus.支付成功.getValue());
                order.setPayTime(tcPayNotifyParam.getPayTime());
                orderService.updateById(order);
            }
        }
        return returnMsg;
    }

    @RequestMapping("/queryOrder")
    public String queryOrder(String orderId) throws Exception {
        Order order = orderService.getByOrderId(orderId);
        if (Objects.isNull(order)) {
            throw new ApiException("订单【"+ orderId +"】不存在！");
        }
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("merchant_no", tcPayProperties.getMerchantNo());
        reqParams.put("m_order_code", orderId);
        reqParams.put("sign", SignUtils.generateSign(reqParams, tcPayProperties.getSecretKey()));
        log.info("【话费直充】订单查询参数：{}", JSON.toJSONString(reqParams));
        String responseStr = OkHttpClientUtils.post(tcPayProperties.getQueryOrderUrl(), reqParams);
        log.info("【话费直充】订单查询响应数据：{}", responseStr);
        Map<String, String> resParams = JSON.parseObject(responseStr, new TypeReference<Map<String, String>>() {
        });
        if (!SignUtils.verifySign(resParams, tcPayProperties.getSecretKey())) {
            log.error("【话费直充】订单查询返回参数验签失败！");
        }
        JSONObject jsonObject = JSON.parseObject(responseStr);
        if ("1".equals(jsonObject.getString("sys_code")) && "2".equals(jsonObject.getString("order_status"))) {
            if (!order.getOrderStatus().equals(OrderStatus.支付成功.getValue())) {
                // 更新订单状态为已支付
            }
        }
        return responseStr;
    }


}
