package com.yzf.greenmall.listener;

import com.yzf.greenmall.service.MailService;
import com.yzf.greenmall.service.SmsService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:信息发送服务监听
 * @author:leo_yuzhao
 * @date:2020/11/20
 */
@Component
public class MailListener {

    @Autowired
    private MailService mailService;

    @RabbitListener(bindings = @QueueBinding(
            // 指定消息队列名称
            value = @Queue(value = "greenmall.mail.queue", durable = "true"),
            // 指定交换机
            exchange = @Exchange(value = "greenmall.mail.exchange", ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            // 指定 key
            key = {"mail.verify.code"}
    ))
    public void listenMail(Map<String, String> msg) {
        this.mailService.sendMessage(msg);
    }

}

