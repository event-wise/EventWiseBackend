package com.event.eventwiseap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupsDTO {
    @NotNull(message = "Id cannot be null")
    Long id;

    @NotNull(message = "Group name cannot be null")
    @NotEmpty(message = "Group name cannot be empty")
    @Size(max = 20, min = 1, message = "Group name must contain 20 characters at most")
    private String groupName;

    @NotNull(message = "Location cannot be null")
    @NotEmpty(message = "Location cannot be empty")
    @Size(max = 20, message = "Location must contain 20 characters at most")
    private String location;
}
