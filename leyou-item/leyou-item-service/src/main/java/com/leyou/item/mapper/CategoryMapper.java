package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * SelectByIdListMapper是一个根据id集合查询的接口，第一个泛型是查询返回类型，第二个是主键的类型
 */
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {

}
