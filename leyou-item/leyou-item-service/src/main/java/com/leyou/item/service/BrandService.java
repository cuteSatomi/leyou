package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序查询品牌信息
     *
     * @param key    查询的条件
     * @param page   查询的页数
     * @param rows   每页显示条数
     * @param sortBy 根据哪个字段排序
     * @param desc   是否升序
     * @return 返回分类集合
     */
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        //初始化Example对象，复杂的条件查询
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //根据name模糊查询，或者根据首字母查询
        //key不为空才处理是否进行模糊查询
        if (StringUtils.isNotBlank(key)) {
            // andLike是根据name来模糊查询，orEquals根据首字母来查询，两个条件应该是or的关系
            // 这里的sql其实就相当于 select * from tb_brand where 1 = 1 and name like '%key%' or letter = 'key'
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        //添加排序条件
        //排序条件不为空时，才进行处理
        if (StringUtils.isNotBlank(sortBy)) {
            // desc参数为true时，才为降序
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        //添加分页条件，使用PageHelper插件
        PageHelper.startPage(page, rows);

        //进行查询
        List<Brand> brands = this.brandMapper.selectByExample(example);
        //将查询的对象包装成PageInfo分页对象
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        // 需要往两张表中插入数据，一张是brand表tb_brand。另一张是中间表tb_category_brand
        // 首先需要新增品牌表，这样才会生成品牌id
        this.brandMapper.insertSelective(brand);

        // 再根据品牌id将数据插入中间表
        cids.forEach(cid -> {
            this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });

    }
}
