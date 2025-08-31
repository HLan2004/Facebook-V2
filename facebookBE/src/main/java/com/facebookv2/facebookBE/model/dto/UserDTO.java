package com.facebookv2.facebookBE.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Getter
@Setter
public class UserDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private MultipartFile imageUrl;

    private Integer birthDay;
    private Integer birthMonth;
    private Integer birthYear;

    private String gender;

    private Set<String> roles;
}
