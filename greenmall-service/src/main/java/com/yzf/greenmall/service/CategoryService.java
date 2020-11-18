package com.yzf.greenmall.service;

import com.github.pagehelper.PageHelper;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.yzf.greenmall.bo.CategoryTreeBo;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Category;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.mapper.CategoryMapper;
import com.yzf.greenmall.mapper.GoodsMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
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

    @Autowired
    private GoodsMapper goodsMapper;

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
        // 1，获取查询条件
        if (CollectionUtils.isEmpty(queryPage.getQueryMap())) {
            queryPage.setQueryMap(Category.originalQueryMap());
        }
        Example example = queryPage.generateExample(Category.class);
        // 2，分页查询
        PageHelper.startPage(queryPage.getPage(), queryPage.getLimit());
        List<Category> categoryList = categoryMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(categoryList)) {
            return new LayuiPage<Category>();
        }

        // 封装商品分类信息
        categoryList.forEach(category -> {
            loadAncient(category);
        });

        // 3，封装分页信息，并返回
        return new LayuiPage<Category>().initLayuiPage(categoryList);
    }

    /**
     * 加载当前节点的祖先节点
     *
     * @param category
     */
    public void loadAncient(Category category) {
        // 设置当前分类的父分类
        if (category.getIsParent() && category.getParentId() == 0) {
            // 一级分类
            category.setAncient("无");
        } else if (category.getIsParent()) {
            // 二级分类
            Category ancient = categoryMapper.selectByPrimaryKey(category.getParentId());
            category.setAncient(ancient.getName());
        } else {
            // 三级分类
            ArrayList<String> cNames = new ArrayList<>();
            Category cur = category;
            while (cur.getParentId() != 0) {
                // 获取当前分类的父分类
                Category ancient = categoryMapper.selectByPrimaryKey(cur.getParentId());
                cNames.add(ancient.getName());
                cur = ancient;
            }
            Collections.reverse(cNames);
            category.setAncient(StringUtils.join(cNames, ">"));
        }
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

    /**
     * 根据分类级别查询商品分类
     *
     * @param level
     * @return
     */
    public List<Category> findCategoryByLevel(Byte level) {
        Category record = new Category();
        record.setLevel(level);
        List<Category> categories = categoryMapper.select(record);
        return categories;
    }

    /**
     * 添加商品分类/编辑商品分类
     * {"name":"afafsd","level":"2","parentId":"1"}
     *
     * @param category
     */
    public void update(Category category) {
        if (category.getId() == null) {
            // 新增
            // 1，初始化isParent
            category.initIsParent();
            // 2，新增
            categoryMapper.insert(category);
        } else {
            // 编辑: 在编辑时一律不能修改【上级分类】【分类级别】
            category.setLevel(null);
            category.setParentId(null);
            categoryMapper.updateByPrimaryKeySelective(category);
        }
    }

    /**
     * 根据分类id查找分类
     *
     * @param id
     * @return
     */
    public Category findCategoryById(Long id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        return category;
    }

    /**
     * 判断分类名称是否重复
     *
     * @param name
     * @return
     */
    public boolean isNameRepeat(String name, Long id) {
        Category record = new Category();
        record.setName(name);
        int i = categoryMapper.selectCount(record);
        if (id == -1) {
            // 新增重复查询
            if (i <= 0) {
                return false;
            }
        } else {
            // 更新重复查询
            if (i > 0) {
                Category category = categoryMapper.selectByPrimaryKey(id);
                if (category == null) {
                    throw new RuntimeException("判断规格参数名称是否重复,查询id无效");
                }
                if (name.equals(category.getName())) {
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

        return true;
    }

    /**
     * 删除分类
     *
     * @param category
     * @return
     */
    public Message delete(Category category) {

        if (category.getId() == null) {
            throw new RuntimeException("需要删除的商品分类id为空，无法删进行删除。");
        }

        // 根据id查询category
        Category delCategory = categoryMapper.selectByPrimaryKey(category.getId());

        // 判断该分类是否为三级分类
        if (delCategory.getLevel() == 3) {
            // 三级分类：判断是否存在商品引用
            Goods record = new Goods();
            record.setCid3(delCategory.getId());
            int i = goodsMapper.selectCount(record);
            if (i > 0) {
                return new Message(Message.MESSAGE_STATE_SUCCESS, "该分类存在商品引用，无法删除。");
            } else {
                categoryMapper.deleteByPrimaryKey(delCategory.getId());
                return new Message(Message.MESSAGE_STATE_SUCCESS, "删除成功！");
            }
        } else {
            // 非三级分类：判断是否存在子分类
            Category record = new Category();
            record.setParentId(delCategory.getId());
            int i = categoryMapper.selectCount(record);
            if (i > 0) {
                return new Message(Message.MESSAGE_STATE_SUCCESS, "该分类下存在子分类，无法删除。");
            } else {
                categoryMapper.deleteByPrimaryKey(delCategory.getId());
                return new Message(Message.MESSAGE_STATE_SUCCESS, "删除成功！");
            }
        }
    }
}
