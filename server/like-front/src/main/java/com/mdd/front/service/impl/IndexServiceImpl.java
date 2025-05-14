package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.mdd.common.entity.distribution.DistributionConfig;
import com.mdd.common.mapper.distribution.DistributionConfigMapper;
import com.mdd.front.service.IGoodsService;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.IndexVisit;
import com.mdd.common.entity.article.Article;
import com.mdd.common.entity.decorate.DecoratePage;
import com.mdd.common.entity.decorate.DecorateTabbar;
import com.mdd.common.entity.setting.HotSearch;
import com.mdd.common.mapper.IndexVisitMapper;
import com.mdd.common.mapper.article.ArticleMapper;
import com.mdd.common.mapper.decorate.DecoratePageMapper;
import com.mdd.common.mapper.decorate.DecorateTabbarMapper;
import com.mdd.common.mapper.setting.HotSearchMapper;
import com.mdd.common.util.*;
import com.mdd.front.service.IIndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 首页服务实现类
 */
@Service
public class IndexServiceImpl implements IIndexService {

    @Resource
    DecoratePageMapper decoratePageMapper;

    @Resource
    DecorateTabbarMapper decorateTabbarMapper;

    @Resource
    HotSearchMapper hotSearchMapper;

    @Resource
    ArticleMapper articleMapper;

    @Resource
    IndexVisitMapper indexVisitMapper;

    @Resource
    DistributionConfigMapper distributionConfigMapper;

    @Resource
    IGoodsService iGoodsService;

