package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.JWTResponse;
import com.event.eventwiseap.dto.LoginRequest;
import com.event.eventwiseap.dto.RegisterRequest;
import com.event.eventwiseap.dto.Response;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
    private static Response response;
    static {
        response = new Response();
    }

    @PostMapping("/register")
    public Response registerUser(@RequestBody @Valid RegisterRequest request){
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
/*
        String stringRole = request.getRole();
        if (!Objects.isNull(stringRole)) {
            if ("admin".equals(stringRole)) {
                roles.add(roleService.findByName(RoleType.ROLE_ADMIN));
            }
        }
*/

        roles.add(roleService.findByName(RoleType.ROLE_USER));
        user.setRoles(roles);
        userService.create(user);
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

        return JWTResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }
}
