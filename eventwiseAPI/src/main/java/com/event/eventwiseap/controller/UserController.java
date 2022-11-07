package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.Response;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping("/api/user")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
    private final UserService userService;
    private static Response response;
    private static final String SESSION_USERNAME = "username";

    static {
        response = new Response();
    }

    @DeleteMapping("/delete-account")
    public Response deleteAccount(HttpServletRequest req){
        HttpSession session = req.getSession();
        String username = session.getAttribute(SESSION_USERNAME).toString();
        try{
            final User user = userService.getByUsername(username);
            userService.delete(user.getId());
            response.setSuccess(true);
            response.setMessage(String.format("The account (username: %s, email: %s) has been deleted", user.getUsername(),user.getEmail()));
        }
        catch (ObjectIsNullException e){
            response.setSuccess(false);
            response.setMessage("Something went wrong while deleting the account, Error: " + e.getMessage());
        }
        if(response.isSuccess()){
            session.invalidate();
        }
        return response;
    }

    @GetMapping("/logout")
    public Response logout(HttpServletRequest req){
        HttpSession session = req.getSession();
        response.setSuccess(true);
        response.setMessage(String.format("%s - logged out", session.getAttribute(SESSION_USERNAME).toString()));
        session.invalidate();
        return response;
    }
}
