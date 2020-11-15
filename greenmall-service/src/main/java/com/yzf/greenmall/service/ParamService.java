package com.yzf.greenmall.service;

import com.github.pagehelper.PageHelper;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Category;
import com.yzf.greenmall.entity.Param;
import com.yzf.greenmall.mapper.CategoryMapper;
import com.yzf.greenmall.mapper.ParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private CategoryMapper categoryMapper;

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

    /**
     * 分页查询规格参数
     *
     * @param queryPage
     * @return
     */
    public LayuiPage<Param> findParamByPage(QueryPage queryPage) {

        // 1，初始化查询条件
        // 是否要进行多表查询的标识
        Long categoryId = null;
        Map queryMap = queryPage.getQueryMap();
        if (!CollectionUtils.isEmpty(queryMap)) {
            if (!StringUtils.isEmpty(queryMap.get("categoryId"))) {
                String searchCid = queryMap.get("categoryId").toString();
                if (!StringUtils.isEmpty(searchCid)) {
                    queryMap.remove("categoryId");
                    categoryId = Long.parseLong(searchCid);
                }
            }
        } else {
            queryPage.setQueryMap(Param.originalQueryMap());
        }

        // 2，执行分页
        PageHelper.startPage(queryPage.getPage(), queryPage.getLimit());
        Example example = queryPage.generateExample(Param.class);
        if (categoryId != null) {
            // 根据分类id查询到规格参数的id
            List<Long> pIds = paramMapper.findParamsIdByCid(categoryId);
            Example example1 = new Example(Category.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", pIds);
            example.and(criteria);
        }
        List<Param> params = paramMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(params)) {
            return new LayuiPage<Param>();
        }

        // 加载规格参数的所属分类名称
        params.forEach(param -> {
            loadCategoryName(param);
        });

        // 3，封装分页信息，并返回
        return new LayuiPage<Param>().initLayuiPage(params);
    }

    /**
     * 加载参数所属分类的名称集合
     *
     * @param param
     */
    private void loadCategoryName(Param param) {
        // 1，根据参数id查询与之绑定的所有分类的名字
        List<String> categoryNames = categoryMapper.findCategoryNamesByPramsId(param.getId());
        if (!CollectionUtils.isEmpty(categoryNames)) {
            param.setCNames(org.apache.commons.lang3.StringUtils.join(categoryNames, " "));
        } else {
            param.setCNames("暂无");
        }
    }

}
