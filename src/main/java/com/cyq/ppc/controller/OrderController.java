package com.cyq.ppc.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.cyq.framework.common.utils.CommonUtils;
import com.cyq.ppc.constant.OrderNotifyStatus;
import com.cyq.ppc.constant.OrderStatus;
import com.cyq.ppc.constant.PayChannel;
import com.cyq.ppc.constant.UserTypeEnum;
import com.cyq.ppc.entity.Account;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.listener.mq.MqConsts;
import com.cyq.ppc.listener.mq.MqService;
import com.cyq.ppc.param.order.OrderQueryParam;
import com.cyq.ppc.service.AccountService;
import com.cyq.ppc.service.OrderService;
import com.cyq.ppc.strategy.unicom.UnicomService;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.packet.tbxjhb.TbxjhbQueryReq;
import com.cyq.wsserver.server.ImUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Validated
@RestController
@RequestMapping("/order")
public class OrderController extends AdminController {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private OrderService orderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UnicomService unicomService;
    @Autowired
    private MqService mqService;

    /**
     * 补单
     *
     * @param orderId
     * @return
     */
    @RequestMapping("/supplyNotify")
    public R<Object> supplyNotify(String orderId) {
        Order order = orderService.getByOrderId(orderId);
        if (!OrderNotifyStatus.回调失败.getValue().equals(order.getOrderStatus())) {
            throw new ApiException("只有回调失败的订单才能人工回调哟");
        }
        if (!OrderStatus.支付成功.getValue().equals(order.getOrderStatus())) {
            throw new ApiException("只有支付成功的订单才能人工补发回调哟");
        }
        if (StringUtils.isNotBlank(order.getNotifyUrl()) && order.getNotifyUrl().startsWith("http")) {
            mqService.sendQueueMsg(MqConsts.订单支付成功_回调通知_QUEUE, order.getOrderId());
            order.setNotifyStatus(OrderNotifyStatus.回调中.getValue());
            orderService.updateById(order);
        }
        return success();
    }

    /**
     * 补单
     *
     * @param orderId
     * @return
     */
    @RequestMapping("/supplyOrder")
    public R<Object> supplyOrder(String orderId) {
        Order order = orderService.getByOrderId(orderId);
        if (!OrderStatus.支付失败.getValue().equals(order.getOrderStatus())) {
            throw new ApiException("只有支付失败的订单才能补单哟");
        }
        order.setOrderStatus(OrderStatus.支付成功.getValue());
        order.setSupplyTime(LocalDateTime.now());
        orderService.updateById(order);
        return success();
    }

    @RequestMapping("/supplyTake")
    public R<Object> supplyTake(String orderId) {
        Order order = orderService.getByOrderId(orderId);
        if (!OrderStatus.支付失败.getValue().equals(order.getOrderStatus())) {
            throw new ApiException("只有支付失败的订单才能补领哟");
        }
        if (!CommonUtils.inString(order.getChannelCode(), PayChannel.淘宝现金红包.getChannelCode(), PayChannel.联通卡密.getChannelCode())) {
            throw new ApiException("通道【" + order.getChannelCode() + "】，不支持补领");
        }
        if (PayChannel.淘宝现金红包.getChannelCode().equals(order.getChannelCode())) {
            Account account = accountService.getOne(Wrappers.<Account>lambdaQuery()
                    .eq(Account::getAccount, order.getAccount())
                    .eq(Account::getChannelCode, order.getChannelCode()));
            if (Objects.nonNull(account)) {
                TbxjhbQueryReq tbxjhbQueryReq = new TbxjhbQueryReq();
                tbxjhbQueryReq.setHongbaoId(order.getUid());
                tbxjhbQueryReq.setSenderName(order.getAccount());
                tbxjhbQueryReq.setUid(account.getUid());
                tbxjhbQueryReq.setSid(account.getSid());
                tbxjhbQueryReq.setOrderId(order.getOrderId());
                ImUtils.sendToTbxjhb(new ImPacket(tbxjhbQueryReq));
            }
        } else if (PayChannel.联通卡密.getChannelCode().equals(order.getChannelCode())) {
            executorService.execute(() -> unicomService.checkOrderStatus(orderId));
        }
        return success("补领命令已发送成功");
    }

    @GetMapping("/list")
    public ModelAndView orderList(Model model) {
        model.addAttribute("channelDataList", PayChannel.comboBox());
        return new ModelAndView("order/list");
    }

    @PostMapping("/list")
    public JSONObject list(OrderQueryParam queryParam) {
        if (UserTypeEnum.商户.getValue().equals(getUserType())) {
            queryParam.setMerchantId(getMerchantId());
        } else if (UserTypeEnum.代理.getValue().equals(getUserType())) {
            queryParam.setAgentId(getUserId());
        }
        return orderService.orderList(queryParam);
    }

}
