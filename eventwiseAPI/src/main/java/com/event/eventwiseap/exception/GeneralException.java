package com.event.eventwiseap.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GeneralException extends RuntimeException{
    private final String message;
}
