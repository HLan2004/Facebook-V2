package com.facebookv2.facebookBE.service;

import com.facebookv2.facebookBE.model.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessage> findByConversationId(Long conversationId);
    ChatMessage save(ChatMessage msg);
}
