package com.event.eventwiseap.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventSaveRequest {
    private Long eventId; // For update

    @NotNull(message = "The event must belong to a group")
    private Long groupId;

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
}
