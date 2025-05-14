package com.mdd.common.mapper.coupon;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.entity.coupon.CouponList;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 优惠券领取Mapper
 */
@Mapper
public interface CouponListMapper extends IBaseMapper<CouponList> {

    /**
     * 生成唯一流水号
     *
     * @author mjf
     * @param field String
     * @return String
     */
    default String randMakeOrderSn(String field) {
        // 生成字母表
        List<String> letterAll = new LinkedList<>();
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            String s = String.valueOf(ch);
            if (Arrays.asList("O", "I").contains(s)) {
                continue;
            }
            letterAll.add(s);
        }

        // 随机取4个字母
        StringBuilder letterArray = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <= 3; i++) {
            letterArray.append(letterAll.get(random.nextInt(letterAll.size())));
        }

        // 生成字母表
        List<String> numAll = new LinkedList<>();
        for (char ch = '2'; ch <= '9'; ch++) {
            numAll.add(String.valueOf(ch));
        }

        // 随机取6位数字
        StringBuilder numArray = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            numArray.append(numAll.get(random.nextInt(numAll.size())));
        }

        String sn = letterArray.append(numArray).toString();
        while (true) {
            CouponList snModel = this.selectOne(
                    new QueryWrapper<CouponList>()
                            .select("id")
                            .eq(field, sn)
                            .last("limit 1"));
            if (snModel == null) {
                break;
            }
        }
        return sn;
    }

}
