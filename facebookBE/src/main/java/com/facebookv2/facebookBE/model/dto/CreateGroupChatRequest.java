
package com.facebookv2.facebookBE.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateGroupChatRequest {
    private String groupName;
    private List<Long> participantIds;
}