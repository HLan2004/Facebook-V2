package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.UserAvatar;
import com.facebookv2.facebookBE.model.dto.FriendDTO;
import com.facebookv2.facebookBE.repository.StatusRepository;
import com.facebookv2.facebookBE.repository.UserRepository;
import com.facebookv2.facebookBE.service.FriendshipService;
import com.facebookv2.facebookBE.service.StatusService;
import com.facebookv2.facebookBE.service.StorageService;
import com.facebookv2.facebookBE.service.UserService;
import com.facebookv2.facebookBE.service.impl.StatusServiceImpl;
import com.facebookv2.facebookBE.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.facebookv2.facebookBE.repository.UserAvatarRepository;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/facebook/profile")
public class ProfileController {

    @Autowired
    private UserAvatarRepository userAvatarRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private StorageService storageService;
    @Autowired
    private FriendshipService friendshipService;

    @GetMapping
    public String profile(Model model, Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);


        List<Status> statuses = statusService.getAllStatusesByUserIdOrderByCreatedTimeDesc(user.getId());

        model.addAttribute("currentUser", user);
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


        String generatedFileName = storageService.store(pictureFile);


        if (generatedFileName != null) {
            status.setPicture(generatedFileName);
        }

        status.setUser(user);
        status.setCreatedTime(LocalDateTime.now());
        statusService.saveStatus(status);

        return "redirect:/facebook/profile"; // Chuyển hướng về trang chủ
    }
    @GetMapping("/{id}")
    public String viewProfile(@PathVariable Long id, Model model, Authentication authentication) {
        User user = userService.findById(id);
        List<Status> statuses = statusService.getAllStatusesByUserIdOrderByCreatedTimeDesc(user.getId());
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        friendshipService.checkFriendship1to1(currentUser, user, model);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", user);
        model.addAttribute("statuses", statuses);
        return "user/profile";
    }


    @PostMapping("/update-images")
    @ResponseBody
    public Map<String, Object> updateProfileImages(
            @RequestParam(name = "avatarImage", required = false) MultipartFile avatarFile,
            @RequestParam(name = "coverImage", required = false) MultipartFile coverFile,
            Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        String avatarFileName = null;
        String coverFileName = null;

        if (avatarFile != null && !avatarFile.isEmpty()) {
            avatarFileName = storageService.store(avatarFile);

            // ✅ Lưu vào bảng UserAvatar để ghi lại lịch sử avatar
            UserAvatar avatarRecord = new UserAvatar();
            avatarRecord.setFileName(avatarFileName);
            avatarRecord.setUploadedAt(LocalDateTime.now());
            avatarRecord.setUser(currentUser);
            userAvatarRepository.save(avatarRecord);
        }

        if (coverFile != null && !coverFile.isEmpty()) {
            coverFileName = storageService.store(coverFile);
        }

        if (avatarFileName != null || coverFileName != null) {
            userService.updateUserImages(email, avatarFileName, coverFileName);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("avatarUrl", avatarFileName != null ? "/uploads/" + avatarFileName : null);
        result.put("coverUrl", coverFileName != null ? "/uploads/" + coverFileName : null);

        return result;
    }



}

