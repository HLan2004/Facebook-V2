package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {
    List<Status> findAllByOrderByCreatedTimeDesc();
}
