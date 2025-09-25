package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.repository.StatusRepository;
import com.facebookv2.facebookBE.repository.UserRepository;
import com.facebookv2.facebookBE.service.StatusService;
import com.facebookv2.facebookBE.service.StorageService;
import com.facebookv2.facebookBE.service.UserService;
import com.facebookv2.facebookBE.service.impl.StatusServiceImpl;
import com.facebookv2.facebookBE.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/facebook/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public String profile(Model model, Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);


        List<Status> statuses = statusService.getAllStatusesByUserIdOrderByCreatedTimeDesc(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("statuses", statuses);

        return "user/profile";
    }

    @PostMapping("/profilepost")
    public String createStatus(Authentication authentication,
                               @ModelAttribute Status status,
                               @RequestParam("pictureFile") MultipartFile pictureFile) { // Thêm tham số MultipartFile
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        // Gọi StorageService để lưu file và lấy lại tên file
        String generatedFileName = storageService.store(pictureFile);

        // Nếu có file được tải lên, gán tên file vào đối tượng Status
        if (generatedFileName != null) {
            status.setPicture(generatedFileName);
        }

        status.setUser(user);
        status.setCreatedTime(LocalDateTime.now());
        statusService.saveStatus(status);

        return "redirect:/facebook/profile"; // Chuyển hướng về trang chủ
    }
}

