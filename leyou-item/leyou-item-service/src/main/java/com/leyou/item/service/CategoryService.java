package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点
     *
     * @param pid 父节点id
     * @return
     */
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        //根据条件查询：先创建对象，对象中的属性会被解析成条件
        return this.categoryMapper.select(record);
    }

    public List<String> queryNamesByIds(List<Long> ids) {
        // 根据多个id查询出多个分类
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        // 我们只需要分类的名字，所以这里使用stream流将分类的name取出放入新集合中返回
        return categories.stream().map(Category::getName).collect(Collectors.toList());
    }
}
