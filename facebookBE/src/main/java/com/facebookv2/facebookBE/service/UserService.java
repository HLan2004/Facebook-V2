package com.facebookv2.facebookBE.service;

import com.facebookv2.facebookBE.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User save(User user);
    User getUserByEmail(String email);
    User findById(Long senderId);
    List<User> searchByName(String name);
}
