package com.facebookv2.facebookBE.service.impl;

import com.facebookv2.facebookBE.model.ChatMessage;
import com.facebookv2.facebookBE.model.Conversation;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.ConversationSummaryDTO;
import com.facebookv2.facebookBE.repository.ChatMessageRepository;
import com.facebookv2.facebookBE.repository.ConversationRepository;
import com.facebookv2.facebookBE.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepo;

    @Autowired
    private ChatMessageRepository chatMessageRepo;


    @Override
    public Conversation findById(Long id) { return conversationRepo.findById(id).orElse(null); }

    @Override
    public Conversation save(Conversation conv) { return conversationRepo.save(conv); }

    @Override
    public List<ConversationSummaryDTO> getConversationSummaries(User user) {
        List<Conversation> conversations = conversationRepo.findByParticipantsContaining(user);

        List<ConversationSummaryDTO> summaries = new ArrayList<>();
        for (Conversation conversation : conversations) {
            ChatMessage lastMessage = chatMessageRepo.findTopByConversationOrderByTimestampDesc(conversation);

            summaries.add(new ConversationSummaryDTO(
                    conversation.getId(),
                    conversation.isGroup() ? conversation.getName() : "Private Chat",
                    lastMessage != null ? lastMessage.getContent() : "",
                    lastMessage != null ? lastMessage.getTimestamp() : null
            ));
        }
        return summaries;
    }

}
