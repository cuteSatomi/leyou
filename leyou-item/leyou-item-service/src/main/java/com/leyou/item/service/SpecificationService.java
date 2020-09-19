package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zzx
 * @Date 2020-09-19 17:22
 */
@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id查询参数组
     *
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        return this.specGroupMapper.select(record);
    }

    /**
     * 根据gid查询规格参数
     *
     * @param gid
     * @return
     */
    public List<SpecParam> queryParamByGid(Long gid) {
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        return this.specParamMapper.select(record);
    }

    public int addGroup(SpecGroup specGroup) {
        return this.specGroupMapper.insert(specGroup);
    }
}
