package com.yzf.greenmall.listener;

import com.yzf.greenmall.service.GoodsSearchService;
import com.yzf.greenmall.service.GoodsService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:GoodsListener
 * @author:leo_yuzhao
 * @date:2020/11/23
 */
@Component
public class GoodsListener {

    @Autowired
    private GoodsSearchService goodsSearchService;

    @RabbitListener(bindings = @QueueBinding(
            // 指定消息队列名称
            value = @Queue(value = "greenmall.goods.queue", durable = "true"),
            // 指定交换机
            exchange = @Exchange(value = "greenmall.goods.exchange", ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            // 指定 key
            key = {"goods.update.goods"}
    ))
    public void listenSms(Long goodsId) {
        this.goodsSearchService.updateSearchGoods(goodsId);
    }

}
