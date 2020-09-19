package com.leyou.item.pojo;

import javax.persistence.*;
import java.util.List;

/**
 * 规格参数组的实体类
 * @Author zzx
 * @Date 2020-09-19 17:02
 */
@Table(name = "tb_spec_group")
public class SpecGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid;
    private String name;

    @Transient //该注解表示忽略该字段，即表中不存在该字段
    private List<SpecParam> params;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SpecParam> getParams() {
        return params;
    }

    public void setParams(List<SpecParam> params) {
        this.params = params;
    }
}
