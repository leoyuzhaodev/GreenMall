package com.yzf.greenmall.service;

import com.github.pagehelper.PageHelper;
import com.yzf.greenmall.bo.GoodsBo;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.GoodsDetail;
import com.yzf.greenmall.mapper.GoodsDetailMapper;
import com.yzf.greenmall.mapper.GoodsMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.jnlp.FileSaveService;
import java.util.Arrays;
import java.util.List;

/**
 * @description:GoodsService
 * @author:leo_yuzhao
 * @date:2020/11/9
 */
@Service
@Transactional
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    @Autowired
    private CategoryService categoryService;

    // 日志
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsService.class);

    /**
     * 查询所有商品
     *
     * @return
     */
    public List<Goods> findAll() {
        return goodsMapper.selectAll();
    }

    /**
     * 添加商品
     *
     * @param goodsBo
     * @return
     */
    public void update(GoodsBo goodsBo) {
        if (goodsBo.getId() == null) {
            // 添加商品
            // 1，初始化商品相关参数
            Goods goods = goodsBo.generateGoods();
            // 2，保存商品
            // 当 i>0 表明数据已经存入数据库
            int i = goodsMapper.insertSelective(goods);
            if (i <= 0) {
                throw new RuntimeException("Goods数据保存失败");
            }
            // 3，保存商品详情
            goodsBo.setGoodsId(goods.getId());
            // 当 i>0 表明数据已经存入数据库
            goodsBo.setGoodsId(goods.getId());
            int j = goodsDetailMapper.insertSelective(goodsBo);
            if (j <= 0) {
                throw new RuntimeException("GoodsDetail数据保存失败");
            }
        } else {
            // 编辑商品
            Goods record = new Goods(goodsBo.getId());
            int i = goodsMapper.selectCount(record);
            if (i <= 0) {
                throw new RuntimeException("需要编辑的商品不存在，商品id：" + record.getId());
            } else {
                // 1，更新商品
                Goods goods = goodsBo.generateEditGoods();
                goodsMapper.updateByPrimaryKeySelective(goods);
                // 2，更新商品详情
                goodsDetailMapper.updateByPrimaryKeySelective(goodsBo);
            }
        }

    }

    /**
     * 根据商品id查询商品以及商品详情
     *
     * @param id
     * @return
     */
    public GoodsBo findGoodsBoById(Long id) {
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(id);
        return GoodsBo.generateGoodsBo(goods, goodsDetail);
    }

    /**
     * 分页查询商品
     *
     * @param queryPage
     * @return
     */
    public LayuiPage<Goods> findGoodsByPage(QueryPage queryPage) {

        // 1，获取查询条件
        if (CollectionUtils.isEmpty(queryPage.getQueryMap())) {
            queryPage.setQueryMap(Goods.originalQueryMap());
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
        return new LayuiPage<Goods>().initLayuiPage(goodsList);
    }

    /**
     * 删除商品
     *
     * @param id
     */
    public void delete(Long id) {
        if (id != null) {
            Goods record = new Goods(id);
            record.setValid(false);
            record.setSaleable(false);
            goodsMapper.updateByPrimaryKeySelective(record);
        }
    }

    /**
     * 撤销删除商品
     *
     * @param id
     */
    public void revocation(Long id) {
        if (id != null) {
            Goods record = new Goods(id);
            record.setValid(true);
            goodsMapper.updateByPrimaryKeySelective(record);
        }
    }

    /**
     * 上架商品
     *
     * @param id
     */
    public void goodsUp(Long id) {
        if (id != null) {
            Goods record = new Goods(id);
            record.setSaleable(true);
            record.setValid(true);
            goodsMapper.updateByPrimaryKeySelective(record);
        }
    }


    /**
     * 下架商品
     *
     * @param id
     */
    public void goodsDown(Long id) {
        if (id != null) {
            Goods record = new Goods(id);
            record.setSaleable(false);
            goodsMapper.updateByPrimaryKeySelective(record);
        }
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            delete(id);
        });
    }

    /**
     * 商品批量撤销删除
     *
     * @param ids
     */
    public void revocationBatch(List<Long> ids) {
        ids.forEach(id -> {
            revocation(id);
        });
    }

    /**
     * 商品批量上架
     *
     * @param ids
     */
    public void upBatch(List<Long> ids) {
        ids.forEach(id -> {
            goodsUp(id);
        });
    }

    /**
     * 商品批量下架
     *
     * @param ids
     * @return
     */
    public void downBatch(List<Long> ids) {
        ids.forEach(id -> {
            goodsDown(id);
        });
    }
}
