package com.yzf.greenmall.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzf.greenmall.bo.ParamBo;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
public class MyTest {

    @Test
    public  void fun1() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = "[{\"id\":\"1\",\"value\":\"2\"},{\"id\":\"2\",\"value\":\"湖北\"}]";
        List<ParamBo> paramBoList = mapper.readValue(jsonStr, new TypeReference<List<ParamBo>>() {});
        System.out.println(paramBoList.size());
    }

}
