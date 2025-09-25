package com.facebookv2.facebookBE.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationSummaryDTO {
    private Long conversationId;
    private String conversationName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
