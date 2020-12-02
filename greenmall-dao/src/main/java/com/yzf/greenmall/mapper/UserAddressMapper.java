package com.yzf.greenmall.mapper;

import com.yzf.greenmall.entity.UserAddress;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:UserAddressMapper
 * @author:leo_yuzhao
 * @date:2020/12/1
 */
public interface UserAddressMapper extends Mapper<UserAddress> {
    /**
     * 取消默认地址根据账户id
     *
     * @param accountId
     */
    @Update("update tb_address SET is_default = 0 WHERE account_id = #{accountId}")
    void cancelDefault(@Param("accountId") Long accountId);
}
