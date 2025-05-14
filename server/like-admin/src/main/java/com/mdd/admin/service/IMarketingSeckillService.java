package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillCreateValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillSearchValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillUpdateValidate;
import com.mdd.admin.vo.marketing.MarketingSeckillDetailVo;
import com.mdd.admin.vo.marketing.MarketingSeckillListedVo;
import com.mdd.common.core.PageResult;

public interface IMarketingSeckillService {

    /**
     * 秒杀活动列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<MarketingSeckillListedVo>
     */
    PageResult<MarketingSeckillListedVo> list(PageValidate pageValidate, MarketingSeckillSearchValidate searchValidate);

    /**
     * 秒杀活动详情
     *
     * @author fzr
     * @param id 主键
     * @return MarketingSeckillVo
     */
    MarketingSeckillDetailVo detail(Integer id);

    /**
     * 秒杀活动新增
     *
     * @author fzr
     * @param createValidate 参数
     */
    void add(MarketingSeckillCreateValidate createValidate);

    /**
     * 秒杀活动编辑
     *
     * @author fzr
     * @param updateValidate 参数
     */
    void edit(MarketingSeckillUpdateValidate updateValidate);

    /**
     * 秒杀活动删除
     *
     * @author fzr
     * @param id 主键
     */
    void del(Integer id);

    /**
     * 秒杀活动命名
     *
     * @param id 主键
     * @param name 活动名称
     */
    void rename(Integer id, String name);

    /**
     * 秒杀活动发布
     *
     * @author fzr
     * @param id 主键
     */
    void release(Integer id);

    /**
     * 秒杀活动开始
     *
     * @author fzr
     * @param id 主键
     */
    void start(Integer id);

    /**
     * 秒杀活动结束
     *
     * @author fzr
     * @param id 主键
     */
    void end(Integer id);

}
