package com.mdd.admin.crontab;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.entity.coupon.CouponList;
import com.mdd.common.enums.CouponEnum;
import com.mdd.common.mapper.coupon.CouponListMapper;
import com.mdd.common.mapper.coupon.CouponMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component("couponListEndJob")
public class CouponListEndJob {

    @Resource
    CouponListMapper couponListMapper;

    // 用户优惠券过期
    public void handle() {
        try {
            long currTime = System.currentTimeMillis() / 1000;
            List<CouponList> list = couponListMapper.selectList(new QueryWrapper<CouponList>()
                    .eq("status", CouponEnum.USE_STATUS_NOT.getCode())
                    .le("invalid_time", currTime));

            if (!list.isEmpty()) {
                for (CouponList couponList : list) {
                    couponList.setStatus(CouponEnum.USE_STATUS_EXPIRE.getCode());
                    couponList.setUpdateTime(System.currentTimeMillis() / 1000);
                    couponListMapper.updateById(couponList);
                }
            }
        } catch (Exception ignored) {
        }
    }

}
