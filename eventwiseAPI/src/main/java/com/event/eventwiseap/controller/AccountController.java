package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.*;
import com.event.eventwiseap.exception.GeneralException;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.security.JWTUtils;
import com.event.eventwiseap.security.UserDetailsImpl;
import com.event.eventwiseap.service.RoleService;
import com.event.eventwiseap.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final UserService userService;
    private final RoleService roleService;
    private static final String SESSION_USERNAME = "username";
    private static Response response;
    static {
        response = new Response();
    }

    @PostMapping("/register")
    public Response registerUser(@RequestBody @Valid RegisterRequest request) throws ConstraintViolationException {
        if (userService.existsByUsername(request.getUsername())){
            response.setSuccess(false);
            response.setMessage("Username is already taken");
            return response;
        }
        if (userService.existsByEmail(request.getEmail())){
            response.setSuccess(false);
            response.setMessage("Email is already in use");
            return response;
        }
        User user = User.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .displayedName(request.getDisplayedName())
                        .password(encoder.encode(request.getPassword()))
                        .location(request.getLocation())
                        .build();

        Set<Role> roles = new HashSet<>();

        roles.add(roleService.findByName(RoleType.ROLE_USER));
        user.setRoles(roles);
        userService.save(user);
        response.setSuccess(true);
        response.setMessage("Registration completed");
        log.info("New user (name = {}) is registered to the system", user.getUsername());
        return response;
    }

    @PostMapping("/login")
    public JWTResponse authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
        log.info("User (name = {}) logged in to the system", loginRequest.getUsername());
        JWTResponse jwtResponse = JWTResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
        jwtResponse.setToken(jwtResponse.getAuthType() + " " + jwt);
        return jwtResponse;
    }

    @GetMapping("/logout")
    public Response logout(HttpServletRequest req){
        HttpSession session = req.getSession();
        response.setSuccess(true);
        response.setMessage(String.format("%s - logged out", session.getAttribute(SESSION_USERNAME).toString()));
        session.invalidate();
        return response;
    }

    @DeleteMapping("/delete-account")
    public Response deleteAccount(HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
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

    @GetMapping("/profile")
    public UserDTO profile(@RequestParam("id") @NotEmpty @NotNull Long id, HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(id);
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        return UserDTO.builder().id(user.getId()).username(user.getUsername()).displayedName(user.getDisplayedName())
                .location(user.getLocation()).build();
    }

    @PostMapping("/update-profile")
    public Response updateProfile(@RequestBody @Valid ProfileUpdateRequest profileUpdateRequest, HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(profileUpdateRequest.getId());
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        user.setEmail(profileUpdateRequest.getEmail());
        user.setDisplayedName(profileUpdateRequest.getDisplayedName());
        user.setLocation(profileUpdateRequest.getLocation());
        userService.save(user);
        response.setSuccess(true);
        response.setMessage("Profile updated successfully");
        return response;
    }

    @PostMapping("/change-password")
    public Response changePassword(@RequestBody @Valid PasswordChangeRequest passwordChangeRequest, HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(passwordChangeRequest.getId());
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }

        if (!encoder.matches(passwordChangeRequest.getCurrentPassword(), user.getPassword())){
            System.out.println("User pwd: " + user.getPassword());
            System.out.println("Curr pwd: " + passwordChangeRequest.getCurrentPassword());
            response.setSuccess(false);
            response.setMessage("Current password is wrong");
            return response;
        }
        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())){
            response.setSuccess(false);
            response.setMessage("New and confirm passwords do not match");
            return response;
        }
        user.setPassword(encoder.encode(passwordChangeRequest.getNewPassword()));
        userService.save(user);
        response.setSuccess(true);
        response.setMessage("Password change successful");
        return response;
    }
}
