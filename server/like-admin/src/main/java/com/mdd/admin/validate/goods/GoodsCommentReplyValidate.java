package com.mdd.admin.validate.goods;

import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("评论回复参数")
public class GoodsCommentReplyValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "ID")
    private Integer id;

    @NotNull(message = "content参数缺失")
    @Length(max = 255, message = "回复内容不能超过255个字符")
    @ApiModelProperty(value = "回复内容")
    private String content;

}
