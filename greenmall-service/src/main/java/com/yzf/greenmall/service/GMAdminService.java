package com.yzf.greenmall.service;

import com.yzf.greenmall.common.CodecUtils;
import com.yzf.greenmall.entity.GMAdmin;
import com.yzf.greenmall.mapper.GMAdminMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:GMAdminService
 * @author:leo_yuzhao
 * @date:2020/11/22
 */
@Service
@Transactional
public class GMAdminService {

    @Autowired
    private GMAdminMapper gmAdminMapper;

    /**
     * 根据昵称查找admin,并比对密码返回数据
     *
     * @param admin
     * @return
     */
    public GMAdmin findGMAdmin(GMAdmin admin) {
        // 1，验证数据
        if (StringUtils.isBlank(admin.getNickName()) || StringUtils.isBlank(admin.getPassword())) {
            return null;
        }
        // 2，根据昵称查找admin
        GMAdmin record = new GMAdmin();
        record.setNickName(admin.getNickName());
        GMAdmin gmAdmin = gmAdminMapper.selectOne(record);
        if (gmAdmin == null) {
            return null;
        }
        // 3，比对密码
        if (!gmAdmin.getPassword().equals(CodecUtils.md5Hex(admin.getPassword(), gmAdmin.getSalt()))) {
            return null;
        }
        return gmAdmin;
    }
}
