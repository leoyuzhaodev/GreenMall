package com.yzf.greenmall.common;

import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.Map;
import java.util.Set;

/**
 * @description:分页查询对象
 * @author:leo_yuzhao
 * @date:2020/11/10
 */
@Data
public class QueryPage<T> {
    private int page; // 页码
    private int limit; // 当前页面数据条数
    // 根据什么字段的什么值进行查询，多个字段用一个值查询：key:field1,field2 value:keyValue
    private Map<String, String> queryMap;

    public QueryPage() {
        this.page = 1;
        this.limit = 10;
    }

    public QueryPage(int page, int limit) {
        this.page = page;
        this.limit = limit;
    }

    public QueryPage(int page, int limit, Map<String, String> queryMap) {
        this.page = page;
        this.limit = limit;
        this.queryMap = queryMap;
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
        Weekend<T> weekend = new Weekend<T>(classz);
        WeekendCriteria<T, Object> keywordCriteria = weekend.weekendCriteria();
        // 表示是否进行了条件查询
        boolean isCriteria = false;

        // 根据 queryMap 构建查询对象
        /*
            queryMap={
                "title,subTitle": "",
                "valid": true,
                "saleable": true
            }
            WHERE (valid = 1 and saleable = 1 and (title like '%胡萝卜%' or sub_title like '%胡萝卜%'))
        */
        if (!CollectionUtils.isEmpty(queryMap)) {
            Set<String> fields = queryMap.keySet();
            for (String field : fields) {
                if (!StringUtils.isEmpty(field)) {
                    String[] fds = field.split(",");
                    if (fds.length > 1) {
                        // 组合关键字查询
                        for (String fd : fds) {
                            if (!StringUtils.isEmpty(fd)) {
                                keywordCriteria.orLike(fd, "%" + queryMap.get(field) + "%");
                            }
                        }
                    } else if (fds.length == 1) {
                        // 组合条件查询
                        if (!StringUtils.isEmpty(fds[0])) {
                            if (!StringUtils.isEmpty(queryMap.get(field))) {
                                isCriteria = true;
                                criteria.andEqualTo(fds[0], queryMap.get(field));
                            }
                        }
                    }
                }
            }
        }
        if (isCriteria) {
            weekend.and(criteria);
        }
        return weekend;
    }

    /**
     * 组装查询对象
     *
     * @param classz
     * @param isLike 是否模糊查询
     * @return
     */
    public Example generateExample(Class classz, boolean isLike) {
        // 查询对象
        Example example = new Example(classz);
        // 查询条件对象
        Example.Criteria criteria = example.createCriteria();
        Weekend<T> weekend = new Weekend<T>(classz);
        WeekendCriteria<T, Object> keywordCriteria = weekend.weekendCriteria();
        // 表示是否进行了条件查询
        boolean isCriteria = false;

        // 根据 queryMap 构建查询对象
         /*
            "id,accountId,addressId":"afdad","categoryId":"10"
            where （id = ? or accountId = ? or addressId = ?） and categoryId = 10
         */
        if (!CollectionUtils.isEmpty(queryMap)) {
            Set<String> fields = queryMap.keySet();
            for (String field : fields) {
                if (!StringUtils.isEmpty(field)) {
                    String[] fds = field.split(",");
                    if (fds.length > 1) {
                        // 组合关键字查询
                        for (String fd : fds) {
                            if (!StringUtils.isEmpty(fd)) {
                                if (isLike) {
                                    keywordCriteria.orLike(fd, "%" + queryMap.get(field) + "%");
                                } else {
                                    // 注意非空判断
                                    if (!StringUtils.isEmpty(queryMap.get(field))) {
                                        keywordCriteria.orEqualTo(fd, queryMap.get(field));
                                    }
                                }
                            }
                        }
                    } else if (fds.length == 1) {
                        // 组合条件查询
                        if (!StringUtils.isEmpty(fds[0])) {
                            if (!StringUtils.isEmpty(queryMap.get(field))) {
                                isCriteria = true;
                                criteria.andEqualTo(fds[0], queryMap.get(field));
                            }
                        }
                    }
                }
            }
        }
        if (isCriteria) {
            weekend.and(criteria);
        }
        return weekend;
    }


}
