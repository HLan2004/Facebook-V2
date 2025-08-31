package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Role;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.UserDTO;
import com.facebookv2.facebookBE.repository.RoleRepository;
import com.facebookv2.facebookBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/facebook/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/signIn")
    public String signInPage(){
        return "auth/signIn";
    }

    @GetMapping("/signUp")
    public String signUpPage(Model model){
        model.addAttribute("user", new UserDTO());
        return "auth/signUp";
    }

    @PostMapping("/save")
    public String signUp(@ModelAttribute("user") UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email is required");
                return "redirect:/facebook/auth/signUp";
            }

            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Password is required");
                return "redirect:/facebook/auth/signUp";
            }

            User user = new User();
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword()); // sẽ encode ở service

            user.setBirthDay(userDTO.getBirthDay());
            user.setBirthMonth(userDTO.getBirthMonth());
            user.setBirthYear(userDTO.getBirthYear());
            user.setGender(userDTO.getGender());

            // Đặt avatar mặc định (user sẽ tự upload sau)
            user.setImageUrl("default.png");

            // lấy ROLE_USER từ DB
            Role roleUser = roleRepository.findByName("ROLE_USER");
            if (roleUser == null) {
                // Tạo role mặc định nếu chưa có
                roleUser = new Role();
                roleUser.setName("ROLE_USER");
                roleUser = roleRepository.save(roleUser);
            }
            user.setRoles(Set.of(roleUser));

            // Lưu user
            User savedUser = userService.save(user);

            if (savedUser != null && savedUser.getId() != null) {
                redirectAttributes.addFlashAttribute("success", "Account created successfully!");
                return "redirect:/facebook/auth/signIn";
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to create account");
                return "redirect:/facebook/auth/signUp";
            }

        } catch (Exception e) {
            System.out.println("Error during sign up: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/facebook/auth/signUp";
        }
    }
}
