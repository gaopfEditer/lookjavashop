package com.mdd.admin.validate.goods;

import com.mdd.admin.vo.goods.GoodsSkuVo;
import com.mdd.admin.vo.goods.SpecValueVo;
import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("商品创建参数")
public class GoodsCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "请输入商品名称")
    @Length(max = 64, message = "商品名称不能大于64个字符")
    @ApiModelProperty(value = "商品名称")
    private String name;

    @NotEmpty(message = "请输入商品货号")
    @Length(max = 32, message = "商品货号不能大于32个字符")
    @ApiModelProperty(value = "商品编码")
    private String code;

    @ApiModelProperty(value = "商品描述")
    private String description;

    @NotEmpty(message = "请上传商品轮播图")
    @ApiModelProperty(value = "商品轮播图")
    private List<String> goodsImage;

    @NotEmpty(message = "请选择商品分类")
    @ApiModelProperty(value = "商品分类")
    private List<Integer> categoryId;

    @IntegerContains(values = {0, 1})
    @ApiModelProperty(value = "视频开关: [0=关闭, 1=开始]")
    private Integer videoStatus;

    @IntegerContains(values = {1, 2})
    @ApiModelProperty(value = "视频来源: [1=视频素材库, 2=视频链接]")
    private Integer videoSource;

    @ApiModelProperty(value = "视频封面")
    private String videoCover;

    @ApiModelProperty(value = "商品视频")
    private String video;

    @NotNull(message = "请选择运费设置")
    @IntegerContains(values = {1,2}, message = "运费模板值错误")
    @ApiModelProperty(value = "运费类型: 1=包邮, 2=运费模板")
    private Integer expressType;

    @ApiModelProperty(value = "运费模板")
    private Integer expressTemplateId;

    @ApiModelProperty(value = "库存预警")
    private Integer stockWarning = 0;

    @ApiModelProperty(value = "虚拟销量")
    private Integer virtualSalesNum = 0;

    @ApiModelProperty(value = "虚拟点击")
    private Integer virtualClickNum = 0;

    @ApiModelProperty(value = "排序编号")
    private Integer sort = 0;

    @NotNull(message = "请选择商品规格")
    @IntegerContains(values = {1,2}, message = "商品规格值错误")
    @ApiModelProperty(value = "商品规格")
    private Integer specType;

    @NotNull(message = "请选择销售状态")
    @IntegerContains(values = {0,1}, message = "销售状态值错误")
    @ApiModelProperty(value = "销售状态")
    private Integer status;

    @ApiModelProperty(value = "商品内容")
    private String content;

    @ApiModelProperty(value = "规格值")
    private List<SpecValueVo> specValue;

    @ApiModelProperty(value = "规格值列表")
    private List<GoodsSkuVo> specValueList;

    @ApiModelProperty(value = "服务规格值列表")
    private List<GoodsSkuVo> serviceSpecValueList;

    @ApiModelProperty(value = "是否开启快递配送")
    private Integer isExpress;

    @ApiModelProperty(value = "是否开启上门自提")
    private Integer isSelffetch;

}
