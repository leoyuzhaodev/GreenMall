package com.yzf.greenmall.web;

import com.yzf.greenmall.bo.GoodsSearch;
import com.yzf.greenmall.common.PageResult;
import com.yzf.greenmall.common.SearchRequest;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.service.GoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:SearchController
 * @author:leo_yuzhao
 * @date:2020/11/18
 */
@Controller
@RequestMapping(path = "/search")
public class SearchController {

    @Autowired
    private GoodsSearchService searchService;

    /**
     * 商品搜索
     *
     * @param request
     * @return
     */
    @PostMapping(path = "/page")
    public ResponseEntity<PageResult<GoodsSearch>> search(@RequestBody SearchRequest request) {
        PageResult<GoodsSearch> result = this.searchService.search(request);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

}
