package com.cyq.ppc.controller.gateway;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.cyq.framework.common.utils.time.DateUtils;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.controller.AdminController;
import com.cyq.ppc.entity.Envelope;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.entity.OrderExt;
import com.cyq.ppc.service.EnvelopeService;
import com.cyq.ppc.service.OrderExtService;
import com.cyq.ppc.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/gateway/tbxjhb")
public class TbxjhbGatewayController extends AdminController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderExtService orderExtService;
    @Autowired
    private EnvelopeService envelopeService;

    /**
     * 支付链接加载
     *
     * @return
     */
    @GetMapping("/loading/{orderId}")
    public ModelAndView loading(@PathVariable String orderId, Model model) throws Exception {
        model.addAttribute("orderId", orderId);
        return new ModelAndView("gateway/tbxjhb/loading");
    }

    @GetMapping("/order/status/{orderId}")
    public R<Map<String, Object>> loading(@PathVariable String orderId) throws Exception {
        Order order = orderService.getByOrderId(orderId);
        if (Objects.isNull(order)) {
            throw new ApiException(String.format("订单【%s】不存在！", orderId));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("orderStatus", order.getOrderStatus());
        map.put("orderStatusName", OrderStatus.getOrderStatus(order.getOrderStatus()).name());
        return success(map);
    }

    /**
     * 跳转到支付页面
     *
     * @param orderId
     * @return
     */
    @GetMapping("/gopay/{orderId}")
    public ModelAndView gopay(@PathVariable String orderId, Model model) throws Exception {
        Order order = orderService.getByOrderId(orderId);
        if (Objects.isNull(order)) {
            throw new ApiException(String.format("订单号【%s】不存在！", orderId));
        }
        if (Objects.nonNull(order.getExpireTime()) && order.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new ApiException("订单已过期！");
        }
        Envelope envelope = envelopeService.getOne(Wrappers.<Envelope>lambdaQuery().eq(Envelope::getEnvelopeId, order.getUid()));
        model.addAttribute("amount", order.getAmount());
        model.addAttribute("orderId", order.getOrderId());
        //model.addAttribute("sdkUrl", URLEncoder.encode(Base64.encodeBase64String(RSA.encryptByPublicKey(envelope.getSdkUrl(), AppConsts.PUBLIC_KEY)), StandardCharsets.UTF_8.name()));
        model.addAttribute("sdkUrl", URLEncoder.encode(envelope.getSdkUrl(), StandardCharsets.UTF_8.name()));
        model.addAttribute("sign", DigestUtils.md5Hex(envelope.getSdkUrl() + "|45446ytytutu657"));
/*        model.addAttribute("tklUrl",envelope.getTklUrl());
        model.addAttribute("sdkUrl", envelope.getSdkUrl());*/

        //model.addAttribute("sdkUrl", StringUtils.substringBefore(envelope.getSdkUrl(), "&bizcontext"));
/*        model.addAttribute("tklUrl", envelope.getTklUrl());
        model.addAttribute("sdkUrl", envelope.getSdkUrl());*/

        model.addAttribute("expireAt", DateUtils.toMilliseconds(order.getExpireTime()) / 1000);
        return new ModelAndView("gateway/tbxjhb/pay_xjhb");
    }

    /**
     * 跳转到支付页面
     *
     * @param orderId
     * @return
     */
    @GetMapping("/openAlipay/{orderId}")
    public ModelAndView openAlipay(@PathVariable String orderId, Model model) {
        Order order = orderService.getByOrderId(orderId);
        OrderExt orderExt = orderExtService.getByOrderId(orderId);
        if (Objects.isNull(order)) {
            throw new ApiException(String.format("订单号【%s】不存在！", orderId));
        }
        model.addAttribute("payUrl", orderExt.getSdkPayUrl());
        return new ModelAndView("gateway/tbxjhb/open_alipay");
    }

}
