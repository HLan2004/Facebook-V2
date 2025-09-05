package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/facebook/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String HomePage(Model model, Authentication authentication){

        String email = authentication.getName(); // lấy email từ principal
        User user = userService.getUserByEmail(email);
        model.addAttribute("user", user);
        return "user/home";
    }
}
