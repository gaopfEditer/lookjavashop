package com.mdd.admin.crontab;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.enums.CouponEnum;
import com.mdd.common.mapper.coupon.CouponMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component("couponCloseJob")
public class CouponCloseJob {

    @Resource
    CouponMapper couponMapper;

    public void handle() {
        List<Coupon> couponList = new LinkedList<>();
        try {
            long currTime = System.currentTimeMillis() / 1000;
            couponList = couponMapper.selectList(new QueryWrapper<Coupon>()
                    .le("get_time_end", currTime)
                    .in("status",
                            Arrays.asList(
                                    CouponEnum.COUPON_STATUS_NOT.getCode(),
                                    CouponEnum.COUPON_STATUS_CONDUCT.getCode()
                            )
                    ));
        } catch (Exception ignored) {}

        for (Coupon coupon : couponList) {
            coupon.setStatus(CouponEnum.COUPON_STATUS_END.getCode());
            coupon.setUpdateTime(System.currentTimeMillis() / 1000);
            couponMapper.updateById(coupon);
        }
    }

}
