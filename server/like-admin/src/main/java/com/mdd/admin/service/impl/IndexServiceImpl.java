package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.IIndexService;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.IndexVisit;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.goods.GoodsComment;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderAfter;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.OrderAfterEnum;
import com.mdd.common.enums.OrderEnum;
import com.mdd.common.mapper.IndexVisitMapper;
import com.mdd.common.mapper.goods.GoodsCommentMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.order.OrderAfterMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 主页服务实现类
 */
@Service
public class IndexServiceImpl implements IIndexService {

    @Resource
    OrderMapper orderMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    IndexVisitMapper indexVisitMapper;

    @Resource
    GoodsCommentMapper goodsCommentMapper;

    @Resource
    OrderAfterMapper orderAfterMapper;

    @Resource
    GoodsMapper goodsMapper;

    /**
     * 控制台数据
     *
     * @author fzr
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> console() {
        Map<String, Object> console = new LinkedHashMap<>();

        // 版本信息
        Map<String, Object> version = new LinkedHashMap<>();
        version.put("name", ConfigUtils.get("website", "name", "LikeAdmin-Java"));
        version.put("version", GlobalConfig.version);
        Map<String, String> channel = new LinkedHashMap<>();
        channel.put("website", "https://www.likeshop.cn/");
        channel.put("document", "https://www.likeshop.cn/doc");
        channel.put("gitee", "https://gitee.com/likeshop_gitee/likeshop");
        version.put("channel", channel);
        console.put("version", version);

        BigDecimal todaySales = orderMapper.sum("pay_money", new QueryWrapper<Order>()
                .ge("pay_time", TimeUtils.today().get(0))
                .le("pay_time", TimeUtils.today().get(1))
                .eq("pay_is", 1));

        long todayOrder = orderMapper.selectCount(new QueryWrapper<Order>()
                .ge("pay_time", TimeUtils.today().get(0))
                .le("pay_time", TimeUtils.today().get(1))
                .eq("pay_is", 1));

        long todayUsers = userMapper.selectCount(new QueryWrapper<User>()
                .ge("create_time", TimeUtils.today().get(0))
                .le("create_time", TimeUtils.today().get(1))
                .eq("is_delete", 0));

        long todayVisits = indexVisitMapper.sumByLong("visit", new QueryWrapper<IndexVisit>()
                .ge("create_time", TimeUtils.today().get(0))
                .le("create_time", TimeUtils.today().get(1)));

        // 今日数据
        long currTime = System.currentTimeMillis() / 1000;
        Map<String, Object> today = new LinkedHashMap<>();
        today.put("time", TimeUtils.timestampToDate(currTime));
        today.put("todaySales", todaySales);    // 销售金额
        today.put("todayOrder", todayOrder);    // 订单数量
        today.put("todayUsers", todayUsers);    // 新增用户
        today.put("todayVisits", todayVisits);  // 新增访问量
        console.put("today", today);

        long stayDeliver = orderMapper.selectCount(new QueryWrapper<Order>().eq("order_status", OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()));
        long stayReply = goodsCommentMapper.selectCount(new QueryWrapper<GoodsComment>().eq("is_reply", 0));
        long stayRefund = orderAfterMapper.selectCount(new QueryWrapper<OrderAfter>()
                .eq("after_status", OrderAfterEnum.AFTER_STATUS_ING.getCode())
                .eq("sub_status", OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getCode()));

        long warningStock = goodsMapper.selectCount(new QueryWrapper<Goods>()
                .eq("is_delete", 0)
                .apply("stock_warning > total_stock"));

        // 等待处理
        Map<String, Object> stay = new LinkedHashMap<>();
        stay.put("stayDeliver", stayDeliver);
        stay.put("stayRefund", stayRefund);
        stay.put("stayReply", stayReply);
        stay.put("warningStock", warningStock);
        console.put("stay", stay);

        // 趋势图
        List<String> date = new LinkedList<>();
        List<Long> visitsList = new LinkedList<>();
        List<BigDecimal> salesList = new LinkedList<>();
        List<String> monthDgoDate = TimeUtils.monthAgoDate(5);
        for (String m : monthDgoDate) {
            int year = Integer.parseInt(m.split("-")[0]);
            int month = Integer.parseInt(m.split("-")[1]);
            date.add(month + "月");
            long startTime = TimeUtils.dateToTimestamp(m);
            long endTime = TimeUtils.dateToTimestamp(TimeUtils.getLastDayOfMonth(year, month)) + 86400 - 1;

            // 访问量
            long visits = indexVisitMapper.sumByLong("visit", new QueryWrapper<IndexVisit>()
                    .ge("create_time", startTime)
                    .le("create_time", endTime));

            BigDecimal salesVolume = orderMapper.sum("pay_money", new QueryWrapper<Order>()
                    .ge("pay_time", startTime)
                    .le("pay_time", endTime)
                    .eq("pay_is", 1));

            visitsList.add(visits);
            salesList.add(salesVolume);
        }

        // 访客图表
        Map<String, Object> visitor = new LinkedHashMap<>();
        visitor.put("date", date);
        visitor.put("visits", visitsList);
        visitor.put("salesVolume", salesList);
        console.put("visitor", visitor);
        return console;
    }

    /**
     * 公共配置
     *
     * @author fzr
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> config() {
        Map<String, String> website   = ConfigUtils.get("website");
        String copyright = ConfigUtils.get("website", "copyright", "");

        String captchaStatus = YmlUtils.get("like.captcha.status");

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("loginCaptcha", StringUtils.isNotNull(captchaStatus) && captchaStatus.equals("true"));
        map.put("webName", website.getOrDefault("name", ""));
        map.put("webLogo", UrlUtils.toAbsoluteUrl(website.getOrDefault("logo", "")));
        map.put("webFavicon", UrlUtils.toAbsoluteUrl(website.getOrDefault("favicon", "")));
        map.put("webBackdrop", UrlUtils.toAbsoluteUrl(website.getOrDefault("backdrop", "")));
        map.put("ossDomain", UrlUtils.domain());
        map.put("copyright", ListUtils.stringToListAsMapStr(copyright));

        return map;
    }

    /**
     * 正版检测
     *
     * @author mjf
     * @return Map<String,Object>
     */
    @Override
    public Map<String, Object> checkAuth() {
        // 域名
        String domain = RequestUtils.domain();
        // 产品code
        String code = GlobalConfig.productCode;

        String requestUrl =  GlobalConfig.productUrl + "/api/version/productAuth?code=%s&domain=%s";
        String url = String.format(requestUrl, code, domain);
        String results = HttpUtils.sendGet(url);
        Map<String, String> responseMap = MapUtils.jsonToMap(results);

        if (responseMap != null && StringUtils.isNotNull(responseMap.get("data"))) {
            String responseData = responseMap.get("data");
            return MapUtils.jsonToMapAsObj(responseData);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", false);
        return resultMap;
    }


}
