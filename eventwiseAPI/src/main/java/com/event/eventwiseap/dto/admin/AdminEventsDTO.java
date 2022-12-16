package com.event.eventwiseap.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AdminEventsDTO {
    private Long eventId;

    @NotNull(message = "Event name cannot be null")
    @NotEmpty(message = "Event name cannot be empty")
    @Size(max = 50,message = "Event name must contain 50 characters at most")
    private String eventName;

    @NotNull(message = "The event must belong to a group")
    private Long groupId;

    @NotNull
    private String groupName;

    @NotNull
    private Long organizerId;

    @NotNull
    private String organizerName;

    @NotNull(message = "Datetime cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'")
    private LocalDateTime dateTime;

    @NotNull(message = "Creation time cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'")
    private LocalDateTime creationTime;

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
