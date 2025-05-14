package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.IDistributionConfigService;
import com.mdd.admin.validate.distribution.DistributionConfigValidate;
import com.mdd.admin.vo.distribution.DistributionConfigVo;
import com.mdd.common.entity.distribution.DistributionConfig;
import com.mdd.common.mapper.distribution.DistributionConfigMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DistributionConfigServiceImpl implements IDistributionConfigService {

    @Resource
    DistributionConfigMapper distributionConfigMapper;

    /**
     * 分销配置详情
     *
     * @author fzr
     * @return DistributionConfigVo
     */
    @Override
    public DistributionConfigVo detail() {
        QueryWrapper<DistributionConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        List<DistributionConfig> list = distributionConfigMapper.selectList(queryWrapper);

        Map<String, String> map = new LinkedHashMap<>();
        for (DistributionConfig config : list) {
            map.put(config.getName(), StringUtils.isNotNull(config.getValue()) ? config.getValue().trim() : null);
        }

        DistributionConfigVo vo = new DistributionConfigVo();
        vo.setOpen(Integer.parseInt(map.getOrDefault("open", "0")));
        vo.setLevel(Integer.parseInt(map.getOrDefault("level", "1")));
        vo.setIsSelfRebate(Integer.parseInt(map.getOrDefault("isSelfRebate", "0")));
        vo.setIsEarningsShow(Integer.parseInt(map.getOrDefault("isEarningsShow", "0")));
        vo.setIsEarningsScope(Integer.parseInt(map.getOrDefault("isEarningsScope", "1")));
        vo.setOpenCondition(Integer.parseInt(map.getOrDefault("openCondition", "2")));
        vo.setProtocolShow(Integer.parseInt(map.getOrDefault("protocolShow", "0")));
        vo.setProtocolContent(map.getOrDefault("protocolContent", ""));
        vo.setApplyImage(UrlUtils.toAbsoluteUrl(map.getOrDefault("applyImage", "/api/static/distribution_bg.png")));
        vo.setEarningsCalMethod(Integer.parseInt(map.getOrDefault("earningsCalMethod", "1")));
        vo.setSettlementTiming(Integer.parseInt(map.getOrDefault("settlementTiming", "1")));
        vo.setSettlementTime(map.getOrDefault("settlementTime", "0"));
        return vo;
    }

    /**
     * 分销配置保存
     *
     * @author fzr
     * @param configValidate 参数
     */
    @Override
    public void save(DistributionConfigValidate configValidate) {
        DistributionConfig config = new DistributionConfig();
        List<String> fields = Arrays.asList(
                      "open", "level", "isSelfRebate", "isEarningsShow", "isEarningsScope",
                      "openCondition", "protocolShow", "protocolContent", "applyImage",
                      "settlementTiming", "settlementTime");

        for (String field : fields) {
            switch (field) {
                case "open":
                    config.setValue(configValidate.getOpen().toString());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "open"));
                    break;
                case "level":
                    config.setValue(configValidate.getLevel().toString());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "level"));
                    break;
                case "isSelfRebate":
                    config.setValue(configValidate.getIsSelfRebate().toString());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "isSelfRebate"));
                    break;
                case "isEarningsShow":
                    config.setValue(configValidate.getIsEarningsShow().toString());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "isEarningsShow"));
                    break;
                case "isEarningsScope":
                    config.setValue(configValidate.getIsEarningsScope().toString());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "isEarningsScope"));
                    break;
                case "openCondition":
                    config.setValue(configValidate.getOpenCondition().toString());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "openCondition"));
                    break;
                case "protocolShow":
                    config.setValue(configValidate.getProtocolShow().toString());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "protocolShow"));
                    break;
                case "protocolContent":
                    config.setValue(configValidate.getProtocolContent());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "protocolContent"));
                    break;
                case "applyImage":
                    config.setValue(UrlUtils.toRelativeUrl(configValidate.getApplyImage()));
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "applyImage"));
                    break;
                case "settlementTiming":
                    config.setValue(configValidate.getSettlementTiming().toString());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "settlementTiming"));
                    break;
                case "settlementTime":
                    config.setValue(configValidate.getSettlementTime().toString());
                    distributionConfigMapper.update(config, new QueryWrapper<DistributionConfig>().eq("name", "settlementTime"));
                    break;
            }
        }
    }

}
