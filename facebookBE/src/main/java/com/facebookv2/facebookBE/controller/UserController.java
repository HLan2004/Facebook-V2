package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.ChatMessage;
import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.ConversationSummaryDTO;
import com.facebookv2.facebookBE.model.dto.UserDTO;
import com.facebookv2.facebookBE.service.*;
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
    private StatusService statusService;

    @Autowired
    private ConversationService conversationService;


    @GetMapping("/home")
    public String home( Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        List<Status> statuses = statusService.getAllStatuses();
        model.addAttribute("user", user);
        model.addAttribute("statuses", statuses);
        model.addAttribute("newStatus", new Status());
        return "user/home";
    }

    @GetMapping("/messages")
    public String messages(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        List<ConversationSummaryDTO> conversations = conversationService.getConversationSummaries(user);
        model.addAttribute("conversations", conversations);

        return "user/messenger";
    }
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        List<User> users = userService.searchByName(keyword);
        model.addAttribute("users", users);
        return "user/search";
    }
}


