package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.repository.StatusRepository;
import com.facebookv2.facebookBE.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/facebook/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final StatusRepository statusRepository;

    public ProfileController(UserRepository userRepository, StatusRepository statusRepository) {
        this.userRepository = userRepository;
        this.statusRepository = statusRepository;
    }

    @GetMapping
    public String profile(Model model) {

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));


        List<Status> statuses = statusRepository.findAllByOrderByCreatedTimeDesc();


        model.addAttribute("user", user);
        model.addAttribute("statuses", statuses);

        return "user/profile";
    }
}

