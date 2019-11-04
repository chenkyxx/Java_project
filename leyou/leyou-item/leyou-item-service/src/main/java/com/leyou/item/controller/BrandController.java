package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@Controller
@RequestMapping(value = "brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 根据查询条件分页查询品牌信息，并排序
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping(value = "page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",required = false) Boolean desc
    ){
        PageResult<Brand> result = this.brandService.queryBrandsByPage(key,page,rows,sortBy,desc);
        if(result == null || CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);

    }


    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam(value = "cids") List<Long> cids){
        this.brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
}
