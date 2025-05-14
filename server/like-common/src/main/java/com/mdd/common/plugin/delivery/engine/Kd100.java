package com.mdd.common.plugin.delivery.engine;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.kuaidi100.sdk.api.QueryTrack;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.request.QueryTrackParam;
import com.kuaidi100.sdk.request.QueryTrackReq;
import com.kuaidi100.sdk.utils.SignUtils;
import com.mdd.common.exception.OperateException;
import com.mdd.common.plugin.delivery.vo.KdQueryTrackParam;
import com.mdd.common.plugin.delivery.vo.KdTrackResultVo;
import com.mdd.common.util.StringUtils;

import java.util.*;


public class Kd100 {

    /**
     * 快递实时查询
     *
     * @author fzr
     * @param customer 授权码
     * @param key 密钥
     * @param kdQueryTrackParam 参数
     */
    public List<KdTrackResultVo> queryTrack(String customer, String key, KdQueryTrackParam kdQueryTrackParam) {
        try {
            QueryTrackReq queryTrackReq = new QueryTrackReq();
            QueryTrackParam queryTrackParam = new QueryTrackParam();
            queryTrackParam.setCom(kdQueryTrackParam.getCom());
            queryTrackParam.setNum(kdQueryTrackParam.getNum());
            queryTrackParam.setPhone(kdQueryTrackParam.getPhone());
            queryTrackParam.setFrom(kdQueryTrackParam.getFrom());
            queryTrackParam.setTo(kdQueryTrackParam.getTo());
            queryTrackParam.setResultv2("0");
            queryTrackParam.setShow("0");
            queryTrackParam.setOrder("desc");
            String param = new Gson().toJson(queryTrackParam);

            queryTrackReq.setParam(param);
            queryTrackReq.setCustomer(customer);
            queryTrackReq.setSign(SignUtils.querySign(param, key, customer));

            IBaseClient baseClient = new QueryTrack();
            HttpResult httpResult = baseClient.execute(queryTrackReq);
            JSONObject resultBody = JSONObject.parseObject(httpResult.getBody());

            // 查询失败
            if (StringUtils.isNotEmpty(resultBody.getString("result"))
                    && resultBody.getString("result").equals("false")) {
                throw new Exception(resultBody.getString("message"));
            }

            JSONArray jsonArrayResult = resultBody.getJSONArray("data");

            List<KdTrackResultVo> trackResult = new LinkedList<>();
            if (!jsonArrayResult.isEmpty()) {
                for (int i = 0; i < jsonArrayResult.size(); i++) {
                    JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                    KdTrackResultVo vo = new KdTrackResultVo();
                    vo.setTime(jsonObject.get("time").toString());
                    vo.setContent(jsonObject.get("context").toString());
                    trackResult.add(vo);
                }
            }
            return trackResult;

        } catch (Exception e) {
            throw new OperateException(e.getMessage());
        }
    }


}
