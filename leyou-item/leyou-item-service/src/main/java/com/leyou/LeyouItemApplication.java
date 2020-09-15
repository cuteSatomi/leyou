package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author zzx
 * @Date 2020-09-13 20:32
 */
@SpringBootApplication
@EnableDiscoveryClient //eureka客户端注解
@MapperScan("com.leyou.item.mapper") //开启通用mapper的扫描并且配置路径
public class LeyouItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouItemApplication.class, args);
    }
}
