package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.ConversationSummaryDTO;
import com.facebookv2.facebookBE.repository.FriendshipRepository;
import com.facebookv2.facebookBE.repository.UserRepository;
import com.facebookv2.facebookBE.service.ConversationService;
import com.facebookv2.facebookBE.service.FriendshipService;
import com.facebookv2.facebookBE.service.StatusService;
import com.facebookv2.facebookBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/facebook/friendship")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public String add(@RequestParam("friendId") Long friendId, Authentication authentication, Model model) {
        String email = authentication.getName();
        friendshipService.addFriendship(email, friendId);
        return "redirect:/facebook/user/home";
    }


    @PostMapping("/remove")
    public String cancel(@RequestParam Long friendId, Authentication authentication){
        // xoá bản ghi của 2 người này trong friendship
        User currentUser = userRepository.findByEmail(authentication.getName());
        friendshipService.deleteFriendshipRecord(currentUser.getId(), friendId);
        return "redirect:/facebook/user/home";
    }

    @PostMapping("/accept")
    public String accept(@RequestParam Long friendId, Authentication authentication){
        // chuyển status từ pending sang accepted
        User currentUser = userRepository.findByEmail(authentication.getName());
        friendshipService.acceptFriendRequest(currentUser.getId(), friendId);
        return "redirect:/facebook/user/home";
    }

    @PostMapping("/decline")
    public String decline(@RequestParam Long friendId, Authentication authentication){
        // chuyển status từ pending sang declined
        User currentUser = userRepository.findByEmail(authentication.getName());
        friendshipService.declineFriendRequest(currentUser.getId(), friendId);
        return "redirect:/facebook/user/home";
    }
}