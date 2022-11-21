package com.event.eventwiseap.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class ProfileUpdateRequest {
    private Long id;

    @NotNull
    @NotEmpty
    @Size(max = 50)
    @Email
    private String email;

    @NotEmpty
    @NotNull
    @Size(min = 5, max = 10)
    private String displayedName;

    @NotNull
    @NotEmpty
    @Size(max = 20)
    private String location;
}
