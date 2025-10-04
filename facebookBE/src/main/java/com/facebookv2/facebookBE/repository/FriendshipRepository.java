package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE f.user.id = :userId AND f.friend.id = :friendId")
    boolean existsFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);
    @Query("SELECT f FROM Friendship f WHERE (f.user.id = :userId AND f.friend.id = :friendId) " +
            "OR (f.user.id = :friendId AND f.friend.id = :userId)")
    Optional<Friendship> findFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

}
