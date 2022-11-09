package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class HomeController {

    private static Response response;
    static {
        response = new Response();
    }
    @GetMapping("/")
    public Response home_page(){
        response.setSuccess(true);
        return response;
    }
}
