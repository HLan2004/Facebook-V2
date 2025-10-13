package com.facebookv2.facebookBE.service;

import com.facebookv2.facebookBE.model.Status;

import java.util.List;
import java.util.Optional;

public interface StatusService {
    Status saveStatus(Status status);
    List<Status> getAllStatuses();
    List<Status> getAllStatusesByUserIdOrderByCreatedTimeDesc(Long userId);
    Optional<Status> findById(Long id);
}
