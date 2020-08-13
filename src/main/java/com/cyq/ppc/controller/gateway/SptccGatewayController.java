package com.cyq.ppc.controller.gateway;

import com.cyq.ppc.controller.AdminController;
import com.cyq.ppc.entity.OrderExt;
import com.cyq.ppc.service.OrderExtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/gateway/sptcc")
public class SptccGatewayController extends AdminController {
    @Autowired
    private OrderExtService orderExtService;

    /**
     * 支付链接加载
     *
     * @return
     */
    @GetMapping("/loading/{orderId}")
    public ModelAndView loading(@PathVariable String orderId, Model model, HttpServletRequest request) throws Exception {
        OrderExt orderExt = orderExtService.getByOrderId(orderId);
        if (isIOS(request)) {
            log.info("当前客户端系统为 IOS");
            String payUrl = URLEncoder.encode("{\"requestType\":\"SafePay\",\"fromAppUrlScheme\":\"com.alibaba.mobilei\",\"dataString\":\"" + orderExt.getSdkPayUrl() + "\"}", StandardCharsets.UTF_8.name());
            model.addAttribute("payUrl", "alipay://alipayclient/?" + payUrl);
            log.info("alipay://alipayclient/?" + payUrl);
        } else {
            log.info("当前客户端系统为 Android");
            model.addAttribute("payUrl", orderExt.getSdkPayUrl());
        }
        return new ModelAndView("gateway/sptcc/pay");
    }

}
