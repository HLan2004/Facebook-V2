package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.UserPrincipal;
import com.facebookv2.facebookBE.service.UserService;
import com.facebookv2.facebookBE.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/facebook/user")
public class UserController {
    @Autowired
    UserServiceImpl userServiceImpl;

    @GetMapping("/home")
    public String HomePage( Model model, Principal principal) {
        String email = principal.getName();
        User user = userServiceImpl.getUserByEmail(email);
        model.addAttribute("user", user);
        return "user/home";
    }



    @GetMapping("/me")
    @ResponseBody // sẽ trả về text lập tức trong trang web
    public String currentUser(UserPrincipal userPrincipal, Principal principal) {
        if (userPrincipal != null) {
            String email = principal.getName();
            return "Xin chào, " + userServiceImpl.getUserByEmail(email);

        }
        return "user not found";
    }
}
