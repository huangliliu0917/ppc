package com.cyq.ppc.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.cyq.framework.common.utils.time.DateUtils;
import com.cyq.ppc.constant.ConfigConsts;
import com.cyq.ppc.constant.PayChannel;
import com.cyq.ppc.constant.YesNoStatus;
import com.cyq.ppc.entity.*;
import com.cyq.ppc.helper.RedisHelper;
import com.cyq.ppc.listener.mq.MqConsts;
import com.cyq.ppc.listener.mq.MqService;
import com.cyq.ppc.param.account.AccountQueryParam;
import com.cyq.ppc.param.order.MerchantOrderCreateParam;
import com.cyq.ppc.service.*;
import com.cyq.ppc.strategy.ChannelStrategy;
import com.cyq.ppc.utils.ChannelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/tbxjhb")
public class TbxjhbController extends AdminController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private EnvelopeService envelopeService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private MqService mqService;
    @Autowired
    private MerchantAccountStatService merchantAccountStatService;
    @Autowired
    private Map<PayChannel, ChannelStrategy> channelStrategyMap;
    @Autowired
    private ChannelService channelService;


    @GetMapping("/account/list")
    public ModelAndView merchant_list() {
        return new ModelAndView("tbxjhb/account_list");
    }

    @RequestMapping("/account/list")
    public JSONObject account_list(AccountQueryParam queryParam) {
        queryParam.setChannelCode(PayChannel.淘宝现金红包.getChannelCode());
        queryParam.setUserId(getUserId());
        queryParam.setUserType(getUserType());
        queryParam.setMerchantId(getMerchantId());
        return accountService.accountList(queryParam);
    }

    @GetMapping("/account/add")
    public ModelAndView idlefish_account_add() {
        return new ModelAndView("tbxjhb/account_add");
    }

    @PostMapping("/account/add")
    public R<Account> idlefish_account_add(Account account) {
        account.setChannelCode(PayChannel.淘宝现金红包.getChannelCode());
        account.setMerchantId(getMerchantId());
        JSONObject jsonObject = ChannelUtils.parseTaobaoCookie(account.getCookie());
        if (StringUtils.isAnyBlank(jsonObject.getString("unb"), jsonObject.getString("cookie2"), jsonObject.getString("senderName"))) {
            throw new ApiException("cookie信息有误，请重新输入");
        }
        account.setAccount(jsonObject.getString("senderName"));
        account.setUid(jsonObject.getString("unb"));
        account.setSid(jsonObject.getString("cookie2"));
        accountService.check(account);
        accountService.save(account);
        return success(account);
    }

    /**
     * 删除商户
     *
     * @param id
     * @return
     */
    @RequestMapping("/account/delete/{id}")
    public R<Object> remove(@PathVariable Integer id) {
        Account account = accountService.getById(id);
        envelopeService.remove(Wrappers.<Envelope>lambdaQuery().eq(Envelope::getUid, account.getUid()));
        channelService.notifyClusterCacheRefresh(account.getMerchantId(), PayChannel.淘宝现金红包.getChannelCode());
        return success(accountService.removeById(id));
    }

    /**
     * 更新状态
     *
     * @param id
     * @return
     */
    @RequestMapping("/account/updateStatus")
    public R<Object> updateStatus(Integer id, Integer status) {
        Account account = accountService.getById(id);
        account.setStatus(status);
        accountService.updateById(account);
        channelService.notifyClusterCacheRefresh(account.getMerchantId(), PayChannel.淘宝现金红包.getChannelCode());
        return success();
    }

    /**
     * 更新状态
     *
     * @param id
     * @return
     */
    @RequestMapping("/account/updateCookie")
    public R<Object> updateCookie(Integer id, String cookie) {
        JSONObject jsonObject = ChannelUtils.parseTaobaoCookie(cookie);
        if (StringUtils.isAnyBlank(jsonObject.getString("unb"), jsonObject.getString("cookie2"), jsonObject.getString("senderName"))) {
            throw new ApiException("cookie信息有误，请重新输入");
        }
        Account account = accountService.getById(id);
        account.setCookie(cookie);
        account.setSid(jsonObject.getString("cookie2"));
        accountService.updateById(account);
        channelService.notifyClusterCacheRefresh(account.getMerchantId(), PayChannel.淘宝现金红包.getChannelCode());
        return success();
    }


    @GetMapping("/channelTest")
    public ModelAndView channelTest() {
        return new ModelAndView("tbxjhb/channel_test");
    }

    /**
     * 通道测试
     *
     * @param createParam
     * @return
     */
    @PostMapping("/channelTest/{id}")
    public R<Object> channelTest(@PathVariable Integer id, MerchantOrderCreateParam createParam) throws Exception {
        Account account = accountService.getById(id);
        createParam.setAccount(account);
        createParam.setMerchantId(account.getMerchantId());
        createParam.setChannelCode(PayChannel.淘宝现金红包.getChannelCode());
        createParam.setMerchantOrderId("D" + IdUtil.objectId());
        ChannelStrategy channelStrategy = channelStrategyMap.get(PayChannel.淘宝现金红包);
        Order order = channelStrategy.createOrder(createParam);
        return success(order.getOrderId());
    }

    /**
     * 预产红包码
     *
     * @param id
     * @return
     */
    @RequestMapping("/preGen/{id}")
    public R<Object> preGen(@PathVariable Integer id) {
        Account account = accountService.getById(id);
        Config config = configService.getByCode(ConfigConsts.配置_淘宝现金红包预产码个数);
        String key = PayChannel.淘宝现金红包.getChannelCode() + ":preGen" + ":" + account.getAccount();
        if (StringUtils.isBlank(RedisHelper.get(key))) {
            String value = "ADDED: " + DateUtils.formatDateTime(LocalDateTime.now());
            RedisHelper.setEx(key, value, 24, TimeUnit.HOURS);
            envelopeService.remove(Wrappers.<Envelope>lambdaQuery().eq(Envelope::getUid, account.getUid()));
            int[] intArray = new int[]{49, 89, 189, 289, 389, 489};
            for (Integer amount : intArray) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("senderName", account.getAccount());
                jsonObject.put("uid", account.getUid());
                jsonObject.put("sid", account.getSid());
                jsonObject.put("account", account);
                jsonObject.put("amount", amount * 100);
                jsonObject.put("num", Integer.parseInt(config.getValue()));
                mqService.sendTopicMsg(MqConsts.淘宝现金红包_预产码_TOPIC, jsonObject.toJSONString());
            }
        } else {
            throw new ApiException("24小时之内只能产码一次哟，您今天已点过产码了哈！");
        }
        return success("产码提交成功，大概需要5分钟左右，请耐心等候！");
    }

    /**
     * 红包统计
     *
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/envelope/stat/{id}")
    public ModelAndView envelopeStat(@PathVariable Integer id, Model model) throws Exception {
        Account account = accountService.getById(id);
        List<Envelope> list = envelopeService.list(Wrappers.<Envelope>lambdaQuery()
                .eq(Envelope::getUid, account.getUid())
                .eq(Envelope::getStatus, YesNoStatus.NO.getValue())
                .gt(Envelope::getCreateTime, LocalDateTime.now().minusHours(24))
                .orderByAsc(Envelope::getAmount));
        Map<Integer, List<Envelope>> collect = list.stream().collect(Collectors.groupingBy(Envelope::getAmount));
        List<Map<String, Object>> data = new ArrayList<>();
        for (Integer key : collect.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("amount", key / 100);
            map.put("num", collect.get(key).stream().filter(e -> e.getStatus().equals(0)).count());
            data.add(map);
        }
        model.addAttribute("data", data);
        return new ModelAndView("tbxjhb/envelope_stat");
    }

    /**
     * 账号统计
     *
     * @param model
     * @return
     * @throws Exception
     */
    @GetMapping("/account/stat")
    public ModelAndView accountStat(Model model) throws Exception {
        List<MerchantAccountStat> list = merchantAccountStatService.list(Wrappers.<MerchantAccountStat>lambdaQuery()
                .eq(MerchantAccountStat::getMerchantId, getMerchantId())
                .eq(MerchantAccountStat::getChannelCode, PayChannel.淘宝现金红包.getChannelCode()));
        model.addAttribute("data", list);
        return new ModelAndView("tbxjhb/account_stat");
    }

}
