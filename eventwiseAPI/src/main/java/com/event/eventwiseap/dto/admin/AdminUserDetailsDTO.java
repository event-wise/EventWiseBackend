package com.event.eventwiseap.dto.admin;

import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdminUserDetailsDTO {
    Long id;

    @NotNull(message = "Username cannot be null")
    @Size(max = 20, min = 5, message = "Username must contain 20 characters at most, 5 characters at least")
    private String username;

    private String email;

    @NotEmpty(message = "Displayed name cannot be empty")
    @NotNull(message = "Displayed name cannot be null")
    @Size(min = 5, max = 10, message = "Displayed name must contain 10 characters at most, 5 characters at least")
    private String displayedName;

    @NotNull(message = "Location cannot be null")
    @NotEmpty(message = "Location cannot be empty")
    @Size(max = 20, message = "Location must contain 20 characters at most")
    private String location;

    private Set<Event> acceptedEvents;

    private Set<Group> groups;

    private Set<Role> roles;
}
