package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.repository.StatusRepository;
import com.facebookv2.facebookBE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    public String profile(Model model, Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email);


        List<Status> statuses = statusRepository.findAllByIdOrderByCreatedTimeDesc(user.getId());


        model.addAttribute("user", user);
        model.addAttribute("statuses", statuses);

        return "user/profile";
    }
}

