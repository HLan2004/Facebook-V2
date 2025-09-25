package com.facebookv2.facebookBE.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String content;
    private Long senderId;
    private String senderFullName;
    private String timestamp;
}
