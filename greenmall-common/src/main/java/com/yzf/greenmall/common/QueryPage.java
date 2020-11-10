package com.yzf.greenmall.common;

import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Map;
import java.util.Set;

/**
 * @description:分页查询对象
 * @author:leo_yuzhao
 * @date:2020/11/10
 */
@Data
public class QueryPage {
    private int page; // 页码
    private int limit; // 当前页面数据条数
    // 根据什么字段的什么值进行查询，多个字段用一个值查询：key:field1,field2 value:keyValue
    private Map<String, String> queryMap;

    public QueryPage() {
        this.page = 1;
        this.limit = 10;
    }

    /**
     * 组装查询对象
     *
     * @param classz
     * @return
     */
    public Example generateExample(Class classz) {
        // 查询对象
        Example example = new Example(classz);
        // 查询条件对象
        Example.Criteria criteria = example.createCriteria();
        // 根据 queryMap 构建查询对象
        if (!CollectionUtils.isEmpty(queryMap)) {
            Set<String> fields = queryMap.keySet();
            for (String field : fields) {
                if (!StringUtils.isEmpty(field)) {
                    String[] fds = field.split(",");
                    if (fds.length > 1) {
                        // key:field1,field2 value:keyValue 使用or连接
                        for (String fd : fds) {
                            if (!StringUtils.isEmpty(fd)) {
                                criteria.orLike(fd, "%" + queryMap.get(field) + "%");
                            }
                        }
                    } else if (fds.length == 1) {
                        // key:field1 value:keyValue 使用and连接
                        if (!StringUtils.isEmpty(fds[0])) {
                            criteria.andLike(fds[0], "%" + queryMap.get(field) + "%");
                        }
                    }

                }
            }
        }
        return example;
    }


}
