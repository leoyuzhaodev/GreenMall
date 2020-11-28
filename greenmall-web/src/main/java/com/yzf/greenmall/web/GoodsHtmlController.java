package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.GoodsIntroduction;
import com.yzf.greenmall.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

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
}
