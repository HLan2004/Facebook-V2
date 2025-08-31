package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}
