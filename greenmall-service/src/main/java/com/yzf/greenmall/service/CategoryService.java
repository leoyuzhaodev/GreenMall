package com.yzf.greenmall.service;

import com.yzf.greenmall.entity.Category;
import com.yzf.greenmall.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:CategoryService
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父id查询分类
     * @param fId
     * @return
     */
    public List<Category> findCategoryByFID(Long fId) {
        Category record = new Category();
        record.setParentId(fId);
        List<Category> categories = categoryMapper.select(record);
        return categories;
    }

}
