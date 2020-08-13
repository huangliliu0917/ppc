package com.cyq.ppc.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyq.ppc.entity.Merchant;
import com.cyq.ppc.entity.Order;
import com.cyq.ppc.param.merchant.MerchantCreateParam;
import com.cyq.ppc.param.merchant.MerchantQueryParam;
import com.cyq.ppc.service.AccountService;
import com.cyq.ppc.service.MerchantService;
import com.cyq.ppc.service.OrderService;
import com.cyq.ppc.service.UserService;
import com.cyq.ppc.utils.LayuiUtils;
import com.cyq.ppc.vo.MerchantVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * <p>
 * 商户信息表 前端控制器
 * </p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/merchant")
public class MerchantController extends AdminController {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public ModelAndView merchant_list() {
        return new ModelAndView("merchant/merchant_list");
    }

    /**
     * 查询商户
     *
     * @return
     */
    @RequestMapping("/list")
    public JSONObject merchant_list(MerchantQueryParam queryParam) {
        Page<MerchantVO> page = new Page<>(queryParam.getPage(), queryParam.getLimit());
        queryParam.setUserId(getUserId());
        queryParam.setUserType(getUserType());
        merchantService.listVO(page, queryParam);
        return LayuiUtils.buildGrid(page);
    }

    @GetMapping("/add")
    public ModelAndView merchant_add() {
        return new ModelAndView("merchant/merchant_add");
    }

    @PostMapping("/add")
    public R<Merchant> merchant_add(@Valid MerchantCreateParam createParam) {
        createParam.setAgentId(getUserId());
        Merchant merchant = merchantService.create(createParam);
        return success(merchant);
    }

    /**
     * 删除商户
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete/{id}")
    public R<Object> remove(@PathVariable Integer id) {
        Merchant merchant = merchantService.getById(id);
        userService.removeById(merchant.getUserId());
        accountService.deleteByMerchantId(merchant.getMerchantId());
        orderService.remove(Wrappers.<Order>lambdaQuery().eq(Order::getMerchantId, merchant.getMerchantId()));
        return success(merchantService.removeById(id));
    }

    /**
     * 更新状态
     *
     * @param id
     * @return
     */
    @RequestMapping("/updateStatus")
    public R<Object> updateStatus(Integer id, Integer status) {
        boolean update = merchantService.update(Wrappers.<Merchant>lambdaUpdate().
                set(Merchant::getStatus, status)
                .eq(Merchant::getId, id));
        return success(update);
    }

    /**
     * 重置商户密钥
     *
     * @param id
     * @return
     */
    @RequestMapping("/resetSecret/{id}")
    public R<Object> resetSecret(@PathVariable Integer id) {
        boolean update = merchantService.update(Wrappers.<Merchant>lambdaUpdate().
                set(Merchant::getSecretKey, IdWorker.get32UUID())
                .eq(Merchant::getId, id));
        return success(update ? 1 : 0);
    }

}
