package com.event.eventwiseap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class PasswordChangeRequest {

    @NotBlank(message = "Current password cannot be empty")
    @Size(min = 5, max = 56, message = "Current password must contain 56 characters at most, 5 characters at least")
    private String currentPassword;

    @NotBlank(message = "New password cannot be empty")
    @Size(min = 5, max = 56, message = "New password must contain 56 characters at most, 5 characters at least")
    private String newPassword;

    @NotBlank(message = "Confirm password cannot be empty")
    @Size(min = 5, max = 56, message = "Confirm password must contain 56 characters at most, 5 characters at least")
    private String confirmPassword;
}
