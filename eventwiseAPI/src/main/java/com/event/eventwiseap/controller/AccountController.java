package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.*;
import com.event.eventwiseap.exception.FieldException;
import com.event.eventwiseap.exception.GeneralException;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.security.JWTUtils;
import com.event.eventwiseap.security.UserDetailsImpl;
import com.event.eventwiseap.service.RoleService;
import com.event.eventwiseap.service.UserService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
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

    private void fieldErrorChecker(Errors errors) throws FieldException{
        if(errors.hasErrors())
        {
            List<String> messages = new ArrayList<>();
            for (FieldError fieldError: errors.getFieldErrors()){
                messages.add(fieldError.getDefaultMessage());
            }
            throw new FieldException(null, messages);
        }
    }
    @PostMapping("/register")
    public Response registerUser(@RequestBody @Valid RegisterRequest request, Errors errors) throws ConstraintViolationException {
        fieldErrorChecker(errors);
        List<String> messages = new ArrayList<>();
        if (userService.existsByUsername(request.getUsername()))
            messages.add("Username is already taken");
        if (userService.existsByEmail(request.getEmail()))
            messages.add("Email is already in use");
        if (!request.getPassword().equals(request.getConfirmPassword()))
            messages.add("New and confirm passwords do not match");
        if(!messages.isEmpty())
            throw new FieldException(null, messages);
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
    public JWTResponse authenticateUser(@RequestBody @Valid LoginRequest loginRequest, Errors errors){
        fieldErrorChecker(errors);
        if(!userService.existsByUsername(loginRequest.getUsername()))
            throw new GeneralException("There is no such an account");
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
    @PreAuthorize("hasRole('ROLE_USER')")
    public Response logout(HttpServletRequest req){
        HttpSession session = req.getSession(false);
        String user = session.getAttribute(SESSION_USERNAME).toString();
        SecurityContextHolder.clearContext();
        session = req.getSession(false);
        if(session != null)
            session.invalidate();
        response.setSuccess(true);
        response.setMessage(String.format("%s - logged out", user));
        return response;
    }

    @DeleteMapping("/delete-account")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Response deleteAccount(HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        final User user = userService.getByUsername(username);
        if(Objects.isNull(user))
            throw new GeneralException("There is no such a user");
        userService.delete(user.getId());
        response.setSuccess(true);
        response.setMessage(String.format("The account (username: %s, email: %s) has been deleted", user.getUsername(),user.getEmail()));
        if(response.isSuccess()){
            session.invalidate();
        }
        return response;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserDTO profile(HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getByUsername(username);
        return UserDTO.builder().id(user.getId()).username(user.getUsername()).displayedName(user.getDisplayedName())
                .location(user.getLocation()).build();
    }

    @PostMapping("/update-profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Response updateProfile(@RequestBody @Valid ProfileUpdateRequest profileUpdateRequest, Errors errors, HttpServletRequest req){
        fieldErrorChecker(errors);
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getByUsername(username);
        user.setDisplayedName(profileUpdateRequest.getDisplayedName());
        user.setLocation(profileUpdateRequest.getLocation());
        userService.save(user);
        response.setSuccess(true);
        response.setMessage("Profile updated successfully");
        return response;
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Response changePassword(@RequestBody @Valid PasswordChangeRequest passwordChangeRequest,Errors errors, HttpServletRequest req){
        fieldErrorChecker(errors);
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getByUsername(username);
        List<String> messages = new ArrayList<>();
        if (!encoder.matches(passwordChangeRequest.getCurrentPassword(), user.getPassword()))
            messages.add("Current password is wrong");
        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword()))
            messages.add("New and confirm passwords do not match");
        if(!messages.isEmpty())
            throw new FieldException(null, messages);
        user.setPassword(encoder.encode(passwordChangeRequest.getNewPassword()));
        userService.save(user);
        response.setSuccess(true);
        response.setMessage("Password change successful");
        return response;
    }
}
