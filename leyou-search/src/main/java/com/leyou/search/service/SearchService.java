package com.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

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

    // 将对象序列化为json字符串的类
    private static final ObjectMapper MAPPER = new ObjectMapper();

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
                specs.put(param.getName(),value);
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
