package com.yzf.greenmall.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzf.greenmall.entity.AdministrativeRegion;
import com.yzf.greenmall.mapper.AdministrativeRegionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:AdministrativeRegionService
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Service
public class AdministrativeRegionService {
    List<AdministrativeRegion> lists = new ArrayList<>();

    @Autowired
    private AdministrativeRegionMapper arMapper;


    /**
     * 初始化地址
     *
     * @throws Exception
     */
    private void initAR() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(new File("D:/test/address.json"));
        list(jsonNode, 1, null);
        int count = 0;
        System.out.println("数据总条数为：" + lists.size());
        for (AdministrativeRegion ar : lists) {
            arMapper.insert(ar);
            System.out.println("添加成功：" + (++count));
        }
    }

    /**
     * 递归遍历读取json文件中的地址
     *
     * @param jsonNode
     * @param level
     * @param fCode
     */
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

    /**
     * 根据地址信息生成地址对象，保存到容器中
     *
     * @param code  12010000
     * @param fCode 12
     * @param name  测试
     * @param level 2
     */
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
    private String getCode(String fCode, int number) {
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

    /**
     * 根据父id查找行政区划信息
     *
     * @param fId
     * @return
     */
    public List<AdministrativeRegion> findByFId(Long fId) {
        AdministrativeRegion ar = new AdministrativeRegion();
        ar.setFatherId(fId);
        List<AdministrativeRegion> arList = arMapper.select(ar);
        return arList;
    }

    /**
     * 根据多个id获取多个对应的名称
     *
     * @param ids
     * @return
     */
    public List<String> findNamesByIds(List<Long> ids) {
        List<AdministrativeRegion> arList = arMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(arList)) {
            return null;
        }
        return arList.stream().map(AdministrativeRegion::getName).collect(Collectors.toList());
    }

}
