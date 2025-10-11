package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.Friendship;
import com.facebookv2.facebookBE.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE f.user.id = :userId AND f.friend.id = :friendId")
    boolean existsFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("SELECT f FROM Friendship f WHERE (f.user.id = :userId AND f.friend.id = :friendId) " +
            "OR (f.user.id = :friendId AND f.friend.id = :userId)")
    Optional<Friendship> findFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Friendship f
        WHERE (f.user.id = :userId AND f.friend.id = :friendId)
           OR (f.user.id = :friendId AND f.friend.id = :userId)
    """)
    void deleteFriendshipRecord(@Param("userId") Long userId,
                                 @Param("friendId") Long friendId);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Friendship f
    SET f.status = com.facebookv2.facebookBE.model.FriendshipStatus.ACCEPTED
    WHERE (f.user.id = :userId AND f.friend.id = :friendId)
       OR (f.user.id = :friendId AND f.friend.id = :userId)
""")
    void acceptFriendRequest(@Param("userId") Long userId,
                             @Param("friendId") Long friendId);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Friendship f
    SET f.status = com.facebookv2.facebookBE.model.FriendshipStatus.DECLINED
    WHERE 
        ((f.user.id = :userId AND f.friend.id = :friendId)
        OR (f.user.id = :friendId AND f.friend.id = :userId))
        AND f.status = com.facebookv2.facebookBE.model.FriendshipStatus.PENDING
""")
    void declineFriendRequest(@Param("userId") Long userId, @Param("friendId") Long friendId);

    // Thêm method này vào FriendshipRepository

    @Query("SELECT f.friend FROM Friendship f WHERE f.user.id = :userId AND f.status = com.facebookv2.facebookBE.model.FriendshipStatus.ACCEPTED")
    List<User> findAcceptedFriendsAsUser(@Param("userId") Long userId);

    @Query("SELECT f.user FROM Friendship f WHERE f.friend.id = :userId AND f.status = com.facebookv2.facebookBE.model.FriendshipStatus.ACCEPTED")
    List<User> findAcceptedFriendsAsFriend(@Param("userId") Long userId);


}
