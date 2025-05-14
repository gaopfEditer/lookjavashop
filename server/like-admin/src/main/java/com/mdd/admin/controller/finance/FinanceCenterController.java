package com.mdd.admin.controller.finance;

import com.mdd.admin.service.IFinanceCenterService;
import com.mdd.admin.vo.finance.FinanceCenterDataVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/finance/center")
@Api("财务中心管理")
public class FinanceCenterController {

    @Resource
    IFinanceCenterService iFinanceCenterService;

    @GetMapping("/data")
    @ApiModelProperty("数据统计")
    public AjaxResult<FinanceCenterDataVo> data() {
        FinanceCenterDataVo vo = iFinanceCenterService.data();
        return AjaxResult.success(vo);
    }

}
