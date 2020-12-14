package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.GoodsBo;
import com.yzf.greenmall.bo.GoodsIntroduction;
import com.yzf.greenmall.bo.GoodsSVBo;
import com.yzf.greenmall.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

/**
 * @description:GoodsHtmlController（商品详情展示）
 * @author:leo_yuzhao
 * @date:2020/11/26
 */
@Controller
@RequestMapping(path = "/goods")
public class GoodsHtmlController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 根据商品id查询商品详情
     *
     * @param model
     * @param id
     * @return
     */
    @GetMapping(path = "/introduction/{id}.html")
    public String toItemPage(Model model, @PathVariable("id") Long id) throws IOException {
        GoodsIntroduction introduction = goodsService.findIntroductionById(id);
        if (introduction == null) {
            throw new RuntimeException("根据商品id无法查询到具体的商品");
        }
        model.addAttribute("introduction", introduction);
        return "introduction";
    }

    /**
     * 查询新上架的商品
     *
     * @return
     */
    @GetMapping(path = "/newGoods")
    public ResponseEntity<List<GoodsSVBo>> queryNewGoods() {
        try {
            List<GoodsSVBo> list = goodsService.findNewGoods();
            if (CollectionUtils.isEmpty(list)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询销量前12的商品
     *
     * @return
     */
    @GetMapping(path = "/queryTopSaleVolumeGoods")
    public ResponseEntity<List<GoodsSVBo>> queryTopSaleVolumeGoods() {
        try {
            List<GoodsSVBo> list = goodsService.findTopSaleVolumeGoods();
            if (CollectionUtils.isEmpty(list)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询推荐的商品
     *
     * @return
     */
    @GetMapping(path = "/queryRecommendGoods")
    public ResponseEntity<List<GoodsSVBo>> queryRecommendGoods() {
        try {
            List<GoodsSVBo> list = goodsService.queryRecommendGoods();
            if (CollectionUtils.isEmpty(list)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
