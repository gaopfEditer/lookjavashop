package com.mdd.admin.validate.setting;

import com.mdd.admin.vo.setting.SettingPaymentMethodVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("支付方式设置参数")
public class SettingPayMethodValidate {

    @ApiModelProperty(value = "支付方式")
    List<List<SettingPaymentMethodVo>> data;

}
