package com.yzf.greenmall.common;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @description:后台返回前台的消息对象
 * @author:leo_yuzhao
 * @date:2020/11/10
 */
@Data
public class Message {

    public static final int MESSAGE_STATE_SUCCESS = 1;
    public static final int MESSAGE_STATE_ERROR = 0;

    // 状态码
    private int state;

    // 消息
    private String info;

    public Message() {
    }

    public Message(int state, String info) {
        this.state = state;
        this.info = info;
    }

    /**
     * 生成 ResponseEntity<Message>
     * @param state
     * @param info
     * @return
     */
    public static ResponseEntity<Message> generateResponseEntity(int state, String info) {
        if (state == MESSAGE_STATE_SUCCESS) {
            return new ResponseEntity(new Message(Message.MESSAGE_STATE_SUCCESS, info),
                    HttpStatus.CREATED);
        } else if (state == MESSAGE_STATE_ERROR) {
            return new ResponseEntity(new Message(Message.MESSAGE_STATE_ERROR, info),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }
}
