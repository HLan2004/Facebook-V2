package com.facebookv2.facebookBE.service;

import com.facebookv2.facebookBE.model.Status;

import java.util.List;

public interface StatusService {
    Status saveStatus(Status status);
    List<Status> getAllStatuses();
    List<Status> getAllStatusesByIdOrderByCreatedTimeDesc(Long id);
}
