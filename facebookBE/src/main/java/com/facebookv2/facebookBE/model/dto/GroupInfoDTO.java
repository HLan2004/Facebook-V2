package com.facebookv2.facebookBE.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfoDTO {
    private Long conversationId;
    private String groupName;
    private String avatar;
    private boolean isGroup;
}

