package com.mdd.admin.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 基础设置Vo
 */
@Data
@ApiModel("基础设置Vo")
public class SettingWebsiteVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "网站名称")
    private String name;
    @ApiModelProperty(value = "网站logo")
    private String logo;
    @ApiModelProperty(value = "网站图标")
    private String favicon;
    @ApiModelProperty(value = "登录页广告图")
    private String backdrop;
    @ApiModelProperty(value = "文档信息: 0=隐藏 1=显示")
    private Integer isShowDoc;

    @ApiModelProperty(value = "商城名称")
    private String shopName;
    @ApiModelProperty(value = "商城LOGO")
    private String shopLogo;
    @ApiModelProperty(value = "经营状态: 0=关闭 1=开启")
    private Integer shopStatus;

    @ApiModelProperty(value = "PC端LOGO")
    private String pcLogo;
    @ApiModelProperty(value = "网站标题")
    private String pcTitle;
    @ApiModelProperty(value = "网站图标")
    private String pcIco;
    @ApiModelProperty(value = "网站描述")
    private String pcDesc;
    @ApiModelProperty(value = "网站关键词")
    private String pcKeywords;

    @ApiModelProperty(value = "联系人名")
    private String contactNickname;
    @ApiModelProperty(value = "联系电话")
    private String contactMobile;

    @ApiModelProperty(value = "收货人姓名")
    private String retreatConsignee;
    @ApiModelProperty(value = "收货人电话")
    private String retreatMobile;
    @ApiModelProperty(value = "收货人省份ID")
    private String retreatProvinceId;
    @ApiModelProperty(value = "收货人城市ID")
    private String retreatCityId;
    @ApiModelProperty(value = "收货人地区ID")
    private String retreatDistrictId;
    @ApiModelProperty(value = "收货人地址")
    private String retreatAddress;

}
