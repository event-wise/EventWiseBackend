package com.event.eventwiseap.dto.admin;

import lombok.Getter;

import javax.validation.constraints.*;

@Getter
public class AdminDeleteRequest {
    @NotNull
    private Long id;
}
