package com.yzf.greenmall.service;

import com.github.pagehelper.PageHelper;
import com.yzf.greenmall.bo.CategoryTransferBo;
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

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private CategoryService categoryService;

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
                queryMap.remove("categoryId");
                if (!StringUtils.isEmpty(searchCid)) {
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
            if (!CollectionUtils.isEmpty(pIds)) {
                Example example1 = new Example(Category.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("id", pIds);
                example.and(criteria);
            }
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
            param.setCNames(org.apache.commons.lang3.StringUtils.join(categoryNames, "，"));
        } else {
            param.setCNames("暂无");
        }
    }

    /**
     * 根据规格参数id加载分类关联穿梭框数据
     *
     * @param paramId
     * @return
     */
    public CategoryTransferBo findParamsCategoryById(Long paramId) {

        // 1，查询所有的三级分类
        List<Category> categories = categoryService.findCategoryByLevel((byte) 3);
        // List<Map<String, Object>>
        List<Map<String, Object>> cbos = new ArrayList<>();
        categories.forEach(category -> {
            Map<String, Object> cMap = new HashMap<>();
            cMap.put("id", category.getId());
            cMap.put("name", category.getName());
            cbos.add(cMap);
        });
        // 2，判断 paramId 是否为 -1
        List<Long> checkedList = null;
        if (paramId != -1) {
            // 根据 paramId 查找关联的分类id
            checkedList = paramMapper.findCIdsByPId(paramId);
        }

        // 3，组装 CategoryTransferBo
        return new CategoryTransferBo(cbos, checkedList);
    }

    /**
     * 判断规格参数名称是否重复
     *
     * @param name
     * @return
     */
    public boolean isNameRepeat(String name, Long id) {
        Param record = new Param();
        record.setName(name);
        int i = paramMapper.selectCount(record);
        if (id == -1) {
            // 新增规格参数重复查询
            if (i <= 0) {
                return false;
            } else {
                return true;
            }
        } else {
            // 更新重复查询
            if (i > 0) {
                Param param = paramMapper.selectByPrimaryKey(id);
                if (param == null) {
                    throw new RuntimeException("判断规格参数名称是否重复,查询id无效");
                }
                if (name.equals(param.getName())) {
                    // 和自己名称重复不算重复
                    return false;
                } else {
                    // 重复
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 新增/更新规格参数
     *
     * @param param
     * @return
     */
    public void update(Param param) {
        // 设置默认单位
        if (StringUtils.isEmpty(param.getUnit())) {
            param.setUnit("无");
        }
        if (param.getId() == null) {
            // 新增规格参数
            // 1，新增规格参数
            int i = paramMapper.insertSelective(param);

            // 2，分类关联
            if (i > 0) {
                if (!CollectionUtils.isEmpty(param.getCategories())) {
                    for (Category category : param.getCategories()) {
                        paramMapper.insertParamCategory(param.getId(), category.getId());
                    }
                }

            } else {
                throw new RuntimeException("新增规格参数失败");
            }
        } else {
            // 更新规格参数
            paramMapper.updateByPrimaryKeySelective(param);
            // 更新关联关系
            // 1，查询原有关联关系 A，初始化现有关联关系
            List<Long> oldCIds = paramMapper.findCIdsByPId(param.getId());
            List<Long> newCIds = null;
            if (CollectionUtils.isEmpty(param.getCategories())) {
                newCIds = new ArrayList<>();
            } else {
                newCIds = param.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toList());
            }
            // 2，A - 现有关联关系 B = 要删除的
            List<Long> delList = (List<Long>) org.apache.commons.collections.CollectionUtils.subtract(oldCIds, newCIds);
            for (Long delCId : delList) {
                paramMapper.deleteParamCategory(param.getId(), delCId);
            }
            // 3，B - A = 要新增的
            List<Long> addList = (List<Long>) org.apache.commons.collections.CollectionUtils.subtract(newCIds, oldCIds);
            for (Long addCId : addList) {
                paramMapper.insertParamCategory(param.getId(), addCId);
            }
        }
    }


    /**
     * 根据id查找规格参数
     *
     * @param id
     * @return
     */
    public Param findParamById(Long id) {
        Param record = new Param();
        record.setId(id);
        Param param = paramMapper.selectByPrimaryKey(record);
        return param;
    }

    /**
     * 判断该分类是否可以取消与该规格参数的关联
     *
     * @param id  规格参数id
     * @param cid 分类id
     * @return
     */
    public boolean canDelCategory(Long id, Long cid) {
        String key = "%\"id\":\"" + id + "\"%";
        Long count = paramMapper.findGoodsCountByPIdAndCId(key, cid);
        if (count > 0) {
            return false;
        }
        return true;
    }

    /**
     * 删除规格参数
     *
     * @param id
     * @return
     */
    public boolean delete(Long id) {
        String key = "%\"id\":\"" + id + "\"%";
        // 查询该规格参数是否被商品引用
        Long count = paramMapper.findGoodsCountByPId(key);
        if (count > 0) {
            return false;
        }
        // 2，删除该规格参数与分类的关联
        List<Long> oldCIds = paramMapper.findCIdsByPId(id);
        for (Long cId : oldCIds) {
            paramMapper.deleteParamCategory(id, cId);
        }
        // 3，根据id删除规格参数
        paramMapper.deleteByPrimaryKey(id);
        return true;
    }
}
