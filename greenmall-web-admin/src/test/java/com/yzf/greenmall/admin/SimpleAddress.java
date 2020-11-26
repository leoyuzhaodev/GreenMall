package com.yzf.greenmall.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzf.greenmall.GreenMallWebAdminApplication;
import com.yzf.greenmall.entity.AdministrativeRegion;
import com.yzf.greenmall.mapper.AdministrativeRegionMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @description:初始化中国行政区划数据
 * @author:leo_yuzhao
 * @date:2020/11/25
 */
public class SimpleAddress {

    List<AdministrativeRegion> lists = new ArrayList<>();


    @Test
    public void fun1() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(new File("D:/test/address.json"));
        list(jsonNode, 1, null);
        int count = 0;
        System.out.println(lists.size());
        System.out.println("id:" + lists.get(lists.size() - 1).getId() + ",name:" + lists.get(lists.size() - 1).getName());
    }

    public void list(JsonNode jsonNode, int level, String fCode) {
        int number = 0;
        if (level == 1) {
            number = 10;
        } else {
            number = 0;
        }
        if (jsonNode.isContainerNode() && !jsonNode.isArray()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> next = fields.next();
                number++;
                String codeArray[] = getCode(fCode, number).split(",");
                save(codeArray[1], fCode, next.getKey(), level);
                list(next.getValue(), level + 1, codeArray[0]);
            }
        } else {
            for (JsonNode node : jsonNode) {
                number++;
                String codeArray[] = getCode(fCode, number).split(",");
                save(codeArray[1], fCode, node.asText(), level);
            }
            return;
        }

    }

    public void save(String code, String fCode, String name, int level) {
        // 1，补位父编号
        StringBuilder fCodeBuilder = new StringBuilder(fCode == null ? "" : fCode);
        if (fCode == null) {
            fCodeBuilder.append("0");
        } else {
            if (fCode.length() < 8) {
                for (int i = 1; i <= 8 - fCode.length(); i++) {
                    fCodeBuilder.append("0");
                }
            }
        }

        // 2，初始行政区域对象
        AdministrativeRegion ar = new AdministrativeRegion(Long.parseLong(code),
                Long.parseLong(fCodeBuilder.toString()), name, level);

        // 3，加入容器
        lists.add(ar);

    }

    /**
     * 获取编号
     *
     * @param fCode  父编号
     * @param number 当前数字
     * @return
     */
    public String getCode(String fCode, int number) {
        // 1，未补位编号
        StringBuilder nt = new StringBuilder();
        if (fCode != null) {
            nt.append(fCode);
        }
        nt.append(number < 10 ? "0" + number : number + "");

        // 2，补位编号
        StringBuilder nt1 = new StringBuilder(nt);
        // 编号不足 8 位，用数字 0 补齐 8 位
        if (nt.length() < 8) {
            for (int i = 1; i <= 8 - nt.length(); i++) {
                nt1.append("0");
            }
        }
        return nt.append("," + nt1.toString()).toString();
    }

}
