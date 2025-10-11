package com.facebookv2.facebookBE.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendDTO {
    private Long id;
    private String fullName;
    private String avatar;
    // Có thể thêm trạng thái online nếu cần
}