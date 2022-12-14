package com.event.eventwiseap.dto.admin;

import lombok.Getter;

import javax.validation.constraints.*;

@Getter
public class AdminUserCreateRequest {
    @NotBlank
    @Pattern(regexp = "^(admin|user)$",message = "Role can only be admin or user")
    private  String role;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 5, max = 20, message = "Username must contain 20 characters at most, 5 characters at least")
    private String username;

    @NotBlank(message = "E-mail cannot be empty")
    @Size(max = 50, message = "E-mail must contain less than 50 characters")
    @Email(message = "Not a proper E-mail")
    private String email;

    @NotEmpty(message = "Displayed name cannot be empty")
    @NotNull(message = "Displayed name cannot be null")
    @Size(min = 5, max = 10, message = "Displayed name must contain 10 characters at most, 5 characters at least")
    private String displayedName;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 5, max = 56, message = "Password must contain 56 characters at most, 5 characters at least")
    private String password;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 5, max = 56, message = "Confirmation password must contain 56 characters at most, 5 characters at least")
    private String confirmPassword;

    @NotNull(message = "Location cannot be null")
    @NotEmpty(message = "Location cannot be empty")
    @Size(max = 20, message = "Location must contain 20 characters at most")
    private String location;
}
