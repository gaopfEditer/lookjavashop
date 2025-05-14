package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.marketing.MarketingCouponCreateValidate;
import com.mdd.admin.validate.marketing.MarketingCouponRecordValidate;
import com.mdd.admin.validate.marketing.MarketingCouponSearchValidate;
import com.mdd.admin.validate.marketing.MarketingCouponUpdateValidate;
import com.mdd.admin.vo.marketing.MarketingCouponDetailVo;
import com.mdd.admin.vo.marketing.MarketingCouponListedVo;
import com.mdd.admin.vo.marketing.MarketingCouponRecordVo;
import com.mdd.admin.vo.marketing.MarketingCouponSelectVo;
import com.mdd.common.core.PageResult;

import java.util.List;

/**
 * 营销优惠券服务接口类
 */
public interface IMarketingCouponService {

    /**
     * 优惠券选择
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<MarketingCouponSelectVo>
     */
    PageResult<MarketingCouponSelectVo> select(PageValidate pageValidate, MarketingCouponSearchValidate searchValidate);

    /**
     * 优惠券列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<MarketingCouponListedVo>
     */
    PageResult<MarketingCouponListedVo> list(PageValidate pageValidate, MarketingCouponSearchValidate searchValidate);

    /**
     * 优惠券信息
     *
     * @author fzr
     * @param id 主键
     * @return MarketingCouponListedVo
     */
    MarketingCouponListedVo info(Integer id);

    /**
     * 优惠券详情
     *
     * @author fzr
     * @param id 主键
     * @return MarketingCouponDetailVo
     */
    MarketingCouponDetailVo detail(Integer id);

    /**
     * 优惠券新增
     *
     * @author fzr
     * @param createValidate 参数
     */
    void add(MarketingCouponCreateValidate createValidate);

    /**
     * 优惠券编辑
     *
     * @author fzr
     * @param updateValidate 参数
     */
    void edit(MarketingCouponUpdateValidate updateValidate);

    /**
     * 优惠券删除
     *
     * @author fzr
     * @param id 主键
     */
    void del(Integer id);

    /**
     * 优惠券命名
     *
     * @author fzr
     * @param id 主键
     * @param name 命名
     * @param sendTotal 数量
     */
    void rename(Integer id, String name, Integer sendTotal);

//    /**
//     * 优惠券发布
//     *
//     * @author fzr
//     * @param id 主键
//     */
//    void release(Integer id);

    /**
     * 优惠券开始
     *
     * @author fzr
     * @param id 主键
     */
    void start(Integer id);

    /**
     * 优惠券结束
     *
     * @author fzr
     * @param id 主键
     */
    void end(Integer id);

    /**
     * 优惠券记录
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<MarketingCouponRecordVo>
     */
    PageResult<MarketingCouponRecordVo> record(PageValidate pageValidate, MarketingCouponRecordValidate searchValidate);

    /**
     * 优惠券作废
     *
     * @author fzr
     * @param ids 记录ID
     */
    void repeal(List<Integer> ids);

}
