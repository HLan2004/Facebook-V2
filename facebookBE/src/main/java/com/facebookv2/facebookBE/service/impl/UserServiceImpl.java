package com.facebookv2.facebookBE.service.impl;

import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.UserPrincipal;
import com.facebookv2.facebookBE.repository.UserRepository;
import com.facebookv2.facebookBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        if (!user.isEnabled()) {
            // Nếu user chưa verify email, không cho đăng nhập
            throw new org.springframework.security.authentication.DisabledException(
                    "Account not verified yet. Please check your email."
            );
        }

        return UserPrincipal.build(user);
    }

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> searchByName(String keyword) {
        return userRepository.searchByName(keyword);
    }

    @Override
    public void updateUserImages(String email, String avatarFileName, String coverFileName) {
        // Tìm user trong database bằng email
        User user = userRepository.findByEmail(email);

        if (user != null) {
            // Chỉ cập nhật nếu có tên file mới được truyền vào
            if (avatarFileName != null) {
                user.setAvatar(avatarFileName);
            }
            if (coverFileName != null) {
                user.setBackGround(coverFileName);
            }

            // Lưu lại user mà KHÔNG mã hóa lại mật khẩu
            userRepository.save(user);
        } else {
            // Ném ra exception nếu không tìm thấy user
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}
