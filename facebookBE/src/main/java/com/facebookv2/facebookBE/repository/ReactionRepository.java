package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.Reaction;
import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Reaction r WHERE r.user.id = :userId AND r.status.id = :statusId")
    void deleteByUserIdAndStatusId(@Param("userId") Long userId, @Param("statusId") Long statusId);

    Boolean existsByUserAndStatus(User user, Status status);

    long countReactionByStatus(Status status);

    Long countCommentByStatus(Status status);
}
