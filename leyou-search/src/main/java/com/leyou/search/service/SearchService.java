package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    // 将对象序列化为json字符串的类
    private static final ObjectMapper MAPPER = new ObjectMapper();


    public SearchResult search(SearchRequest request) {

        // 校验参数
        if (StringUtils.isBlank(request.getKey())) {
            return null;
        }
        // 自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加查询条件，各个词之间应该是AND关系
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        // 添加分页，page和size参数从自己封装的request对象中取，这里有一个注意事项：分页页码从0开始，因此这里的page要减1
        queryBuilder.withPageable(PageRequest.of(request.getPage() - 1, request.getSize()));
        // 添加结果集过滤，只需查出id、skus和subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));

        // 添加分类和品牌的聚合，其实就是下面的写法
        /*
            GET /goods/_search
            {
                "size": 0,
                "aggs": {
                    "categories": {
                        "terms": {
                            "field": "cid3"
                        }
                    }
                }
            }
         */
        String categoryAggName = "categories";
        String brandAggName = "brands";
        // 根据cid3聚合查询，类似sql的group by cid3，terms()中的参数是自定义的查询名称
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 执行查询，获取结果集 ，该方法只能获取普通结果集无法获取聚合结果集
        //Page<Goods> goodsPage = this.goodsRepository.search(queryBuilder.build());
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        // 通过聚合名称获取聚合结果集并且解析
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        // 返回自己封装的分页结果集
        return new SearchResult(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent(), categories, brands);
    }

    /**
     * 解析品牌的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        // 强转
        LongTerms terms = (LongTerms) aggregation;

        /*List<Brand> brands = new ArrayList<>();

        // 获取聚合中的桶
        terms.getBuckets().forEach(bucket -> {
            // 从桶中获取所有品牌id，根据id查询相应的品牌
            Brand brand = this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
            brands.add(brand);
        });
        return brands;*/

        return terms.getBuckets().stream().map(bucket -> {
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }

    /**
     * 解析分类的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        // 强转
        LongTerms terms = (LongTerms) aggregation;

        // 获取桶的集合，转换成List<Map<String, Object>>
        return terms.getBuckets().stream().map(bucket -> {
            // 初始化一个map
            Map<String, Object> map = new HashMap<>();
            // 获取桶中的分类id(key)
            Long id = bucket.getKeyAsNumber().longValue();
            // 根据分类id查询分类名称
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(id));
            // 将获取到的数据放入map
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        // 根据分类的id查询分类名称的集合
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        // 根据品牌id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        // 查询所有sku价格，首先根据spuId查询出所有的sku
        List<Sku> skus = this.goodsClient.querySkusByBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        // 收集sku的必要字段信息，只需要id/title/image/price
        List<Map<String, Object>> skuMapList = new ArrayList<>();

        skus.forEach(sku -> {
            // 将查询出的所有sku的价格添加到prices集合中
            prices.add(sku.getPrice());

            // 收集sku数据
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            // 获取sku中的图片，数据库中的图片可能是多张，以"，"分割。切割完成返回数组的第一个元素，即获取第一张图片即可
            map.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuMapList.add(map);
        });

        // 获取spu的cid3查询所有的规格参数
        List<SpecParam> params = this.specificationClient.queryParamsByGidOrCid(null, spu.getCid3(), null, true);
        // 根据spuId查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        // 将通用的规格参数字符串反序列化为map
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        // 将特殊的规格参数字符串反序列化为map
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });

        Map<String, Object> specs = new HashMap<>();
        params.forEach(param -> {
            // 判断规格参数的类型是否是通用的
            if (param.getGeneric()) {
                // 如果是通用类型的参数，从genericSpecMap获取规格参数
                String value = genericSpecMap.get(param.getId().toString()).toString();
                // 判断是否是一个数值类型，如果是数值类型，应该返回一个区间
                if (param.getNumeric()) {
                    value = chooseSegment(value, param);
                }
                specs.put(param.getName(), value);
            } else {
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(), value);
            }
        });

        // goods的id就是spu的id，因此直接取即可
        goods.setId(spu.getId());
        // 下面一些spu中已经有的属性也是直接取出即可
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());

        // 拼接all字段，需要分类名称以及品牌名称
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " ") + " " + brand);

        // 获取spu下的所有sku价格
        goods.setPrice(prices);
        // 获取spu下的所有sku，并转换成json字符串保存
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        // 获取查询的所有规格参数 {name: value}
        goods.setSpecs(null);

        return goods;
    }

    public String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其他";
        //保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
