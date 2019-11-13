package com.leyou.item.service;

/**
 * Copyright (C), 2015-2019, 重庆智汇航安智能科技研究院有限公司
 * FileName: SpecificationService
 * Author:   chenky  842202171@qq.com
 * Date:     2019/11/8 21:40
 * History:
 */

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;

    /**
     * 根据分类Id来查询参数组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        return this.groupMapper.select(group);

    }

    /**
     * 根据条件查询规格参数
     *
     * @param cid
     * @param gid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParams(Long cid, Long gid, Boolean generic, Boolean searching) {
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setGeneric(generic);
        record.setSearching(searching);
        return this.paramMapper.select(record);

    }
}

