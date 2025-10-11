package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
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
    // Dán code này vào ProfileController.java để thay thế phương thức cũ

    @PostMapping("/update-images")
    public String updateProfileImages(@RequestParam("avatarImage") MultipartFile avatarFile,
                                      @RequestParam("coverImage") MultipartFile coverFile,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {

        // 1. Lấy email của người dùng đang đăng nhập
        String email = authentication.getName();

        // 2. Khởi tạo biến để lưu tên file mới, ban đầu là null
        String avatarFileName = null;
        String coverFileName = null;

        // 3. Xử lý file avatar nếu người dùng có tải lên
        if (!avatarFile.isEmpty()) {
            // Lưu file và lấy lại tên duy nhất đã được tạo
            avatarFileName = storageService.store(avatarFile);
        }

        // 4. Xử lý file ảnh bìa nếu người dùng có tải lên
        if (!coverFile.isEmpty()) {
            // Lưu file và lấy lại tên duy nhất đã được tạo
            coverFileName = storageService.store(coverFile);
        }

        // 5. Chỉ gọi update vào database khi có ít nhất 1 ảnh mới được tải lên
        if (avatarFileName != null || coverFileName != null) {
            // GỌI PHƯƠNG THỨC MỚI, AN TOÀN HƠN MÀ BẠN VỪA TẠO
            userService.updateUserImages(email, avatarFileName, coverFileName);
        }

        // 6. Gửi thông báo thành công và chuyển hướng
        redirectAttributes.addFlashAttribute("success", "Cập nhật ảnh thành công!");
        return "redirect:/facebook/profile";
    }

}

