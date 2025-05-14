package com.mdd.common.mapper.user;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.user.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户地址Mapper
 */
@Mapper
public interface UserAddressMapper extends IBaseMapper<UserAddress> {
}
