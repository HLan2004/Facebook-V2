package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.ChatMessage;
import com.facebookv2.facebookBE.model.Conversation;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.ChatMessageDTO;
import com.facebookv2.facebookBE.model.response.ChatMessageResponse;
import com.facebookv2.facebookBE.service.ChatMessageService;
import com.facebookv2.facebookBE.service.ConversationService;
import com.facebookv2.facebookBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat/{conversationId}")
    public void send(@DestinationVariable Long conversationId,
                     @Payload ChatMessageDTO incoming,
                     Authentication authentication) {

        // Xác định user thật từ Authentication
        String email = authentication.getName();
        User sender = userService.getUserByEmail(email);
        if (sender == null) return;

        Conversation conv = conversationService.findById(conversationId);
        if (conv == null) return;

        ChatMessage message = new ChatMessage();
        message.setConversation(conv);
        message.setSender(sender);
        message.setContent(incoming.getContent());
        message.setTimestamp(LocalDateTime.now());

        ChatMessage saved = chatMessageService.save(message);

        // Response đầy đủ để client render
        ChatMessageResponse response = new ChatMessageResponse(
                saved.getId(),
                saved.getContent(),
                sender.getId(),          // để client biết ai gửi
                sender.getFirstName() + " " + sender.getLastName(),
                saved.getTimestamp().toString()
        );

        template.convertAndSend("/topic/conversation/" + conversationId, response);
    }

}
