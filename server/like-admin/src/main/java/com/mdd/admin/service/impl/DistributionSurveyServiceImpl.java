package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IDistributionSurveyService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.distribution.DistributionSurveyDataVo;
import com.mdd.admin.vo.distribution.DistributionSurveyEarningsVo;
import com.mdd.admin.vo.distribution.DistributionSurveyFansVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.distribution.Distribution;
import com.mdd.common.entity.distribution.DistributionOrder;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.DistributionEnum;
import com.mdd.common.mapper.distribution.DistributionMapper;
import com.mdd.common.mapper.distribution.DistributionOrderMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 分销数据服务实现类
 */
@Service
public class DistributionSurveyServiceImpl implements IDistributionSurveyService {

    @Resource
    UserMapper userMapper;

    @Resource
    DistributionMapper distributionMapper;

    @Resource
    DistributionOrderMapper distributionOrderMapper;

    /**
     * 数据概括
     *
     * @author mjf
     * @return DistributionSurveyDataVo
     */
    @Override
    public DistributionSurveyDataVo data() {
        // 今日入账佣金
        BigDecimal todayEarnings = distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("status", DistributionEnum.ORDER_RETURNED.getCode())
                .ge("create_time", TimeUtils.today().get(0))
                .le("create_time", TimeUtils.today().get(1)));

        // 今日待结算佣金
        BigDecimal todayWaitEarnings = distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("status", DistributionEnum.ORDER_UN_RETURNED.getCode())
                .ge("create_time", TimeUtils.today().get(0))
                .le("create_time", TimeUtils.today().get(1)));

        // 累计待结算佣金
        BigDecimal totalWaitEarnings = distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("status", DistributionEnum.ORDER_UN_RETURNED.getCode()));

        // 累计入账佣金
        BigDecimal totalEarnings = distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("status", DistributionEnum.ORDER_RETURNED.getCode()));

        // 分销商
        MPJQueryWrapper<Distribution> distributionMPJQueryWrapper = new MPJQueryWrapper<>();
        distributionMPJQueryWrapper.select("t.id");
        distributionMPJQueryWrapper.eq("t.is_delete", 0);
        distributionMPJQueryWrapper.eq("u.is_delete", 0);
        distributionMPJQueryWrapper.eq("u.is_close", 0);
        distributionMPJQueryWrapper.innerJoin("?_user u ON u.id = t.user_id".replace("?_",GlobalConfig.tablePrefix));
        Long distribution = distributionMapper.selectCount(distributionMPJQueryWrapper);

        MPJQueryWrapper<Distribution> distributionStoreMPJQueryWrapper = new MPJQueryWrapper<>();
        distributionStoreMPJQueryWrapper.select("t.id");
        distributionStoreMPJQueryWrapper.eq("t.is_distribution", 1);
        distributionStoreMPJQueryWrapper.eq("t.is_delete", 0);
        distributionStoreMPJQueryWrapper.eq("u.is_delete", 0);
        distributionStoreMPJQueryWrapper.eq("u.is_close", 0);
        distributionStoreMPJQueryWrapper.innerJoin("?_user u ON u.id = t.user_id".replace("?_",GlobalConfig.tablePrefix));
        Long distributionStore = distributionMapper.selectCount(distributionStoreMPJQueryWrapper);

        // 分销商占比
        double percent = (double) distributionStore / distribution * 100;
        DecimalFormat df = new DecimalFormat("#.00");
        String distributionRadio = df.format(percent);

        DistributionSurveyDataVo vo = new DistributionSurveyDataVo();
        vo.setTodayEarnings(todayEarnings);
        vo.setTodayWaitEarnings(todayWaitEarnings);
        vo.setTotalEarnings(totalEarnings);
        vo.setTotalWaitEarnings(totalWaitEarnings);
        vo.setDistributionStore(distributionStore);
        vo.setDistributionRatio(distributionRadio);
        return vo;
    }

    /**
     * 分销商收入排行
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @return PageResult<DistributionSurveyEarningsVo>
     */
    @Override
    public PageResult<DistributionSurveyEarningsVo> topEarnings(PageValidate pageValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<DistributionOrder> mpjQueryWrapper = new MPJQueryWrapper<DistributionOrder>()
                .leftJoin("?_user U ON U.id = t.user_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_distribution D ON D.user_id = t.user_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("t.status", DistributionEnum.ORDER_RETURNED.getCode())
                .select("sum(t.earnings) as total_earnings, t.user_id, U.avatar, U.nickname")
                .groupBy("t.user_id")
                .orderByDesc("total_earnings");

        IPage<DistributionSurveyEarningsVo> iPage = distributionOrderMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                DistributionSurveyEarningsVo.class,
                mpjQueryWrapper);

        for (DistributionSurveyEarningsVo vo : iPage.getRecords()) {
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
        }

        return PageResult.iPageHandle(iPage);
    }

    /**
     * 分销商粉丝排行
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @return PageResult<DistributionSurveyFansVo>
     */
    @Override
    public PageResult<DistributionSurveyFansVo> topFans(PageValidate pageValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<User> mpjQueryWrapper = new MPJQueryWrapper<User>()
                .selectAll(User.class)
                .select("t.id as user_id")
                .select("(SELECT COUNT(id) FROM ?_user WHERE first_leader = t.id OR second_leader = t.id) as total_fans".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_distribution D ON D.user_id = t.id".replace("?_", GlobalConfig.tablePrefix))
                .eq("D.is_distribution", 1)
                .orderByDesc("total_fans")
                .last("limit 50");

        List<DistributionSurveyFansVo> list = userMapper.selectJoinList(DistributionSurveyFansVo.class, mpjQueryWrapper);

        int startPosition = (pageNo - 1) * pageSize;
        Stream<DistributionSurveyFansVo> stream = list.stream().skip(startPosition).limit(pageSize);
        List<DistributionSurveyFansVo> pageList = stream.collect(Collectors.toList());

        for (DistributionSurveyFansVo vo : pageList) {
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
        }

        PageResult<DistributionSurveyFansVo> pageResult = new PageResult<>();
        pageResult.setCount((long) list.size());
        pageResult.setPageNo(pageNo);
        pageResult.setPageSize(pageSize);
        pageResult.setLists(pageList);
        return pageResult;
    }
}
