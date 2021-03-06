package com.yzf.greenmall.service;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yzf.greenmall.bo.Comment;
import com.yzf.greenmall.bo.EvaluateBo;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.PageResult;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.*;
import com.yzf.greenmall.mapper.EvaluateMapper;
import com.yzf.greenmall.mapper.GoodsDetailMapper;
import com.yzf.greenmall.mapper.GoodsMapper;
import com.yzf.greenmall.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description:EvaluateService
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Service
@Transactional
public class EvaluateService {

    @Autowired
    private EvaluateMapper evaluateMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    /**
     * 加载评论
     *
     * @param id 商品id
     * @return
     */
    public EvaluateBo loadEvaluate(Long id) {

        // 1，统计该商品的所有评价
        Long totalEvaluate = evaluateMapper.getTotalEvaluate(id);

        Long goodEvaluateNum = 0l;
        Long commonEvaluateNum = 0l;
        Long badEvaluateNum = 0l;
        double goodEvaluateDegree = 0.0;
        PageResult<Comment> evaluatePage = new PageResult<Comment>(0l, new ArrayList<>());

        if (totalEvaluate > 0) {
            // 2，统计好评数量
            goodEvaluateNum = evaluateMapper.getGoodEvaluateNum(id);
            // 3，统计中评数量
            commonEvaluateNum = evaluateMapper.getCommonEvaluateNum(id);
            // 4，统计差评数量
            badEvaluateNum = evaluateMapper.getBadEvaluateNum(id);
            // 5，得出好评度
            BigDecimal b = new BigDecimal(goodEvaluateNum.doubleValue() / totalEvaluate);
            goodEvaluateDegree = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            // 6，获取评论数据
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("goodsId", id + "");
            QueryPage queryPage = new QueryPage(1, 5, queryMap);
            evaluatePage = this.findEvaluateByPage(queryPage);
        }


        /*
            private Long totalEvaluate; // 全部评价数量
            private Long goodEvaluateNum; // 好评数量
            private Long commonEvaluateNum; // 中评数量
            private Long badEvaluateNum; // 差评数量
            private Double goodEvaluateDegree; // 好评度
            private PageResult<Comment> pageResult; // 分页评价列表
       */

        EvaluateBo evaluateBo = new EvaluateBo();
        evaluateBo.setTotalEvaluate(totalEvaluate);
        evaluateBo.setGoodEvaluateNum(goodEvaluateNum);
        evaluateBo.setCommonEvaluateNum(commonEvaluateNum);
        evaluateBo.setBadEvaluateNum(badEvaluateNum);
        evaluateBo.setGoodEvaluateDegree(goodEvaluateDegree);
        evaluateBo.setPageResult(evaluatePage);

        return evaluateBo;
    }

    /**
     * 分页查询评论
     *
     * @param // 评价类型 1：好评 2：中评 3：差评 4：全评价
     * @return
     */
    public PageResult<Comment> findEvaluateByPage(QueryPage queryPage) {
        Example example = new Example(Evaluate.class);
        Example.Criteria criteria = example.createCriteria();
        // 添加商品id条件
        criteria.andEqualTo("goodsId", "" + queryPage.getQueryMap().get("goodsId"));
        // 只能查询有效评论
        criteria.andEqualTo("valid", Evaluate.VALID_YES);
        // 按时间降序排列
        example.setOrderByClause("create_time desc");
        Integer eType = 6;
        if (!CollectionUtils.isEmpty(queryPage.getQueryMap())) {
            Object reqEType = queryPage.getQueryMap().get("eType");
            if (!StringUtils.isEmpty(reqEType)) {
                eType = Integer.parseInt(reqEType.toString());
            }
        }
        if (eType == 1) {
            // 1：好评
            criteria.andBetween("score", 4, 5);
        } else if (eType == 2) {
            // 2：中评
            criteria.andBetween("score", 3, 3);
        } else if (eType == 3) {
            // 3：差评
            criteria.andBetween("score", 1, 2);
        } else {
            // 4：全评价
            criteria.andBetween("score", 1, 5);
        }
        PageHelper.startPage(queryPage.getPage(), queryPage.getLimit());
        // 数据库中查询到的评论数据
        List<Evaluate> evaluateList = evaluateMapper.selectByExample(example);
        // 前端展示评论数据
        List<Comment> comments = new ArrayList<>();
        for (Evaluate evaluate : evaluateList) {
            Comment comment = new Comment();
            // 1，查询用户名称
            User user = userMapper.selectByPrimaryKey(evaluate.getAccountId());
            String userNickName = user.getNickName();
            comment.setUserNickName(StrUtil.hide(userNickName, 1, userNickName.length() - 1));
            // 2，评论时间
            comment.setDate(evaluate.getCreateTime());
            // 3，评论文字：xxxx
            comment.setContent(evaluate.getContent());
            // 4，评论图片
            comment.setImages(evaluate.getImages());
            // 5，用户头像
            comment.setUserPortrait(user.getPortrait());
            comments.add(comment);
        }

        // 分页信息对象
        PageInfo<Evaluate> pageInfo = new PageInfo(evaluateList);

        // 封装分页数据对象
        return new PageResult<Comment>(pageInfo.getTotal(), comments);
    }

