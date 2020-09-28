package com.leyou.user.controller;

import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author zzx
 * @date 2020-09-28 17:10:04
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;


}
