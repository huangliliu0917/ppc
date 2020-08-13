package com.cyq.ppc.controller.gateway;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.cyq.ppc.constant.AppConsts;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.constant.PayChannel;
import com.cyq.ppc.constant.YesNoStatus;
import com.cyq.ppc.controller.AdminController;
import com.cyq.ppc.entity.Merchant;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.helper.SpringContextHolder;
import com.cyq.ppc.listener.event.DispatchOrderEvent;
import com.cyq.ppc.param.order.MerchantOrderCreateParam;
import com.cyq.ppc.param.order.MerchantOrderQueryParam;
import com.cyq.ppc.param.order.OrderQueryParam;
import com.cyq.ppc.service.*;
import com.cyq.ppc.strategy.ChannelStrategy;
import com.cyq.ppc.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 本平台支付接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/gateway")
public class PayGatewayController extends AdminController {
    private static final String ORDER_LOADING = "XXXX";
    private static final String ORDER_QUERY = "XXXXXX";
    @Autowireds
    private MerchantService merchantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderExtService orderExtService;
    @Autowired
    private ToolkitService toolkitService;
    @Autowired
    BlackGroupService blackGroupService;
    @Autowired
    private Map<PayChannel, ChannelStrategy> channelStrategyMap;

    /**
     * 支付订单创建入口页面
     *
     * @return
     */
    @GetMapping("/index")
    public ModelAndView pay() {
        return new ModelAndView("gateway/index");
    }

    @GetMapping("/order/list")
    public ModelAndView list() {
        return new ModelAndView("gateway/order_list");
    }

    /**
     * 订单列表
     *
     * @param queryParam
     * @return
     */
    @PostMapping("/order/list")
    public JSONObject list(OrderQueryParam queryParam) {
        return orderService.orderList(queryParam);
    }

    @GetMapping("/sdkTransform")
    public ModelAndView sdk_transform() {
        return new ModelAndView("gateway/sdk_transform");
    }

    /**
     * 创建支付订单
     *
     * @param createParam
     * @return
     * @throws Exception
     */
    @PostMapping("/createDemoOrder")
    public R<Object> createOrder(MerchantOrderCreateParam createParam) throws Exception {
        createParam.setMerchantId(AppConsts.演示商户ID);
        createParam.setMerchantOrderId("Y" + IdUtil.objectId());
        PayChannel payChannel = PayChannel.getPayChannel(createParam.getChannelCode());
        if (Objects.isNull(payChannel)) {
            throw new ApiException("通道未开通");
        }
        ChannelStrategy channelStrategy = channelStrategyMap.get(payChannel);
        if (Objects.isNull(channelStrategy)) {
            throw new ApiException("通道还未集成，如有需要，请联系提供商！");
        }
        Order order = channelStrategy.createOrder(createParam);
        SpringContextHolder.publishEvent(new DispatchOrderEvent(this, order));
        String href = MessageFormat.format("/api/gateway/{0}/loading/{1}", createParam.getChannelCode(), order.getOrderId());
        log.info("href：{}", href);
        return success(href);
    }

