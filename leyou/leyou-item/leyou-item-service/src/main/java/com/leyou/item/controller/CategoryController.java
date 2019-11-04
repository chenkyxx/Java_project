package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     *根据父节点的ID查询子节点
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam(value = "pid",defaultValue = "0") Long pid){
        if(pid == null || pid < 0){
            // 相应400  参数不合法
            // return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().build();   //都是等价
        }
        List<Category> categories = this.categoryService.queryCategoriesByPid(pid);
        //if(categories == null || categories.size() == 0){

        //}
        if(CollectionUtils.isEmpty(categories)){   // 判断一个集合是否为空
            // 相应404 资源未找到
            // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.notFound().build();
          }
        return ResponseEntity.ok(categories);


        // 程序出错  默认响应500  所以try catch 可以沈略,以下的也可以省略
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
