package com.mdd.admin.controller.selffetchOrder;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSONArray;
import com.mdd.admin.LikeAdminThreadLocal;
import com.mdd.admin.service.IOrderManageService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.order.OrderRemarkValidate;
import com.mdd.admin.validate.order.OrderSearchValidate;
import com.mdd.admin.validate.order.OrderSelffetchValidate;
import com.mdd.admin.validate.order.OrderSendDeliveryValidate;
import com.mdd.admin.vo.order.OrderManageDetailVo;
import com.mdd.admin.vo.order.OrderManageExportVo;
import com.mdd.admin.vo.order.OrderManageListedVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.enums.OrderEnum;
import com.mdd.common.enums.OrderGoodsEnum;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/order/selffetch")
@Api("订单管理")
public class SelffetchOrderManageController {

    @Resource
    IOrderManageService iOrderManageService;

    @GetMapping("/list")
    @ApiOperation("订单列表")
    public AjaxResult<PageResult<OrderManageListedVo>> list(@Validated PageValidate pageValidate,
                                                            @Validated OrderSearchValidate orderSearchValidate) {
        orderSearchValidate.setDeliveryType(OrderEnum.DELIVERY_TYPE_PICK.getCode());
        PageResult<OrderManageListedVo> lists = iOrderManageService.list(pageValidate,orderSearchValidate);
        return AjaxResult.success(lists);
    }

    @GetMapping("/detail")
    @ApiOperation("订单详情")
    public AjaxResult<OrderManageDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        OrderManageDetailVo detail = iOrderManageService.detail(id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/cancel")
    @ApiOperation("订单取消")
    public AjaxResult<Object> cancel(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderManageService.cancel(idValidate.getId(), adminId);
        return AjaxResult.success();
    }

    @PostMapping("/remarks")
    @ApiOperation("订单备注")
    public AjaxResult<Object> remarks(@Validated @RequestBody OrderRemarkValidate remarkValidate) {
        Integer orderId = remarkValidate.getId();
        String remarks = remarkValidate.getRemarks();
        iOrderManageService.remarks(orderId, remarks);
        return AjaxResult.success();
    }

    @PostMapping("/sendDelivery")
    @ApiOperation("订单发货")
    public AjaxResult<Object> sendDelivery(@Validated @RequestBody OrderSendDeliveryValidate sendDeliveryValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderManageService.sendDelivery(adminId, sendDeliveryValidate);
        return AjaxResult.success();
    }

    @PostMapping("/takeDelivery")
    @ApiOperation("确认收货")
    public AjaxResult<Object> takeDelivery(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderManageService.takeDelivery(idValidate.getId(), adminId);
        return AjaxResult.success();
    }

    @GetMapping("/logistics")
    @ApiModelProperty("查看物流")
    public AjaxResult<Object> logistics(@Validated @IDMust() @RequestParam("id") Integer id) {
        Map<String, Object> map = iOrderManageService.logistics(id);
        return AjaxResult.success(map);
    }

    @PostMapping("/verify")
    @ApiOperation("确认自提")
    public AjaxResult<Object> verify(@Validated @RequestBody OrderSelffetchValidate orderSelffetchValidate) {
        //System.out.println(orderSelffetchValidate);
        Integer orderId = orderSelffetchValidate.getId();
        JSONArray items = orderSelffetchValidate.getItems();
        iOrderManageService.selffetchVerify(orderId, items);
        return AjaxResult.success();
    }

    @GetMapping("/exportExcel")
    @ApiOperation("订单列表")
    @NotLogin
    public void exportExcel(HttpServletResponse response, @Validated OrderSearchValidate orderSearchValidate) throws Exception {
        try {
            orderSearchValidate.setDeliveryType(OrderEnum.DELIVERY_TYPE_PICK.getCode());
            List<OrderManageExportVo> lists = iOrderManageService.exportExcel(orderSearchValidate);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(TimeUtils.timestamp() + "-自提订单列表","UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

            EasyExcel.write(response.getOutputStream(), OrderManageExportVo.class).sheet("自提订单列表").doWrite(lists);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
