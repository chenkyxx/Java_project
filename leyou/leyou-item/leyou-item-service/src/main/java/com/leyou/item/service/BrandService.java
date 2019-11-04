package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Transient;
import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     *根据查询条件分页查询品牌信息，并排序
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        // 初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        // 根据name模糊查询 或者根据letter进行首字母查询

        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name", "%"+ key+"%").orEqualTo("letter",key);
        }
        // 添加分页条件
        PageHelper.startPage(page,rows);

        // 添加排序
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+""+(desc?"desc":"asc"));
        }
        List<Brand> brands = this.brandMapper.selectByExample(example);
        // 包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        // 包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());

    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Transactional   //事物  要成功  都成功  要失败都失败
    public void saveBrand(Brand brand, List<Long> cids) {
        // 先新增brand
        //boolean flag = this.brandMapper.insertSelective(brand) == 1;  加了事物 不需要了
        this.brandMapper.insertSelective(brand);
        // 再新增中间表
//  由于有了事物  就不要判断了
//        if(flag){
//            cids.forEach(cid->{
//                // 通用mapper不能操作两张表，所以只能自己写sql语句
//                this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
//            });
//        }
        cids.forEach(cid->{
            this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });
    }
}
