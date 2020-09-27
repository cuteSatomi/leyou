package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zzx
 * @Date 2020-09-20 21:37
 */
@Service
public class GoodsService {

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据条件分页查询SPU
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        // 创建条件查询所需对象
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        // 添加查询条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        // 添加上下架的过滤条件
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        // 添加分页
        PageHelper.startPage(page, rows);

        // 执行查询，获取spu集合
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        // spu集合转换成spubo集合
        // 通过stream表达式将spus转换成spubos
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            //使用BeanUtils工具类将Spu对象的属性拷贝到SpuBo对象中
            BeanUtils.copyProperties(spu, spuBo);
            // bname和cname需要查表获得
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            //根据多个id查询返回分类名称集合
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            // 将集合转成字符串
            spuBo.setCname(StringUtils.join(names, "--"));
            return spuBo;
        }).collect(Collectors.toList());

        // 返回pageResult<SpuBo>
        return new PageResult<SpuBo>(pageInfo.getTotal(), spuBos);
    }

    /**
     * 新增商品
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        /*
         根据表结构，tb_spu与tb_spu_detail  通过  tb_spu.id 和 tb_spu_detail.spu_id关联
                   tb_spu与tb_sku  通过  tb_spu.id 和 tb_sku.spu_id关联
                   tb_sku与tb_stock  通过  tb_sku.id 和 tb_stock.sku_id关联
        */

        // 先新增tb_spu，需要设置一些默认值
        // 防止注入
        spuBo.setId(null);
        // 默认上架
        spuBo.setSaleable(true);
        // 默认可用
        spuBo.setValid(true);
        // 当前时间即创建时间
        spuBo.setCreateTime(new Date());
        // 新增商品时创建时间即最后更新时间
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);

        // 再根据自动生成的spuId去新增tb_spu_detail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        // 将新增spu表后自动生成的id设置到spuDetail的spu_id
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);

        saveSkuAndStock(spuBo);
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 根据spuId新增tb_sku
            // 同上
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            //再根据新增tb_sku时自动生成的id去新增tb_stock
            Stock stock = new Stock();
            // 新增sku后生成的skuId设置进stock中
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据supId查询spuDetail
     *
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查询sku集合
     *
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            //根据sku的id查询出库存信息
            Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 更新商品信息
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        // 根据spuId查询要删除的spu
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            // 根据skuId删除stock
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        });

        // 删除sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        this.skuMapper.delete(sku);

        // 新增sku
        // 新增stock
        // 调用上面封装的方法
        saveSkuAndStock(spuBo);

        // 更新spu和spuDetail
        // 把不需要在商品更新页面修改的属性设为null，防止恶意篡改
        spuBo.setCreateTime(null);
        // 更新最后更新时间
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
    }

    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }
}
