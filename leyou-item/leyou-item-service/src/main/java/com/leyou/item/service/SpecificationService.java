package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 添加分组
     * @param specGroup
     * @return
     */
    @Transactional
    public int addGroup(SpecGroup specGroup) {
        return this.specGroupMapper.insert(specGroup);
    }

    /**
     * 修改分组信息
     * @param specGroup
     * @return
     */
    @Transactional
    public int updateGroup(SpecGroup specGroup) {
        return this.specGroupMapper.updateByPrimaryKey(specGroup);
    }

    /**
     * 新增规格参数
     * @param specParam
     * @return
     */
    public int addParam(SpecParam specParam) {
        return this.specParamMapper.insert(specParam);
    }

    /**
     * 修改规格参数
     * @param specParam
     * @return
     */
    @Transactional
    public int updateParam(SpecParam specParam) {
        return this.specParamMapper.updateByPrimaryKey(specParam);
    }

    /**
     * 根据id删除规格参数
     * @param id
     * @return
     */
    @Transactional
    public int deleteParamById(Long id) {
        return this.specParamMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据id删除分组
     * @param id
     * @return
     */
    @Transactional
    public int deleteGroupById(Long id) {
        //其实这里的操作分为两步,首先需要删除该分组下的所有规格参数，再删除该分组
        //我们拿到的id在参数表中其实是group_id
        SpecParam record = new SpecParam();
        record.setGroupId(id);
        //删除该分组下的所有规格参数
        this.specParamMapper.delete(record);
        //删除该分组
        return this.specGroupMapper.deleteByPrimaryKey(id);
    }
}
