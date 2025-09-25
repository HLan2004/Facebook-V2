package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.ChatMessage;
import com.facebookv2.facebookBE.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Lấy toàn bộ tin nhắn của 1 conversation (theo thứ tự thời gian tăng dần)
    List<ChatMessage> findByConversationIdOrderByTimestampAsc(Long conversationId);


    ChatMessage findTopByConversationOrderByTimestampDesc(Conversation conversation);

}
