package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @Author zzx
 * @Date 2020-09-13 20:00
 */

@SpringBootApplication
@EnableDiscoveryClient //开启eureka客户端注解，这里还可以使用@EnableEurekaClient
@EnableZuulProxy //开启zuul网关注解
public class LeyouGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouGatewayApplication.class,args);
    }
}
