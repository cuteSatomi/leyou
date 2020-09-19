package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author zzx
 * @Date 2020-09-19 17:23
 */
@Controller
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询参数组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = this.specificationService.queryGroupByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据gid查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamByGid(@RequestParam("gid")Long gid){
       List<SpecParam> params =  this.specificationService.queryParamByGid(gid);
       if(CollectionUtils.isEmpty(params)){
           return ResponseEntity.notFound().build();
       }
        return ResponseEntity.ok(params);
    }

    /**
     * 添加分组
     * @param specGroup
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> addGroup(@RequestBody SpecGroup specGroup){
        int affect = this.specificationService.addGroup(specGroup);
        if(affect == 0){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

}
