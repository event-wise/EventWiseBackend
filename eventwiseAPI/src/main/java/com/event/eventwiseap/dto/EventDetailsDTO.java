package com.event.eventwiseap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailsDTO {
    private Long eventId;

    @NotNull(message = "The event must belong to a group")
    private Long groupId;

    @NotNull(message = "Organizer indicator cannot be null")
    private boolean organizer;

    @NotNull(message = "Event name cannot be null")
    @NotEmpty(message = "Event name cannot be empty")
    @Size(max = 50,message = "Event name must contain 50 characters at most")
    private String eventName;

    @NotNull(message = "Datetime cannot be null")
    private LocalDateTime dateTime;

    @NotNull(message = "Location cannot be null")
    @NotEmpty(message = "Location cannot be empty")
    @Size(max = 20, message = "Location must contain 20 characters at most")
    private String location;

    @NotNull(message = "Type cannot be null")
    @NotEmpty(message = "Type cannot be empty")
    @Size(max = 20, message = "Type must contain 20 characters at most")
    private String type;

    @NotNull(message = "Description cannot be null")
    @Size(max = 500, message = "Description must contain 500 characters at most")
    private String description;

    private List<String> acceptedMembers;

    private boolean accepted;
}
