package com.leyou.search.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Map;

// indexName：对应索引库名称, type：对应在索引库中的类型, shards：分片数量，默认5, replicas：副本数量，默认1
@Document(indexName = "goods",type = "docs",shards = 1,replicas = 0)
public class Goods {
    @Id // 作用在成员变量，标记一个字段作为id主键
    private Long id; //spuId

    // type：字段类型，取值是枚举：FieldType, index：是否索引，布尔类型，默认是true
    // store：是否存储，布尔类型，默认是false, analyzer：分词器名称：ik_max_word
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String all; // 所有需要被搜索的信息，包括标题、分类甚至品牌

    @Field(type = FieldType.Keyword,index = false)
    private String subTitle; // 卖点，即副标题
    private Long brandId; // 品牌id
    private Long cid1; // 1级分类id
    private Long cid2; // 2级分类id
    private Long cid3; // 3级分类id
    private Date createTime; // 创建时间
    private List<Long> price; // 价格

    @Field(type = FieldType.Keyword,index = false)
    private String skus; // List<Sku>信息的json结构
    private Map<String,Object> specs; // 可搜索的规格参数，key是参数名，值是参数值

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCid1() {
        return cid1;
    }

    public void setCid1(Long cid1) {
        this.cid1 = cid1;
    }

    public Long getCid2() {
        return cid2;
    }

    public void setCid2(Long cid2) {
        this.cid2 = cid2;
    }

    public Long getCid3() {
        return cid3;
    }

    public void setCid3(Long cid3) {
        this.cid3 = cid3;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<Long> getPrice() {
        return price;
    }

    public void setPrice(List<Long> price) {
        this.price = price;
    }

    public String getSkus() {
        return skus;
    }

    public void setSkus(String skus) {
        this.skus = skus;
    }

    public Map<String, Object> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, Object> specs) {
        this.specs = specs;
    }
}
