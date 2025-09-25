package com.facebookv2.facebookBE.service.impl;

import com.facebookv2.facebookBE.model.ChatMessage;
import com.facebookv2.facebookBE.repository.ChatMessageRepository;
import com.facebookv2.facebookBE.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepo;


    @Override
    public List<ChatMessage> findByConversationId(Long conversationId) {
        return chatMessageRepo.findByConversationIdOrderByTimestampAsc(conversationId);
    }

    @Override
    public ChatMessage save(ChatMessage msg) {
        return chatMessageRepo.save(msg);
    }
}
