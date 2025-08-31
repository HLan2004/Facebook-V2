package com.facebookv2.facebookBE.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/facebook/user")
public class UserController {

    @GetMapping("/home")
    public String HomePage(){
        return "user/home";
    }
}
