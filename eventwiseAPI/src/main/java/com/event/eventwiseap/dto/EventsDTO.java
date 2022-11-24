package com.event.eventwiseap.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventsDTO {
    @NotNull(message = "Id cannot be null")
    Long id;

    @NotNull(message = "Event name cannot be null")
    @NotEmpty(message = "Event name cannot be empty")
    @Size(max = 50, message = "Event name must contain 50 characters at most")
    private String eventName;

    @NotNull(message = "Event mush have a time")
    private LocalDateTime dateTime;
}