    /**
     * 首页
     *
     * @author fzr
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> index() {
        Map<String, Object> response = new LinkedHashMap<>();
        DecoratePage decoratePage = decoratePageMapper.selectOne(
                new QueryWrapper<DecoratePage>()
                    .eq("id", 1)
                    .last("limit 1"));

        List<Map<String, Object>> articleList = new LinkedList<>();
        List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                .eq("is_show", 1)
                .eq("is_delete", 0)
                .orderByDesc("id")
                .last("limit 3"));

        for (Article article : articles) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", article.getId());
            map.put("title", article.getTitle());
            map.put("intro", article.getIntro());
            map.put("summary", article.getSummary());
            map.put("image", UrlUtils.toAbsoluteUrl(article.getImage()));
            map.put("author", article.getAuthor());
            map.put("visit", article.getVisit());
            map.put("createTime", TimeUtils.timestampToDate(article.getCreateTime()));
            articleList.add(map);
        }

        response.put("domain", UrlUtils.domain());
        response.put("pages", decoratePage.getPageData());
        response.put("article", articleList);
        response.put("hot_goods", iGoodsService.limitList("hot", 4));
        response.put("new_goods", iGoodsService.limitList("new", 20));
        return response;
    }

    /**
     * 装修
     *
     * @author fzr
     * @param id 主键
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> decorate(Integer id) {
        DecoratePage decoratePage = decoratePageMapper.selectOne(
                new QueryWrapper<DecoratePage>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(decoratePage, "数据不存在!");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("type", decoratePage.getPageType());
        response.put("name", decoratePage.getPageName());
        response.put("pages", decoratePage.getPageData());
        return response;
    }

    /**
     * 配置
     *
     * @author fzr
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> config() {
        Map<String, Object> response = new LinkedHashMap<>();

        // 底部导航
        List<Map<String, String>> tabs = new LinkedList<>();
        List<DecorateTabbar> decorateTabbars = decorateTabbarMapper.selectList(new QueryWrapper<DecorateTabbar>().orderByAsc("id"));
        for (DecorateTabbar tab: decorateTabbars) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("name", tab.getName());
            map.put("selected", UrlUtils.toAbsoluteUrl(tab.getSelected()));
            map.put("unselected", UrlUtils.toAbsoluteUrl(tab.getUnselected()));
            map.put("link", tab.getLink());
            tabs.add(map);
        }

        // 导航颜色
        String tabbarStyle = ConfigUtils.get("tabbar", "style", "{}");

        // 登录配置
        Map<String, Object> loginMap = new LinkedHashMap<>();
        Map<String, String> loginConfig = ConfigUtils.get("login");
        loginMap.put("loginWay", ListUtils.stringToListAsInt(loginConfig.getOrDefault("loginWay", ""), ","));
        loginMap.put("forceBindMobile", Integer.parseInt(loginConfig.getOrDefault("forceBindMobile", "0")));
        loginMap.put("openAgreement", Integer.parseInt(loginConfig.getOrDefault("openAgreement", "0")));
        loginMap.put("openOtherAuth", Integer.parseInt(loginConfig.getOrDefault("openOtherAuth", "0")));
        loginMap.put("autoLoginAuth", ListUtils.stringToListAsInt(loginConfig.getOrDefault("autoLoginAuth", ""), ","));

        // 网址信息
        Map<String, Object> websiteMap = new LinkedHashMap<>();
        Map<String, String> websiteConfig = ConfigUtils.get("website");
        websiteMap.put("name", websiteConfig.getOrDefault("shopName", "LikeAdmin"));
        websiteMap.put("logo", UrlUtils.toAbsoluteUrl(websiteConfig.getOrDefault("shopLogo", "")));

        // H5配置
        Map<String, Object> h5Map = new LinkedHashMap<>();
        Map<String, String> h5Config = ConfigUtils.get("h5_channel");
        h5Map.put("status", Integer.parseInt(h5Config.getOrDefault("status", "0")));
        h5Map.put("close", Integer.parseInt(h5Config.getOrDefault("close", "0")));
        h5Map.put("url", h5Config.getOrDefault("url", "0"));
        h5Map.put("accessLink", RequestUtils.uri());

        //备案信息
        String copyright = websiteConfig.getOrDefault("copyright", "[]");
        List<Map<String, String>> copyrightMap = ListUtils.stringToListAsMapStr(copyright);


        // 响应数据
        response.put("version", GlobalConfig.version);
        response.put("domain", UrlUtils.domain());
        response.put("style", MapUtils.jsonToMap(tabbarStyle));
        response.put("tabbar", tabs);
        response.put("login", loginMap);
        response.put("website", websiteMap);
        response.put("h5", h5Map);
        response.put("copyright",copyrightMap);
        return response;
    }

    /**
     * 政策
     *
     * @author fzr
     * @param type 类型 service=服务协议,privacy=隐私协议,distribution=分销协议
     * @return Map<String, Object>
     */
    @Override
    public Map<String, String> policy(String type) {
        if (type.equals("distribution")) {
            List<DistributionConfig> list = distributionConfigMapper.selectList(
                    new QueryWrapper<DistributionConfig>()
                            .eq("is_delete", 0));

            Map<String, String> distributionMap = new LinkedHashMap<>();
            String content = "";
            for (DistributionConfig config : list) {
                if (config.getName().equals("protocolContent")) {
                    content = StringUtils.isNotNull(config.getValue()) ? config.getValue().trim() : "";
                    break;
                }
            }
            distributionMap.put("name", "分销协议");
            distributionMap.put("content", content);
            return distributionMap;
        }
        Map<String, String> map = ConfigUtils.getMap("protocol", type);
        if (map == null) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("name", "");
            m.put("content", "");
            return m;
        }
        return map;
    }

    /**
     * 热搜
     *
     * @author fzr
     * @return List<String>
     */
    @Override
    public List<String> hotSearch() {
        String isHotSearch = ConfigUtils.get("search", "isHotSearch", "0");

        List<String> list = new LinkedList<>();
        if (Integer.parseInt(isHotSearch) == 1) {
            List<HotSearch> hotSearches = hotSearchMapper.selectList(
                    new QueryWrapper<HotSearch>()
                        .orderByDesc(Arrays.asList("sort", "id")));

            for (HotSearch hotSearch : hotSearches) {
                list.add(hotSearch.getName());
            }
        }

        return list;
    }

    /**
     * 用户访问量统计
     *
     * @author mjf
     * @param terminal Integer
     */
    @Override
    public void visit(Integer terminal) {
        // ip
        String ip = IpUtils.getIpAddress();
        // 当天时间
        List<Long> todayTime = TimeUtils.today();
        // 一个ip一个终端一天只生成一个记录
        IndexVisit indexVisit = indexVisitMapper.selectOne(new QueryWrapper<IndexVisit>()
                .eq("ip", ip)
                .eq("terminal", terminal)
                .ge("create_time", todayTime.get(0))
                .le("create_time", todayTime.get(1))
                .last("limit 1"));

        if (indexVisit != null) {
            indexVisit.setVisit(indexVisit.getVisit() + 1);
            indexVisit.setUpdateTime(System.currentTimeMillis() / 1000);
            indexVisitMapper.updateById(indexVisit);
        } else {
            IndexVisit indexVisitData = new IndexVisit();
            indexVisitData.setIp(ip);
            indexVisitData.setTerminal(terminal);
            indexVisitData.setVisit(1);
            indexVisitData.setCreateTime(System.currentTimeMillis() / 1000);
            indexVisitData.setUpdateTime(System.currentTimeMillis() / 1000);
            indexVisitMapper.insert(indexVisitData);
        }
    }

}
