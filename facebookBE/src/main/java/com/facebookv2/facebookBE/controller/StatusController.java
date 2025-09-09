package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.service.StatusService;
import com.facebookv2.facebookBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/facebook/status")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @Autowired
    private UserService userService;


    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        List<Status> statuses = statusService.getAllStatuses();

        model.addAttribute("user", user);
        model.addAttribute("statuses", statuses);
        model.addAttribute("newStatus", new Status());

        return "user/home";
    }


    @PostMapping("/post")
    public String createStatus(Authentication authentication,
                               @ModelAttribute Status status) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        status.setUser(user);
        status.setCreatedTime(LocalDateTime.now());
        statusService.saveStatus(status);

        return "redirect:/facebook/status/home";
    }
}

