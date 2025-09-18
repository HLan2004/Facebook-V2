package com.facebookv2.facebookBE.service.impl;

import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.repository.StatusRepository;
import com.facebookv2.facebookBE.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusServiceImpl implements StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public Status saveStatus(Status status) {
        return statusRepository.save(status);
    }

    @Override
    public List<Status> getAllStatuses() {

        return statusRepository.findAllByOrderByCreatedTimeDesc();
    }
    @Override
    public List<Status> getAllStatusesByIdOrderByCreatedTimeDesc(Long id) {
        return statusRepository.findAllByIdOrderByCreatedTimeDesc(id);
    }
}
