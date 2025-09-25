package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.service.StorageService;
import com.facebookv2.facebookBE.service.UserService;
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
@RequestMapping("/facebook/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private StatusServiceImpl statusServiceImpl;


    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        List<Status> statuses = statusServiceImpl.getAllStatuses();
        model.addAttribute("user", user);
        model.addAttribute("statuses", statuses);
        model.addAttribute("newStatus", new Status());
        return "user/home";
    }

    @GetMapping("/messages")
    public String messages(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        model.addAttribute("user", user);


        // TODO: nếu bạn có MessageService/ConversationService, thay đoạn dưới bằng call service thật
        // model.addAttribute("conversations", conversationService.getConversationsForUser(user.getId()));
        // model.addAttribute("activeConversation", conversationService.getDefaultConversation(user.getId()));
        // model.addAttribute("unreadMessages", messageService.countUnreadForUser(user.getId()));


        // Hiện tại tạm thời set bằng 0 để badge không hiển thị
        model.addAttribute("unreadMessages", 0);


        return "user/messenger";
    }


}


