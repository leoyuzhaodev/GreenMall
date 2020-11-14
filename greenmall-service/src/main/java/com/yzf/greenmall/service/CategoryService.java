package com.yzf.greenmall.service;

import com.github.pagehelper.PageHelper;
import com.yzf.greenmall.bo.CategoryTreeBo;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Category;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.mapper.CategoryMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     *
     * @param fId
     * @return
     */
    public List<Category> findCategoryByFID(Long fId) {
        Category record = new Category();
        record.setParentId(fId);
        List<Category> categories = categoryMapper.select(record);
        return categories;
    }

    /**
     * 根据多个分类id，查询多个分类名称
     *
     * @param idList
     * @return
     */
    public List<String> findCategoriesNameByIds(List<Long> idList) {
        List<Category> categories = categoryMapper.selectByIdList(idList);
        List<String> categoryNames = categories.stream().map(category -> {
            return category.getName();
        }).collect(Collectors.toList());
        return categoryNames;
    }

    /**
     * 分页查询商品分类
     *
     * @param queryPage
     * @return
     */
    public LayuiPage<Category> findCategoryByPage(QueryPage queryPage) {
       /* // 1，获取查询条件
        if (CollectionUtils.isEmpty(queryPage.getQueryMap())) {
            queryPage.setQueryMap(Category.originalQueryMap());
        }
        Example example = queryPage.generateExample(Goods.class);
        // 2，分页查询
        PageHelper.startPage(queryPage.getPage(), queryPage.getLimit());
        List<Goods> goodsList = goodsMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(goodsList)) {
            return new LayuiPage<Goods>();
        }

        // 封装商品分类信息
        goodsList.forEach(goods -> {
            // 根据多个分类id查询分类
            List<String> categoriesNames = categoryService.findCategoriesNameByIds(Arrays.asList(goods.getCid1(), goods.getCid2(), goods.getCid3()));
            String[] names = categoriesNames.toArray(new String[3]);
            // 设置三级分类
            goods.setCategory(StringUtils.join(names, ">"));
        });

        // 3，封装分页信息，并返回
        return new LayuiPage<Goods>().initLayuiPage(goodsList);*/
        return null;
    }

    /**
     * 加载分类树
     *
     * @return
     */
    public List<CategoryTreeBo> loadCategoryTreeBo() {
        Category record = new Category();
        record.setParentId(0L);
        List<Category> fathers = categoryMapper.select(record);
        if (CollectionUtils.isEmpty(fathers)) {
            return null;
        }
        List<CategoryTreeBo> categoryTreeBos = generateCategoryTree(fathers);
        return categoryTreeBos;
    }

    /**
     * 递归加载子分类
     *
     * @param categories
     * @return
     */
    public List<CategoryTreeBo> generateCategoryTree(List<Category> categories) {

        if (CollectionUtils.isEmpty(categories)) {
            return null;
        }

        List<CategoryTreeBo> categoryTreeBos = new ArrayList<>();
        categories.forEach(category -> {
            CategoryTreeBo categoryTreeBo = new CategoryTreeBo(category.getName(), category.getId());
            categoryTreeBos.add(categoryTreeBo);
        });

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            if (category.getIsParent() == true) {
                Category record = new Category();
                record.setParentId(category.getId());
                List<Category> categoryList = categoryMapper.select(record);
                categoryTreeBos.get(i).setChildren(generateCategoryTree(categoryList));
            } else {
                break;
            }
        }

        return categoryTreeBos;
    }

}
