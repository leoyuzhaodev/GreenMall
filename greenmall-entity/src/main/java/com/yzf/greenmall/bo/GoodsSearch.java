package com.yzf.greenmall.bo;

import com.yzf.greenmall.entity.Goods;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:用户elasticSearch搜索
 * @author:leo_yuzhao
 * @date:2020/11/18
 */
@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 0)
public class GoodsSearch implements Serializable {
    @Id
    private Long id; // 商品ID

    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;// 子标题

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all; // 所有需要被搜索的信息，商品名称，子标题，分类

    @Field(type = FieldType.Keyword, index = false)
    private String images; // 商品图片以英文逗号分割

    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private Date createTime;// 创建时间
    private Long salesVolume;// 销量
    private Double price; // 商品价格，注意通用 Mapper 不支持基本数据类型
    private Integer evaluationScores; // 总评分均分

    public GoodsSearch() {

    }

    public GoodsSearch(Long id, String subTitle, String all, String images, Long cid1, Long cid2, Long cid3, Date createTime, Double price) {
        this.id = id;
        this.subTitle = subTitle;
        this.all = all;
        this.images = images;
        this.cid1 = cid1;
        this.cid2 = cid2;
        this.cid3 = cid3;
        this.createTime = createTime;
        this.price = price;
        this.evaluationScores = evaluationScores;
    }

    /**
     * Goods => GoodsSearch
     *
     * @param goods
     * @return
     */
    public static GoodsSearch generateGoodsSearch(Goods goods) {
        GoodsSearch goodsSearch = new GoodsSearch(
                goods.getId(),
                goods.getSubTitle(),
                goods.getTitle() + goods.getSubTitle() + goods.getCategory(),
                goods.getGoodsDetail().getImages(),
                goods.getCid1(),
                goods.getCid2(),
                goods.getCid3(),
                goods.getCreateTime(),
                goods.getGoodsDetail().getPrice()
        );
        return goodsSearch;
    }
}
