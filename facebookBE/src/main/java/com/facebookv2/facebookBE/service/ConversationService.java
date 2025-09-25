package com.facebookv2.facebookBE.service;

import com.facebookv2.facebookBE.model.Conversation;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.ConversationSummaryDTO;

import java.util.List;

public interface ConversationService {
    Conversation findById(Long id);
    Conversation save(Conversation conv);

    List<ConversationSummaryDTO> getConversationSummaries(User user);
}
