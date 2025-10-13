package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {
    List<UserAvatar> findByUserIdOrderByUploadedAtDesc(Long userId);
}
