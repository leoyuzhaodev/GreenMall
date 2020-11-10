package com.yzf.greenmall.service;

import com.yzf.greenmall.entity.Param;
import com.yzf.greenmall.mapper.ParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:ParamService
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Service
@Transactional
public class ParamService {
    @Autowired
    private ParamMapper paramMapper;

    /**
     * 根据【三级分类】查找规格参数
     *
     * @param cid3
     * @return
     */
    public List<Param> findParamsByCid(Long cid3) {
        List<Param> params = paramMapper.findParamsByCid(cid3);
        return params;
    }
}
