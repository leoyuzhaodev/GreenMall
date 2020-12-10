package com.yzf.greenmall.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.yzf.greenmall.bo.EvaluateBo;
import com.yzf.greenmall.bo.GoodsBo;
import com.yzf.greenmall.bo.GoodsIntroduction;
import com.yzf.greenmall.bo.ParamBo;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.entity.*;
import com.yzf.greenmall.mapper.GoodsDetailMapper;
import com.yzf.greenmall.mapper.GoodsMapper;
import com.yzf.greenmall.mapper.OrderDetailMapper;
import com.yzf.greenmall.mapper.ParamMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.jnlp.FileSaveService;
import java.io.IOException;
import java.util.*;

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

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ParamMapper paramMapper;

    @Autowired
    private EvaluateService evaluateService;

    @Autowired
    private SalesVolumeService salesVolumeService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

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
        // 记录操作商品id，方便 rabbitMq 发送消息，elasticSearch 更新索引库
        Long goodsId = null;
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
            goodsId = goods.getId();
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
                goodsId = record.getId();
            }
        }

        // 发送消息
        updateGoodsSearch(goodsId);

    }

    /**
     * 发送消息更新索引库数据
     *
     * @param goodsId
     */
    public void updateGoodsSearch(Long goodsId) {
        this.amqpTemplate.convertAndSend("greenmall.goods.exchange", "goods.update.goods", goodsId);
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
            // 设置三级分类
            goods.setCategory(loadGoodsCategory(goods.getCid1(), goods.getCid2(), goods.getCid3()));
            // 设置库存
            Long stock = goodsDetailMapper.findStockByGoodsId(goods.getId());
            goods.setStock(stock == null ? 0 : stock);
        });

        // 3，封装分页信息，并返回
        return new LayuiPage<Goods>().initLayuiPage(goodsList);
    }

    /**
     * 查询商品的三级分类
     *
     * @param cid1
     * @param cid2
     * @param cid3
     * @return
     */
    private String loadGoodsCategory(Long cid1, Long cid2, Long cid3) {
        // 根据多个分类id查询分类
        List<String> categoriesNames = categoryService.findCategoriesNameByIds(Arrays.asList(cid1, cid2, cid3));
        String[] names = categoriesNames.toArray(new String[3]);
        return StringUtils.join(names, ">");
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
            // 发送消息
            updateGoodsSearch(id);
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
            // 发送消息
            updateGoodsSearch(id);
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
            // 发送消息
            updateGoodsSearch(id);
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
            // 发送消息
            updateGoodsSearch(id);
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

    /**
     * 根据商品id查询商品详情
     *
     * @param id 商品id
     * @return
     */
    public GoodsIntroduction findIntroductionById(Long id) throws IOException {

        // 1，根据id查询商品
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        if (goods == null) {
            return null;
        }

        // 2，根据商品id查询商品详情
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(id);

        // 3，加载规格参数
        List<Map<String, String>> params = loadParams(goodsDetail.getParams());

        // 4，加载评价信息
        EvaluateBo evaluate = evaluateService.loadEvaluate(id);

        // 5，加载商品月销量
        Calendar calendar = Calendar.getInstance();
        Long monthSalesVolume = salesVolumeService.getGoodsSVByMonthAndYear(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                goods.getId());

        // 6，加载商品总销量
        Long allSalesVolume = salesVolumeService.getGoodsAllSalesVolume(goods.getId());

        // 7，构建 GoodsIntroduction
        GoodsIntroduction goodsIntroduction = new GoodsIntroduction();
        goodsIntroduction.setId(goods.getId());
        goodsIntroduction.setImages(goodsDetail.getImages());
        goodsIntroduction.setTitle(goods.getTitle());
        goodsIntroduction.setCategory(loadGoodsCategory(goods.getCid1(), goods.getCid2(), goods.getCid3()));
        goodsIntroduction.setPrice(goodsDetail.getPrice());
        goodsIntroduction.setSubtitle(goods.getSubTitle());
        goodsIntroduction.setStock(goodsDetail.getStock());
        goodsIntroduction.setParams(params);
        goodsIntroduction.setGoodsDetail(goodsDetail.getDescription());
        goodsIntroduction.setAfterService(goodsDetail.getAfterService());
        goodsIntroduction.setSaleMonth(monthSalesVolume);
        goodsIntroduction.setSaleAll(allSalesVolume);
        goodsIntroduction.setCommentAll(evaluate.getTotalEvaluate());
        goodsIntroduction.setEvaluate(evaluate);

        return goodsIntroduction;
    }


    /**
     * 加载规格参数
     * [{"id":"1","value":"2"}] => [{"name":"规格参数名称","value":"2"}]
     *
     * @param params
     * @return
     */
    private List<Map<String, String>> loadParams(String params) throws IOException {
        if (StringUtils.isEmpty(params)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        List<ParamBo> paramBoList = mapper.readValue(params, new TypeReference<List<ParamBo>>() {
        });
        List<Map<String, String>> paramsVal = new ArrayList<>();
        for (ParamBo paramBo : paramBoList) {
            Param param = paramMapper.selectByPrimaryKey(paramBo.getId());
            Map<String, String> map = new HashMap<>();
            map.put("name", param.getName());
            map.put("value", paramBo.getValue());
            map.put("unit", param.getUnit());
            paramsVal.add(map);
        }
        return paramsVal;
    }


    /**
     * 判断商品是否可用
     *
     * @param goodsId
     * @return
     */
    public boolean isGoodsAvailable(Long goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        // 商品不存在
        if (goods == null) {
            return false;
        }
        // 商品下架/删除
        if (goods.getValid() == false || goods.getSaleable() == false) {
            return false;
        }
        return true;
    }

    /**
     * 判断商品是否可用
     *
     * @param goodsId
     * @return
     */
    public boolean isGoodsAvailable(Long goodsId, Integer num) {
        if (!this.isGoodsAvailable(goodsId)) {
            return false;
        }
        // 获取商品详情
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(goodsId);
        if (goodsDetail.getStock() - num < 0) {
            return false;
        }
        return true;
    }

    /**
     * 查找商品和商品详情经常使用的字段: 商品的图片，商品的名称，商品的价格
     *
     * @param goodsId
     * @return
     */
    public Object[] findImageTitlePrice(Long goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        if (goods == null) {
            return null;
        }
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(goodsId);
        return new Object[]{goodsDetail.getImages(), goods.getTitle(), goodsDetail.getPrice()};
    }

    /**
     * 根据订单ID更新库存
     *
     * @param orderId
     */
    public void updateStock(Long orderId) {

        // 0，验证数据
        if (orderId == null) {
            throw new RuntimeException("根据订单ID更新库存：订单ID为空");
        }

        // 1，根据订单Id 查询订单详情
        OrderDetail record = new OrderDetail();
        record.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(record);
        if (CollectionUtils.isEmpty(orderDetails)) {
            throw new RuntimeException("根据订单ID更新库存：根据订单ID无法查询到订单详情");
        }

        // 2，根据订单详情更新库存
        for (OrderDetail item : orderDetails) {
            GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(item.getGoodsId());
            // 减少库存
            goodsDetail.setStock(goodsDetail.getStock() - item.getNum());
            goodsDetailMapper.updateByPrimaryKeySelective(goodsDetail);
        }

    }
}
