package com.event.eventwiseap.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    @Size(min = 5, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 5, max = 10)
    private String displayedName;

    @NotBlank
    @Size(min = 5, max = 56)
    private String password;

    @NotBlank
    @Size(max = 20)
    private String location;

    private String role;
}
