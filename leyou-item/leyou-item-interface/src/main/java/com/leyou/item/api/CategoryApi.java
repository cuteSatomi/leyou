package com.leyou.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category") //配置全局路径为category
public interface CategoryApi {

    @GetMapping
    public List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);
}
