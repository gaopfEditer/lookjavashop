package com.mdd.common.plugin.delivery;

import com.mdd.common.plugin.delivery.engine.Kd100;
import com.mdd.common.plugin.delivery.engine.Kdniao;
import com.mdd.common.plugin.delivery.vo.KdQueryTrackParam;
import com.mdd.common.plugin.delivery.vo.KdTrackResultVo;
import com.mdd.common.util.ConfigUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DeliveryDriver {

    private final String engine;
    private final String requestType;
    private final String customer;
    private final String key;

    /**
     * 初始化
     */
    public DeliveryDriver() {
        this.engine = ConfigUtils.get("logistics", "engine", "");
        Map<String, String> config = ConfigUtils.getMap("logistics", this.engine);

        this.key = config.getOrDefault("key", "");
        this.customer = config.getOrDefault("customer", "");

        // 快递鸟专属: free=免费查询(1002), charge=收费查询(8001)
        String requestType = config.getOrDefault("requestType", "free");
        this.requestType = requestType.equals("free") ? "1002" : "8001";
    }

    /**
     * 查询实时物流
     *
     * @author fzr
     * @param kdQueryTrackParam 查询参数
     */
    public List<KdTrackResultVo> queryTrack(KdQueryTrackParam kdQueryTrackParam) {
        List<KdTrackResultVo> result = new LinkedList<>();
        switch (this.engine) {
            case "kd100":
                result = (new Kd100()).queryTrack(this.customer, this.key, kdQueryTrackParam);
                break;
            case "kdniao":
                result = (new Kdniao()).queryTrack(this.customer, this.key, this.requestType, kdQueryTrackParam);
                break;
        }
        return result;
    }

}
