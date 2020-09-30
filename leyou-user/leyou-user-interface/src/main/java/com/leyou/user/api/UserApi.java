package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zzx
 * @date 2020-09-30 13:47:58
 */
public interface UserApi {

    @GetMapping("query")
    public User queryUser(@RequestParam("username") String username, @RequestParam("password") String password);
}
