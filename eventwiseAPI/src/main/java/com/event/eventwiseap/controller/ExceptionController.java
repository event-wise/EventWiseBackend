package com.event.eventwiseap.controller;


import com.event.eventwiseap.exception.ErrorResponse;
import com.event.eventwiseap.exception.FieldException;
import com.event.eventwiseap.exception.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
@ResponseBody
public class ExceptionController {
    @ExceptionHandler(GeneralException.class)
    public ErrorResponse handle(HttpServletRequest req, GeneralException ex){
        return new ErrorResponse(req.getRequestURI(), ex.getMessage());
    }

    @ExceptionHandler(FieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FieldException fieldHandle(HttpServletRequest req, FieldException ex){
        ex.setPath(req.getRequestURI());
        return ex;
    }
}
