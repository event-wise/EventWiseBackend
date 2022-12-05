package com.event.eventwiseap.dto;

import lombok.Getter;

import javax.validation.constraints.*;

@Getter
public class ProfileUpdateRequest {

    @NotEmpty(message = "Displayed name cannot be empty")
    @NotNull(message = "Displayed name cannot be null")
    @Size(min = 5, max = 10, message = "Displayed name must contain 10 characters at most, 5 characters at least")
    private String displayedName;

    @NotNull(message = "Location cannot be null")
    @NotEmpty(message = "Location cannot be empty")
    @Size(max = 20, message = "Location must contain 20 characters at most")
    private String location;
}
