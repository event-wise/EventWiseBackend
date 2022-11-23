package com.event.eventwiseap.dto;

import lombok.Getter;

import javax.validation.constraints.*;

@Getter
public class ProfileUpdateRequest {
    private Long id;

    @NotBlank(message = "E-mail cannot be empty")
    @Size(max = 50, message = "E-mail must contain less than 50 characters")
    @Email(message = "Not a proper E-mail")
    private String email;

    @NotEmpty(message = "Displayed name cannot be empty")
    @NotNull(message = "Displayed name cannot be null")
    @Size(min = 5, max = 10, message = "Displayed name must contain 10 characters at most, 5 characters at least")
    private String displayedName;

    @NotNull(message = "Location cannot be null")
    @NotEmpty(message = "Location cannot be empty")
    @Size(max = 20, message = "Location must contain 20 characters at most")
    private String location;
}
