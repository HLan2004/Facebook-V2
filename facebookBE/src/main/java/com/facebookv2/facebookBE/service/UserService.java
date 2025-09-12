package com.facebookv2.facebookBE.service;

import com.facebookv2.facebookBE.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(User user);
    User getUserByEmail(String email);
}
