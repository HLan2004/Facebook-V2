package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository

public interface StatusRepository extends JpaRepository<Status, Long> {
    List<Status> findAllByOrderByCreatedTimeDesc();
    List<Status> getAllStatusesByUserIdOrderByCreatedTimeDesc(Long userId);
}
