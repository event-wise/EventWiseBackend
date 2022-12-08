package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.Response;
import com.event.eventwiseap.dto.UserDTO;
import com.event.eventwiseap.dto.admin.UserCreateRequest;
import com.event.eventwiseap.dto.admin.UserUpdateRequest;
import com.event.eventwiseap.exception.FieldException;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.security.JWTUtils;
import com.event.eventwiseap.service.RoleService;
import com.event.eventwiseap.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
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

    @GetMapping("/get-all-users")
    public List<UserDTO> getAllUsers(HttpServletRequest req){
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOS = new ArrayList<>();
        for(User user: users)
            userDTOS.add(new UserDTO(user.getId(),  user.getUsername(), user.getDisplayedName(), user.getLocation()));

        return userDTOS;
    }

    @PostMapping("/create-user")
    public Response createUser(@RequestBody @Valid UserCreateRequest userCreateRequest, Errors errors){
        fieldErrorChecker(errors);

        List<String> messages = new ArrayList<>();
        if (userService.existsByUsername(userCreateRequest.getUsername()))
            messages.add("Username is already taken");
        if (userService.existsByEmail(userCreateRequest.getEmail()))
            messages.add("Email is already in use");
        if (!userCreateRequest.getPassword().equals(userCreateRequest.getConfirmPassword()))
            messages.add("New and confirm passwords do not match");
        if(!messages.isEmpty())
            throw new FieldException(null, messages);

        User user = User.builder()
                .username(userCreateRequest.getUsername())
                .email(userCreateRequest.getEmail())
                .displayedName(userCreateRequest.getDisplayedName())
                .password(encoder.encode(userCreateRequest.getPassword()))
                .location(userCreateRequest.getLocation())
                .build();

        String stringRole = userCreateRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (!Objects.isNull(stringRole)) {
            if ("admin".equals(stringRole)) {
                roles.add(roleService.findByName(RoleType.ROLE_ADMIN));
            }
        }
        roles.add(roleService.findByName(RoleType.ROLE_USER));

        user.setRoles(roles);
        userService.save(user);
        response.setSuccess(true);
        response.setMessage("New user creation is successful");
        return response;
    }

    @PostMapping("update-user-details")
    public Response updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest, Errors errors){
        fieldErrorChecker(errors);

        List<String> messages = new ArrayList<>();
        if (userService.existsByUsername(userUpdateRequest.getUsername()))
            messages.add("Username is already taken");
        if (userService.existsByEmail(userUpdateRequest.getEmail()))
            messages.add("Email is already in use");
        if(!messages.isEmpty())
            throw new FieldException(null, messages);

        User user = userService.getById(userUpdateRequest.getId());
        String stringRole = userUpdateRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (!Objects.isNull(stringRole)) {
            if ("admin".equals(stringRole)) {
                roles.add(roleService.findByName(RoleType.ROLE_ADMIN));
            }
        }
        roles.add(roleService.findByName(RoleType.ROLE_USER));

        user.setRoles(roles);
        user.setUsername(userUpdateRequest.getUsername());
        user.setEmail(userUpdateRequest.getEmail());
        user.setDisplayedName(userUpdateRequest.getDisplayedName());
        user.setLocation(userUpdateRequest.getLocation());
        userService.save(user);
        response.setSuccess(true);
        response.setMessage("User details updated successfully");
        return  response;
    }

    @DeleteMapping("/delete-user")
    public Response deleteUser(@RequestParam("userId") @NotEmpty @NotNull Long userId){
        try {
            User user = userService.getById(userId);
            userService.delete(userId);
            response.setSuccess(true);
            response.setMessage(String.format("The account (username: %s, email: %s) has been deleted", user.getUsername(),user.getEmail()));
        }
        catch (ObjectIsNullException e){
            response.setSuccess(false);
            response.setMessage("Something went wrong while deleting the account, Error: " + e.getMessage());
        }

        return response;
    }


}
