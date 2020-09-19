package com.leyou.item.pojo;

import javax.persistence.*;

/**
 * 规格参数实体类
 *
 * @Author zzx
 * @Date 2020-09-19 17:04
 */
@Table(name = "tb_spec_param")
public class SpecParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //该注解表示主键回写
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    //属性名与表中字段名一致时无需加Column注解，此处看似该属性名与表中一致，但是其实numeric在mysql中是一个关键字，需要用``转义
    @Column(name = "`numeric`")
    private Boolean numeric;
    /**
     * 数字类型的单位，非数字类型可以为空
     */
    private String unit;
    /**
     * 是否是sku通用属性
     */
    private Boolean generic;
    /**
     * 是否用于搜索过滤
     */
    private Boolean searching;
    /**
     * 数值类型参数，如CPU频率：0.5 - 1.0
     */
    private String segments;

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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNumeric() {
        return numeric;
    }

    public void setNumeric(Boolean numeric) {
        this.numeric = numeric;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getGeneric() {
        return generic;
    }

    public void setGeneric(Boolean generic) {
        this.generic = generic;
    }

    public Boolean getSearching() {
        return searching;
    }

    public void setSearching(Boolean searching) {
        this.searching = searching;
    }

    public String getSegments() {
        return segments;
    }

    public void setSegments(String segments) {
        this.segments = segments;
    }
}
