package com.event.eventwiseap.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@SuperBuilder
public class RegisterRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 5, max = 20, message = "Username must contain 20 characters at most, 5 characters at least")
    private String username;

    @NotBlank(message = "E-mail cannot be empty")
    @Size(max = 50, message = "E-mail must contain less than 50 characters")
    @Email(message = "Not a proper E-mail")
    private String email;

    @NotBlank(message = "Displayed name cannot be empty")
    @Size(min = 5, max = 10, message = "Displayed name must contain 10 characters at most, 5 characters at least")
    private String displayedName;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 5, max = 56, message = "Password must contain 56 characters at most, 5 characters at least")
    private String password;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 5, max = 56, message = "Confirmation password must contain 56 characters at most, 5 characters at least")
    private String confirmPassword;

    @NotBlank(message = "Location cannot be empty")
    @Size(max = 20, message = "Location must contain 20 characters at most")
    private String location;
}
