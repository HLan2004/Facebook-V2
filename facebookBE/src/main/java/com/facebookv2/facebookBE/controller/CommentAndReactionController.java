package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Comment;
import com.facebookv2.facebookBE.model.Reaction;
import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.repository.CommentReactionRepository;
import com.facebookv2.facebookBE.repository.ReactionRepository;
import com.facebookv2.facebookBE.repository.StatusRepository;
import com.facebookv2.facebookBE.repository.UserRepository;
import com.facebookv2.facebookBE.service.StatusService;
import com.facebookv2.facebookBE.service.UserService;
import com.facebookv2.facebookBE.service.impl.CommentAndReactionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/facebook/car")
public class CommentAndReactionController {
    private final UserService userService;
    private final StatusService statusService;
    private final CommentAndReactionService commentAndReactionService;

    public CommentAndReactionController( UserService userService, StatusService statusService, CommentAndReactionService commentAndReactionService) {
        this.userService = userService;
        this.statusService = statusService;
        this.commentAndReactionService = commentAndReactionService;
    }

    @PostMapping("/reaction/add")
    @ResponseBody  // trả về dữ liệu, không render view
    public Map<String, Object> addReaction(@RequestBody Map<String, Long> data,
                              Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        Long statusId = data.get("statusId"); // lấy từ JSON body
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);

        Optional<Status> optionalStatus = statusService.findById(statusId);
        if(optionalStatus.isPresent()) {
            commentAndReactionService.saveReaction(currentUser, optionalStatus.get());
            result.put("success", true);
            result.put("message", "Đã thích");
        } else {
            result.put("success", false);
            result.put("message", "Status không tồn tại");
        }

        return result;
    }

    @PostMapping("/reaction/remove")
    @ResponseBody
    public Map<String, Object> removeReaction(@RequestBody Map<String, Long> data,
                               Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        Long statusId = data.get("statusId");
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Optional<Status> optionalStatus = statusService.findById(statusId);
        if(optionalStatus.isPresent()) {
            commentAndReactionService.deleteReactionByUserAndStatus(currentUser, optionalStatus.get());
            result.put("success", true);
            result.put("message", "Đã bỏ thích");
        } else {
            result.put("success", false);
            result.put("message", "Status không tồn tại");
        }
        return result;
    }

    @PostMapping("/comment/add")
    @ResponseBody
    public Map<String, Object> addComment(@RequestParam("statusId") Long statusId,
                           @RequestParam("content") String content,
                           Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Map<String, Object> result = new HashMap<>();
        Optional<Status> optionalStatus = statusService.findById(statusId);
        if(optionalStatus.isPresent()) {
            commentAndReactionService.saveComment(currentUser,optionalStatus.get(),content);
            result.put("success", true);
            result.put("message", "Bình luận đã gửi");
        } else {
            result.put("success", false);
            result.put("message", "không tìm thấy post");
        }
        return result;
    }

    @GetMapping("/comment/list")
    @ResponseBody
    public List<Map<String, Object>> getComments(@RequestParam Long statusId) {
        List<Comment> comments = commentAndReactionService.getCommentsByStatus(statusId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment c : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("content", c.getContent());
            map.put("createdAt", c.getCreatedAt());
            map.put("userName", c.getUser().getFirstName() + " " + c.getUser().getLastName());
            map.put("avatar", c.getUser().getAvatar());
            result.add(map);
        }
        return result;
    }





}
