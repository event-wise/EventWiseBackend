package com.event.eventwiseap.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'")
    private LocalDateTime dateTime;
}
