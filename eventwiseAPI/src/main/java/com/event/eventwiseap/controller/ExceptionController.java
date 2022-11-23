package com.event.eventwiseap.controller;


import com.event.eventwiseap.exception.ErrorResponse;
import com.event.eventwiseap.exception.GeneralException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
@ResponseBody
public class ExceptionController {
    @ExceptionHandler(GeneralException.class)
    public ErrorResponse handle(HttpServletRequest req, GeneralException ex){
        return new ErrorResponse(req.getRequestURI(), ex.getMessage());
    }

}
