package com.event.eventwiseap.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ObjectIsNullException extends RuntimeException{
    private final String message;
}