    /**
     * 添加测试数据
     */
    public void addTestData() {
        /*
            5	28	4	5	完美	http://image.greenmall.com/group1/M00/00/00/wKjmgF-tM0yAOf5XAABiHYHHMos819.jpg
            2020-11-26 17:09:55
         */

        for (int i = 0; i < 100; i++) {
            Evaluate evaluate = new Evaluate();
            evaluate.setOrderId(5l);
            evaluate.setGoodsId(28l);
            evaluate.setAccountId(4l);
            evaluate.setScore(5);
            evaluate.setContent("完美" + (i + 1));
            evaluate.setImages("http://image.greenmall.com/group1/M00/00/00/wKjmgF-tM0yAOf5XAABiHYHHMos819.jpg");
            evaluate.setCreateTime(new Date());
            evaluateMapper.insert(evaluate);
        }
    }

    /**
     * 计算商品好评度
     *
     * @return
     */
    public Double findGoodsEvaluateDegree(Long goodsId) {

        // 1,查询所有的评论数
        Long totalEvaluate = evaluateMapper.getTotalEvaluate(goodsId);
        if (totalEvaluate == 0) {
            return 0d;
        }
        // 2，查询好评数量
        Long goodEvaluateNum = evaluateMapper.getGoodEvaluateNum(goodsId);
        BigDecimal b = new BigDecimal(goodEvaluateNum.doubleValue() / totalEvaluate.doubleValue());
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 根据订单id判读订单是否已经评价
     *
     * @param orderId
     * @return
     */
    public boolean isOrderEvaluated(Long orderId) {
        Evaluate record = new Evaluate();
        record.setOrderId(orderId);
        int i = evaluateMapper.selectCount(record);
        if (i > 0) {
            return true;
        }
        return false;
    }

    /**
     * 发表评论
     *
     * @param list
     * @param loginUser
     * @return
     */
    public Message comment(List<Evaluate> list, UserInfo loginUser) {

        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("评论list为空，无法进行评论！");
        }

        list.forEach(item -> {
            // 设置评论用户
            item.setAccountId(loginUser.getId());

            // 设置评论时间
            item.setCreateTime(new Date());

            // 设置评论是否有效
            item.setValid(Evaluate.VALID_YES);

            // 存入数据库中
            evaluateMapper.insertSelective(item);

            // 更新索引库
            Long goodsId = item.getGoodsId();
            goodsService.updateGoodsSearch(goodsId);
        });

        return new Message(1, "");
    }

    /**
     * 根据商品Id 获得该商品的评分
     *
     * @param id
     * @return
     */
    public Integer getGoodsAvgScore(Long id) {
        Integer goodsAvgScore = evaluateMapper.getGoodsAvgScore(id);
        return goodsAvgScore == null ? 0 : goodsAvgScore;
    }

    /**
     * 根据用户加载评论
     *
     * @param loginUser
     * @return
     */
    public List<Evaluate> findComment(UserInfo loginUser) {

        // 1，根据用户ID查找评论
        Example example = new Example(Evaluate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("accountId", loginUser.getId());
        criteria.andEqualTo("valid", Evaluate.VALID_YES);
        example.setOrderByClause("create_time desc");
        List<Evaluate> list = evaluateMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        // 2，封装数据，商品图片，商品id
        for (Evaluate evaluate : list) {
            Long goodsId = evaluate.getGoodsId();
            Object[] infos = goodsService.findImageTitlePrice(goodsId);
            evaluate.setGoodsImage(((String) infos[0]).split(",")[0]);
            evaluate.setGoodsTitle((String) infos[1]);
        }

        return list;
    }

    /**
     * 根据id删除评论
     *
     * @param id 评论ID
     * @return
     */
    public Message deleteComment(Long id) {

        // 1，查找评论
        Evaluate evaluate = evaluateMapper.selectByPrimaryKey(id);
        if (evaluate == null) {
            return new Message(2, "根据ID删除评论：根据ID无法搜索到评论数据!");
        }
        // 2，删除
        evaluate.setValid(Evaluate.VALID_NO);
        evaluateMapper.updateByPrimaryKeySelective(evaluate);

        return new Message(1, "");
    }

    /**
     * 后端管理：分页查找评价
     *
     * @param queryPage
     * @return
     */
    public LayuiPage<Evaluate> findEvaluateByPageAdmin(QueryPage<Evaluate> queryPage) {
        if (CollectionUtils.isEmpty(queryPage.getQueryMap())) {
            queryPage.setQueryMap(Evaluate.originalQueryMap());
        }
        Example example = queryPage.generateExample(Evaluate.class, false);
        // 2，分页查询
        PageHelper.startPage(queryPage.getPage(), queryPage.getLimit());
        List<Evaluate> evaluateList = evaluateMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(evaluateList)) {
            return new LayuiPage<Evaluate>();
        }

        // 3，封装分页信息，并返回
        return new LayuiPage<Evaluate>().initLayuiPage(evaluateList);
    }

    /**
     * 删除或者撤销删除评论
     *
     * @param type       1：删除 0：撤销
     * @param evaluateId
     * @return
     */
    public Message evaluateValid(Integer type, Long evaluateId) {

        Evaluate evaluate = evaluateMapper.selectByPrimaryKey(evaluateId);
        if (evaluate == null) {
            throw new RuntimeException("根据评论ID删除或者撤销删除评论异常：根据评论ID无法查找到评论信息！");
        }

        if (type == 1) {
            evaluate.setValid(Evaluate.VALID_NO);
        } else if (type == 0) {
            evaluate.setValid(Evaluate.VALID_YES);
        }
        evaluateMapper.updateByPrimaryKeySelective(evaluate);
        return new Message(1, "");
    }
}

