package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.service.StatusService;
import com.facebookv2.facebookBE.service.UserService;
import com.facebookv2.facebookBE.service.StorageService;
import com.facebookv2.facebookBE.service.impl.StatusServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/facebook/status")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @Autowired
    private UserService userService;


    @Autowired
    private StorageService storageService;

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


    @PostMapping("/homepost")
    public String createStatus(Authentication authentication,
                               @ModelAttribute Status status,
                               @RequestParam("pictureFile") MultipartFile pictureFile) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        String generatedFileName = storageService.store(pictureFile);


        if (generatedFileName != null) {
            status.setPicture(generatedFileName);
        }

        status.setUser(user);
        status.setCreatedTime(LocalDateTime.now());
        statusService.saveStatus(status);

        return "redirect:/facebook/status/home"; // Chuyển hướng về trang chủ
    }
}