package com.yzf.greenmall.service;

import com.github.pagehelper.PageHelper;
import com.yzf.greenmall.common.LayuiPage;
import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.NumberCalUtil;
import com.yzf.greenmall.common.QueryPage;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.Order;
import com.yzf.greenmall.entity.Refund;
import com.yzf.greenmall.mapper.RefundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.sql.Ref;
import java.util.Date;
import java.util.List;

/**
 * @description:退款Service
 * @author:leo_yuzhao
 * @date:2020/12/8
 */
@Service
@Transactional
public class RefundService {

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;


    /**
     * 用户提交退款申请
     *
     * @param refund
     * @param loginUser
     * @return
     */
    public Message userRefund(Refund refund, UserInfo loginUser) {

        // 1，账户id
        refund.setAccountId(loginUser.getId());
        // 2，总价
        refund.setTotalPrice(NumberCalUtil.multiply(refund.getUnitPrice(), refund.getNum().doubleValue()));
        // 3，申请时间
        refund.setCreateTime(new Date());
        // 4，根据订单id和商品id更改状态
        orderService.refundGoods(refund.getOrderId(), refund.getGoodsId());
        // 5，给用户退款
        userService.transferAccounts(loginUser.getId(), refund.getTotalPrice());

        refundMapper.insertSelective(refund);
        return new Message(1, "");

    }

    /**
     * 查找用户提交的退款申请
     *
     * @param loginUser
     * @return
     */
    public List<Refund> findRefund(UserInfo loginUser) {
        Example example = new Example(Refund.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("accountId", loginUser.getId());
        example.setOrderByClause("create_time desc");
        List<Refund> refundList = refundMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(refundList)) {
            return null;
        }

        refundList.forEach(refund -> {
            // 1,加载商品图片和名称
            Object goodsInfos[] = goodsService.findImageTitlePrice(refund.getGoodsId());
            refund.setGoodsImage(goodsInfos[0].toString().split(",")[0]);
            refund.setGoodsTitle(goodsInfos[1].toString());
            // 2,加载退款状态
            refund.setRefundState(orderService.findRefundState(refund.getOrderId(), refund.getGoodsId()));
        });

        return refundList;
    }

    /**
     * 分页查询退款申请
     *
     * @param queryPage
     * @return
     */
    public LayuiPage<Refund> findOrderByPage(QueryPage<Refund> queryPage) {

        if (CollectionUtils.isEmpty(queryPage.getQueryMap())) {
            queryPage.setQueryMap(Refund.originalQueryMap());
        }
        Example example = queryPage.generateExample(Refund.class, false);
        // 2，分页查询
        PageHelper.startPage(queryPage.getPage(), queryPage.getLimit());
        List<Refund> refundList = refundMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(refundList)) {
            return new LayuiPage<Refund>();
        }

        // 3，封装分页信息，并返回
        return new LayuiPage<Refund>().initLayuiPage(refundList);
    }
}
