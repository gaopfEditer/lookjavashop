
package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IDistributionApplyService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionApplyAuditValidate;
import com.mdd.admin.validate.distribution.DistributionApplySearchValidate;
import com.mdd.admin.vo.distribution.DistributionApplyDetailVo;
import com.mdd.admin.vo.distribution.DistributionApplyListedVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.DevRegion;
import com.mdd.common.entity.distribution.Distribution;
import com.mdd.common.entity.distribution.DistributionApply;
import com.mdd.common.enums.DistributionEnum;
import com.mdd.common.mapper.DevRegionMapper;
import com.mdd.common.mapper.distribution.DistributionApplyMapper;
import com.mdd.common.mapper.distribution.DistributionMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 分销申请服务实现类
 */
@Service
public class DistributionApplyServiceImpl implements IDistributionApplyService {

    @Resource
    DistributionMapper distributionMapper;

    @Resource
    DistributionApplyMapper distributionApplyMapper;

    @Resource
    DevRegionMapper devRegionMapper;

    /**
     * 分销申请记录
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionApplySearchValidate
     * @return PageResult<DistributionApplyListedVo>
     */
    @Override
    public PageResult<DistributionApplyListedVo> list(PageValidate pageValidate, DistributionApplySearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();
        Integer applyStatus = searchValidate.getStatus();
        String userKeyword = searchValidate.getSearchUserKeyword();
        String inviterKeyword = searchValidate.getSearchInviterKeyword();

        MPJQueryWrapper<DistributionApply> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper
                .orderByDesc("t.id")
                .eq("t.is_delete", 0)
                .leftJoin("?_user U1 ON t.user_id=U1.id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_user U2 ON U1.inviter_id=U2.id".replace("?_", GlobalConfig.tablePrefix));

        // 用户搜索
        if (StringUtils.isNotBlank(userKeyword)) {
            mpjQueryWrapper.nested(wq -> wq
                    .like("U1.sn", userKeyword).or()
                    .like("U1.nickname", userKeyword).or()
                    .like("U1.mobile", userKeyword));
        }

        // 邀请人信息
        if (StringUtils.isNotBlank(inviterKeyword)) {
            mpjQueryWrapper.nested(wq -> wq
                    .like("U2.sn", inviterKeyword).or()
                    .like("U2.nickname", inviterKeyword).or()
                    .like("U2.mobile", inviterKeyword));
        }

        // 申请时间
        distributionApplyMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "datetime:startTime-endTime@t.create_time:str"
        });

        Map<String, Object> extend = new LinkedHashMap<>();
        MPJQueryWrapper<DistributionApply> allMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<DistributionApply> waitMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<DistributionApply> passMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<DistributionApply> refuseMpj = mpjQueryWrapper.clone();
        extend.put("all", distributionApplyMapper.selectCount(allMpj.select("t.id")));
        extend.put("wait", distributionApplyMapper.selectCount(waitMpj.select("t.id").eq("t.status", DistributionEnum.APPLY_STATUS_ING.getCode())));
        extend.put("pass", distributionApplyMapper.selectCount(passMpj.select("t.id").eq("t.status", DistributionEnum.APPLY_STATUS_SUCCESS.getCode())));
        extend.put("refuse", distributionApplyMapper.selectCount(refuseMpj.select("t.id").eq("t.status", DistributionEnum.APPLY_STATUS_FAIL.getCode())));

        // 申请状态
        if (StringUtils.isNotNull(applyStatus) && applyStatus > 0) {
            mpjQueryWrapper.eq("t.status", applyStatus);
        }

        mpjQueryWrapper
                .selectAll(DistributionApply.class)
                .select("U1.sn,U1.nickname,U1.avatar, U1.inviter_id")
                .select("U2.nickname as inviter_name");

        IPage<DistributionApplyListedVo> iPage = distributionApplyMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                DistributionApplyListedVo.class,
                mpjQueryWrapper);

        for (DistributionApplyListedVo vo : iPage.getRecords()) {
            vo.setStatusMsg(DistributionEnum.getApplyStatusMsg(vo.getStatus()));
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), extend);
    }

    /**
     * 分销申请详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionApplyDetailVo
     */
    @Override
    public DistributionApplyDetailVo detail(Integer id) {
        DistributionApplyDetailVo vo = distributionApplyMapper.selectJoinOne(
                DistributionApplyDetailVo.class,
                new MPJQueryWrapper<DistributionApply>()
                        .eq("t.is_delete", 0)
                        .eq("t.id", id)
                        .selectAll(DistributionApply.class)
                        .select("U1.sn,U1.nickname,U1.avatar,U2.nickname as inviter")
                        .leftJoin("?_user U1 ON t.user_id=U1.id".replace("?_", GlobalConfig.tablePrefix))
                        .leftJoin("?_user U2 ON U1.inviter_id=U2.id".replace("?_", GlobalConfig.tablePrefix))
                        .last("limit 1"));

        vo.setStatusMsg(DistributionEnum.getApplyStatusMsg(vo.getStatus()));
        vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
        vo.setRegion(getRegion(vo.getProvince(), vo.getCity(), vo.getDistrict()));
        vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
        if (StringUtils.isNotEmpty(vo.getAuditTime()) && Long.parseLong(vo.getAuditTime()) > 0) {
            vo.setAuditTime(TimeUtils.timestampToDate(vo.getAuditTime()));
        } else {
            vo.setAuditTime("");
        }
        return vo;
    }

    /**
     * 审核分销申请
     *
     * @author mjf
     * @param auditValidate DistributionApplyAuditValidate
     */
    @Override
    public void audit(DistributionApplyAuditValidate auditValidate) {
        Integer applyId = auditValidate.getId();
        Integer status = auditValidate.getStatus();
        String auditRemark = auditValidate.getAuditRemark();

        // 申请记录
        DistributionApply distributionApply = distributionApplyMapper.selectById(applyId);
        Assert.notNull(distributionApply, "申请信息不存在");

        if (status.equals(1)) {
            distributionApply.setStatus(DistributionEnum.APPLY_STATUS_SUCCESS.getCode());
        } else {
            distributionApply.setStatus(DistributionEnum.APPLY_STATUS_FAIL.getCode());
        }

        // 更新申请记录
        if (StringUtils.isNotBlank(auditRemark)) {
            distributionApply.setAuditRemark(auditRemark);
        }
        distributionApply.setUpdateTime(System.currentTimeMillis() / 1000);
        distributionApply.setAuditTime(System.currentTimeMillis() / 1000);
        distributionApplyMapper.updateById(distributionApply);

        // 更新用户分销基础信息
        if (status.equals(1)) {
            Distribution distrinution = new Distribution();
            distrinution.setIsDistribution(1);
            distrinution.setDistributionTime(System.currentTimeMillis() / 1000);
            distrinution.setUpdateTime(System.currentTimeMillis() / 1000);
            distributionMapper.update(distrinution, new QueryWrapper<Distribution>()
                    .eq("is_delete", 0)
                    .eq("user_id", distributionApply.getUserId()));
        }
    }

    /**
     * 获取省市区
     *
     * @author mjf
     * @param provinceId Integer
     * @param cityId Integer
     * @param districtId Integer
     * @return String
     */
    @Override
    public String getRegion(Integer provinceId, Integer cityId, Integer districtId) {

        List<DevRegion> regionList = devRegionMapper.selectList(new QueryWrapper<DevRegion>()
                .in("id", Arrays.asList(provinceId, cityId, districtId)));

        String provinceName = "";
        String cityName = "";
        String districtName = "";

        for (DevRegion devRegion : regionList) {
            if (1 == devRegion.getLevel()) {
                provinceName = devRegion.getName();
            }
            if (2 == devRegion.getLevel()) {
                cityName = devRegion.getName();
            }
            if (3 == devRegion.getLevel()) {
                districtName = devRegion.getName();
            }
        }
        return provinceName + cityName + districtName;
    }

}
