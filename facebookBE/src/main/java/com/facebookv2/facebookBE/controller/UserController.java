package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.ChatMessage;
import com.facebookv2.facebookBE.model.Friendship;
import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.ConversationSummaryDTO;
import com.facebookv2.facebookBE.model.dto.UserDTO;
import com.facebookv2.facebookBE.repository.FriendshipRepository;
import com.facebookv2.facebookBE.service.*;
import com.facebookv2.facebookBE.service.impl.CommentAndReactionService;
import com.facebookv2.facebookBE.service.impl.StatusServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/facebook/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private CommentAndReactionService commentAndReactionService;


    @GetMapping("/home")
    public String home( Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        List<Status> statuses = statusService.getAllStatuses();
        // ✅ Map lưu bài nào đã được like
        Map<Long, Boolean> likedMap = new HashMap<>();
        // Map lưu số lượt like
        Map<Long, Long> likeCountMap = new HashMap<>();
        // Map lưu số lượt comment
        Map<Long, Long> commentCountMap = new HashMap<>();
        for (Status status : statuses) {
            boolean liked = commentAndReactionService.existsByUserAndStatus(user, status);
            likedMap.put(status.getId(), liked);
            Long likeCount = commentAndReactionService.countReactionByStatus(status);
            likeCountMap.put(status.getId(), likeCount);
            Long commentCount = commentAndReactionService.countCommentByStatus(status);
            commentCountMap.put(status.getId(), commentCount);
        }
        model.addAttribute("user", user);
        model.addAttribute("statuses", statuses);
        model.addAttribute("newStatus", new Status());
        model.addAttribute("likedMap", likedMap);
        model.addAttribute("likeCountMap", likeCountMap);
        model.addAttribute("commentCountMap", commentCountMap);
        return "user/home";
    }

    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model, Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        List<User> users = userService.searchByName(keyword);
        friendshipService.checkFriendship(currentUser, users, model);
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        return "user/search";
    }

//    @PostMapping("/add")
//    public String add(@RequestParam("friendId") Long friendId, Authentication authentication, Model model) {
//        String email = authentication.getName();
//        friendshipService.addFriendship(email, friendId);
//        return "redirect:/facebook/user/home";
//    }
}


