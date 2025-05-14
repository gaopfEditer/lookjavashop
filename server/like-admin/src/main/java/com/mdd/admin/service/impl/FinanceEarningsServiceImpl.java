package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IFinanceEarningsService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.finance.FinanceEarningsSearchValidate;
import com.mdd.admin.vo.finance.FinanceEarningsListVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.log.LogEarnings;
import com.mdd.common.enums.LogEarningsEnum;
import com.mdd.common.mapper.log.LogEarningsMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户佣金记录服务实现类
 */
@Service
public class FinanceEarningsServiceImpl implements IFinanceEarningsService {

    @Resource
    LogEarningsMapper logEarningsMapper;

    @Override
    public PageResult<FinanceEarningsListVo> list(PageValidate pageValidate, FinanceEarningsSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<LogEarnings> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.selectAll(LogEarnings.class)
                .select("U.id as user_id,U.sn as user_sn,U.nickname,U.avatar")
                .leftJoin("?_user U ON U.id=t.user_id".replace("?_", GlobalConfig.tablePrefix))
                .orderByDesc("id");

        logEarningsMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "=:type@change_type:int",
                "datetime:startTime-endTime@t.create_time:str",
        });

        if (StringUtils.isNotEmpty(searchValidate.getKeyword())) {
            String keyword = searchValidate.getKeyword();
            mpjQueryWrapper.nested(wq->wq
                    .like("U.nickname", keyword).or()
                    .like("U.sn", keyword).or()
                    .like("U.mobile", keyword));
        }

        IPage<FinanceEarningsListVo> iPage = logEarningsMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                FinanceEarningsListVo.class,
                mpjQueryWrapper);

        for (FinanceEarningsListVo vo : iPage.getRecords()) {
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            vo.setChangeType(LogEarningsEnum.getMsgByCode(Integer.parseInt(vo.getChangeType())));
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
        }

        Map<String, Object> extend = new LinkedHashMap<>();
        extend.put("changeType", LogEarningsEnum.getTypeList());

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), extend);
    }

}
