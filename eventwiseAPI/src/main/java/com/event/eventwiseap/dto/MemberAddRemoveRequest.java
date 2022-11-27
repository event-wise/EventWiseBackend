package com.event.eventwiseap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberAddRemoveRequest {
    @NotNull(message = "The action must have an actor")
    Long actorUserId;

    @NotNull(message = "The action must have a subject")
    Long subjectUserId;

    @NotNull(message = "The action must belong to a group")
    Long groupId;
}
