package com.leyou.gateway.filter;

import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzx
 * @date 2020-09-30 15:08:38
 */
// 加入到spring容器中并且启用@EnableConfigurationProperties注解，才可以使用JwtProperties配置类
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        // 登录过滤器应该是前置过滤
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        // 获取白名单
        List<String> allowPaths = this.filterProperties.getAllowPaths();

        // 初始化zuul网关的运行上下文，从运行上下文中获取request对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        // 获取请求路径，判断请求路径是否在白名单中
        String url = request.getRequestURL().toString();

        for (String allowPath : allowPaths) {
            if (StringUtils.contains(url, allowPath)) {
                // 如果是白名单内的路径，则不需要过滤，即不需要执行run方法，直接返回false
                return false;
            }
        }

        // 此处配置为true才会执行run方法对登录进行拦截过滤
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 初始化zuul网关的运行上下文，从运行上下文中获取request对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());

        /*if (StringUtils.isBlank(token)) {
            // 为空直接拦截
            context.setSendZuulResponse(false);
            // 并且响应身份未认证
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }*/

        try {
            JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            // 解析异常也是拦截
            context.setSendZuulResponse(false);
            // 并且响应身份未认证
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }
}
