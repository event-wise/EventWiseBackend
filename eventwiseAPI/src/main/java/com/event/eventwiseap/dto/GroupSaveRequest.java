package com.event.eventwiseap.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class GroupSaveRequest {
    Long groupId; // For update

    @NotNull(message = "A group must have an owner")
    Long ownerId;

    @NotNull(message = "Group name cannot be null")
    @NotEmpty(message = "Group name cannot be empty")
    @Size(max = 20, min = 1, message = "Group name must contain 20 characters at most")
    private String groupName;

    @NotNull(message = "Location cannot be null")
    @NotEmpty(message = "Location cannot be empty")
    @Size(max = 20, message = "Location must contain 20 characters at most")
    private String location;

    @NotNull(message = "Description cannot be null")
    @Size(max = 500, message = "Description must contain 500 characters at most")
    private String description;
}
