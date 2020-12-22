package com.yzf.greenmall.service;

import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.UserAddress;
import com.yzf.greenmall.mapper.AdministrativeRegionMapper;
import com.yzf.greenmall.mapper.UserAddressMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

/**
 * @description:UserAddressService
 * @author:leo_yuzhao
 * @date:2020/12/1
 */
@Service
@Transactional
public class UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private AdministrativeRegionMapper arMapper;

    @Autowired
    private AdministrativeRegionService arService;

    /**
     * 更新或新增售后地址
     *
     * @param loginUser
     * @param userAddress
     * @return
     */
    public UserAddress update(UserInfo loginUser, UserAddress userAddress) {
        if (userAddress.getId() == null) {
            // 新增
            userAddress.setValid((byte) 1);
            userAddress.setAccountId(loginUser.getId());
            // 判断是否是新增的第一个地址如果是，就设置为默认
            UserAddress record = new UserAddress();
            record.setAccountId(loginUser.getId());
            // 有效地址
            record.setValid((byte) 1);
            int i = userAddressMapper.selectCount(record);
            if (i <= 0) {
                userAddress.setIsDefault((byte) 1);
            } else {
                userAddress.setIsDefault((byte) 0);
            }
            userAddressMapper.insertSelective(userAddress);
            // 加载全地址返回前段
            loadFullAddress(userAddress);
            return userAddress;
        } else {
            userAddressMapper.updateByPrimaryKeySelective(userAddress);
            return null;
        }
    }

    /**
     * 让某个地址失效
     *
     * @param loginUser
     * @param addressId
     */
    public void setUserAddressInvalid(UserInfo loginUser, Long addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setValid((byte) 0);
        userAddress.setId(addressId);
        // 清除默认地址标识
        userAddress.setIsDefault((byte) 0);
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    /**
     * 根据用户id查找地址
     *
     * @param loginUser
     * @return
     */
    public List<UserAddress> findUserAddress(UserInfo loginUser) {

        Example example = new Example(UserAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("accountId", loginUser.getId());
        // 排除无效地址
        criteria.andEqualTo("valid", 1);

        List<UserAddress> list = userAddressMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        list.forEach(address -> {
            loadFullAddress(address);
        });

        return list;
    }

    /**
     * 加载全地址
     *
     * @param userAddress
     */
    public void loadFullAddress(UserAddress userAddress) {
        // 1，根据三级行政区域id获取名称
        List<Long> ids = Arrays.asList(userAddress.getProvince(), userAddress.getCounty(), userAddress.getTown());
        List<String> names = arService.findNamesByIds(ids);
        // 2，拼接全地址字符串
        userAddress.setFullAddress(StringUtils.join(names, " ") + " " + userAddress.getAddressDetail());
    }

    /**
     * 根据用户id,以及地址id设置默认地址
     *
     * @param loginUser
     * @param id
     */
    public void setDefaultAddress(UserInfo loginUser, Long id) {
        // 1，将原默认地址改为非默认
        userAddressMapper.cancelDefault(loginUser.getId());
        // 2，将现非默认地址改为默认
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id);
        userAddress.setIsDefault((byte) 1);
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    /**
     * 根据地址id查找地址信息
     *
     * @param id
     * @return
     */
    public UserAddress findUserAddress(Long id) {
        if (id == null) {
            return null;
        }
        UserAddress userAddress = userAddressMapper.selectByPrimaryKey(id);
        if (userAddress == null) {
            return null;
        } else {
            loadFullAddress(userAddress);
        }
        return userAddress;
    }
}
