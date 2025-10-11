package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.Comment;
import com.facebookv2.facebookBE.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.status.id = :statusId")
    long countByStatusId(@Param("statusId") Long statusId);

    Long countCommentByStatus(Status status);

    List<Comment> findByStatusIdOrderByCreatedAtAsc(Long statusId);

}
