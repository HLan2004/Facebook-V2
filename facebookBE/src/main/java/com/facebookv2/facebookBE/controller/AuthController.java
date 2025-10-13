package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Role;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.UserDTO;
import com.facebookv2.facebookBE.repository.RoleRepository;
import com.facebookv2.facebookBE.repository.UserRepository;
import com.facebookv2.facebookBE.service.EmailService;
import com.facebookv2.facebookBE.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Set;

@Controller
@RequestMapping("/facebook/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepo;

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

            // Tạo user mới
            User user = new User();
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword()); // sẽ encode ở service
            user.setBirthDay(userDTO.getBirthDay());
            user.setBirthMonth(userDTO.getBirthMonth());
            user.setBirthYear(userDTO.getBirthYear());
            user.setGender(userDTO.getGender());
            user.setAvatar(null);

            // Lấy ROLE_USER từ DB hoặc tạo nếu chưa có
            Role roleUser = roleRepo.findByName("ROLE_USER");
            if (roleUser == null) {
                roleUser = new Role();
                roleUser.setName("ROLE_USER");
                roleUser = roleRepo.save(roleUser);
            }
            user.setRoles(Set.of(roleUser));

            // Tạo verification code và đặt trạng thái chưa kích hoạt
            // Sinh verification code 5 chữ số
            Random random = new Random();
            int verificationCodeInt = 10000 + random.nextInt(90000); // số từ 10000 đến 99999
            String verificationCode = String.valueOf(verificationCodeInt);
            user.setVerificationCode(verificationCode);
            user.setEnabled(false);

            // Lưu user
            User savedUser = userService.save(user);

            if (savedUser != null && savedUser.getId() != null) {
                // Gửi email xác nhận
                String message = """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            body { font-family: Arial, sans-serif; background-color: #f0f2f5; margin:0; padding:0; }
                            .container { max-width: 600px; margin: 20px auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
                            .header { font-size: 28px; color: #1877f2; font-weight: bold; margin-bottom: 20px; text-align:center; }
                            .content { font-size: 16px; line-height: 1.6; color: #1c1e21; text-align:center; }
                            .code {
                                display: block;        /* block để xuống dòng */
                                max-width: 200px;      /* giới hạn chiều ngang */
                                width: 100%%;           /* co dãn theo nội dung nhưng không vượt max-width */
                                font-size: 24px;
                                font-weight: bold;
                                color: #ffffff;
                                background-color: #1877f2;
                                padding: 12px 0;       /* chỉ padding trên/dưới, không quá rộng ngang */
                                border-radius: 6px;
                                margin: 15px auto;     /* cách trên/dưới và center */
                                letter-spacing: 3px;
                                text-align: center;
                            }
                        
                            .footer { font-size: 12px; color: #65676b; margin-top: 20px; text-align:center; }
                            @media only screen and (max-width: 600px) {
                                .container { margin: 10px; padding: 15px; }
                                .header { font-size: 24px; }
                                .code { font-size: 20px; padding: 10px 15px; }
                            }
                        </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">FacebookClone Verification</div>
                                <div class="content">
                                    Hi %s,<br>
                                    Thank you for registering on FacebookClone!<br>
                                    Your verification code is:<br>
                                    <div class="code">%s</div>
                                    <p style="margin-top:15px;">Enter this code in the app to verify your account.</p>
                                    If you did not register, please ignore this email.
                                </div>
                                <div class="footer">&copy; 2025 FacebookClone. All rights reserved.</div>
                            </div>
                        </body>
                        </html>
                        """.formatted(savedUser.getFirstName(), verificationCode);

                // Gửi HTML email
                emailService.sendHtmlMessage(savedUser.getEmail(), "Your FacebookClone verification code", message);

                // Chuyển ngay sang verify page thay vì signIn
                redirectAttributes.addFlashAttribute("success",
                        "Account created! Please check your email for the verification code.");
                String encodedEmail = URLEncoder.encode(savedUser.getEmail(), StandardCharsets.UTF_8);
                return "redirect:/facebook/auth/verify?email=" + encodedEmail;
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

    @GetMapping("/verify")
    public String verifyPage(@RequestParam(value = "email", required = false) String email, Model model) {
        // Trả về form nhập mã xác nhận và truyền email để điền sẵn
        model.addAttribute("email", email);
        return "auth/verify";
    }

    @PostMapping("/verify")
    @Transactional // đảm bảo các thay đổi được commit
    public String verifyAccountByCode(@RequestParam("email") String email,
                                      @RequestParam("code") String code,
                                      RedirectAttributes redirectAttributes) {
        // Lấy user từ DB
        User user = userRepo.findByEmail(email);
        if (user != null && code.equals(user.getVerificationCode())) {
            user.setEnabled(true);
            user.setVerificationCode(null); // xóa code
            // Không cần gọi save() nếu entity đang managed (vì @Transactional), nhưng gọi cũng không sao
            userRepo.save(user);

            redirectAttributes.addFlashAttribute("success", "Account verified successfully!");
            return "redirect:/facebook/auth/signIn";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid verification code or email");
            return "redirect:/facebook/auth/verify?email=" + email;
        }
    }


}
