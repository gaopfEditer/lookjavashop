package com.mdd.common.mapper.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.user.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends IBaseMapper<User> {

    /**
     * 生成邀请码
     *
     * @author mjf
     * @param field String
     * @return String
     */
    default String randMakeOrderSn(String field) {
        // 字母
        List<String> letterAll = new LinkedList<>();
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            String s = String.valueOf(ch);
            if (Arrays.asList("I", "O").contains(s)) {
                continue;
            }
            letterAll.add(s);
        }

        // 数字
        for (char ch = '2'; ch <= '9'; ch++) {
            String s = String.valueOf(ch);
            letterAll.add(s);
        }

        StringBuilder randCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            randCode.append(letterAll.get(random.nextInt(letterAll.size())));
        }

        String code = randCode.toString();
        while (true) {
            User userModel = this.selectOne(
                    new QueryWrapper<User>()
                            .select("id")
                            .eq(field, code)
                            .last("limit 1"));
            if (userModel == null) {
                break;
            } else {
                StringBuilder randCodeRand = new StringBuilder();
                Random random2 = new Random();
                for (int i = 0; i < 4; i++) {
                    randCodeRand.append(letterAll.get(random2.nextInt(letterAll.size())));
                }
                code = randCodeRand.toString();
            }
        }
        return code;
    }
}
