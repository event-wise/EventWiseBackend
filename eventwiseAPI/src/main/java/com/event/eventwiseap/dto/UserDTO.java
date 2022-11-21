package com.event.eventwiseap.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDTO {
    Long id;

    @NotNull
    @Size(max = 20, min = 5)
    private String username;

    @NotEmpty
    @NotNull
    @Size(min = 5, max = 10)
    private String displayedName;

    @NotNull
    @NotEmpty
    @Size(max = 20)
    private String location;
}