    @PostMapping("/createOrder")
    public R<Object> createOrder(HttpServletRequest request) throws Exception {
        String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("支付订单创建参数：\n {}", body);
        if (StringUtils.isBlank(body)) {
            throw new ApiException("请以 POST JSON 的方式提交参数数据");
        }
        Map<String, String> params = JSON.parseObject(body, new TypeReference<Map<String, String>>() {
        });
        MerchantOrderCreateParam createParam = JSON.parseObject(body, MerchantOrderCreateParam.class);
        log.info("MerchantOrderCreateParam：{}", JSON.toJSONString(createParam));
        Assert.hasText(createParam.getChannelCode(), "通道编码不能为空！");
        Assert.hasText(createParam.getMerchantId(), "商户号不能为空！");
        Assert.hasText(createParam.getMerchantOrderId(), "商户订单号不能为空！");
        Assert.hasText(createParam.getNotifyUrl(), "回调地址不能为空！");
        Assert.hasText(createParam.getSign(), "接口签名不能为空！");
        if (createParam.getAmount() == null || createParam.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("金额必须大于0");
        }
        PayChannel payChannel = PayChannel.getPayChannel(createParam.getChannelCode());
        if (Objects.isNull(payChannel)) {
            throw new ApiException("通道不存在，请核对！");
        }
        String merchantId = createParam.getMerchantId();
        Merchant merchant = merchantService.getByMerchantId(merchantId);
        if (Objects.isNull(merchant)) {
            log.error("商户【" + merchantId + "】不存在！");
            throw new ApiException("商户【" + merchantId + "】不存在！");
        }
        if (YesNoStatus.NO.getValue().equals(merchant.getStatus())) {
            log.error("商户【" + merchantId + "】已被禁用！");
            throw new ApiException("商户【" + merchantId + "】已被禁用！");
        }
        if (!SignUtils.verifySign(params, merchant.getSecretKey())) {
            throw new ApiException("参数验签失败");
        }
        Order order = channelStrategyMap.get(payChannel).createOrder(createParam);
        String payUrl = MessageFormat.format(ORDER_LOADING, createParam.getChannelCode(), order.getOrderId());
        log.info("支付链接payUrl：{}", payUrl);
        Map<String, String> resMap = new HashMap<>();
        resMap.put("orderId", order.getOrderId());
        resMap.put("merchantOrderId", order.getMerchantOrderId());
        resMap.put("amount", order.getAmount().toString());
        resMap.put("payUrl", payUrl);
        if (payChannel.getChannelCode().equals(PayChannel.话费直充.getChannelCode())) {
            resMap.put("payUrl", order.getLink());
        }
        resMap.put("sign", SignUtils.generateSign(resMap, merchant.getSecretKey()));
        return success(resMap);
    }

    @PostMapping("/queryOrder")
    public R<Object> queryOrder(HttpServletRequest request) throws Exception {
        String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("支付订单查询参数：\n {}", body);
        Map<String, String> params = JSON.parseObject(body, new TypeReference<Map<String, String>>() {
        });
        MerchantOrderQueryParam queryParam = JSON.parseObject(body, MerchantOrderQueryParam.class);
        if (StringUtils.isAnyBlank(queryParam.getMerchantId(), queryParam.getMerchantOrderId(), queryParam.getSign())) {
            throw new ApiException("参数不完整！");
        }
        String merchantId = queryParam.getMerchantId();
        Merchant merchant = merchantService.getByMerchantId(merchantId);
        if (Objects.isNull(merchant)) {
            log.error("商户【" + merchantId + "】不存在！");
            throw new ApiException("商户【" + merchantId + "】不存在！");
        }
        if (!SignUtils.verifySign(params, merchant.getSecretKey())) {
            log.error("参数验签失败！");
        }
        Order order = orderService.getByMerchantIdAndMerchantOrderId(queryParam.getMerchantId(), queryParam.getMerchantOrderId());
        if (Objects.isNull(order)) {
            throw new ApiException("商户订单号【" + queryParam.getMerchantOrderId() + "】不存在！");
        }
        Map<String, String> resMap = new HashMap<>();
        resMap.put("orderId", order.getOrderId());
        resMap.put("merchantOrderId", order.getMerchantOrderId());
        resMap.put("amount", order.getAmount().toString());
        resMap.put("status", String.valueOf(OrderStatus.支付成功.getValue().equals(order.getOrderStatus()) ? 1 : 0));
        resMap.put("sign", SignUtils.generateSign(resMap, merchant.getSecretKey()));
        return success(resMap);
    }

    @RequestMapping("/supplyTakeAll")
    public R<Object> supplyTakeAll(String merchantId, String channelCode) {
        return null;
    }

}
