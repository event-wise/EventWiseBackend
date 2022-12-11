package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.GroupSaveRequest;
import com.event.eventwiseap.dto.Response;
import com.event.eventwiseap.dto.UserDTO;
import com.event.eventwiseap.dto.admin.*;
import com.event.eventwiseap.exception.FieldException;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.security.JWTUtils;
import com.event.eventwiseap.service.GroupService;
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
    private final GroupService groupService;
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
    public List<AdminUserDTO> getAllUsers(HttpServletRequest req){
        List<User> users = userService.getAllUsers();
        List<AdminUserDTO> adminUserDTOS = new ArrayList<>();
        for(User user: users)
            adminUserDTOS.add(new AdminUserDTO(user.getId(),  user.getUsername(), user.getDisplayedName(),user.getEmail(), user.getLocation(), user.getRoles()));

        return adminUserDTOS;
    }

    /*
    @GetMapping("/get-user-details")
    public AdminUserDetailsDTO getUserDetails(@RequestParam("userId") @NotEmpty @NotNull Long userId, HttpServletRequest req){
        User user = userService.getById(userId);
        return AdminUserDetailsDTO.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).displayedName(user.getDisplayedName())
                .location(user.getLocation()).acceptedEvents(user.getAcceptedEvents()).groups(user.getGroups()).roles(user.getRoles()).build();
    }
    */

    @PostMapping("/create-user")
    public Response createUser(@RequestBody @Valid AdminUserCreateRequest adminUserCreateRequest, Errors errors){
        fieldErrorChecker(errors);

        List<String> messages = new ArrayList<>();
        if (userService.existsByUsername(adminUserCreateRequest.getUsername()))
            messages.add("Username is already taken");
        if (userService.existsByEmail(adminUserCreateRequest.getEmail()))
            messages.add("Email is already in use");
        if (!adminUserCreateRequest.getPassword().equals(adminUserCreateRequest.getConfirmPassword()))
            messages.add("New and confirm passwords do not match");
        if(!messages.isEmpty())
            throw new FieldException(null, messages);

        User user = User.builder()
                .username(adminUserCreateRequest.getUsername())
                .email(adminUserCreateRequest.getEmail())
                .displayedName(adminUserCreateRequest.getDisplayedName())
                .password(encoder.encode(adminUserCreateRequest.getPassword()))
                .location(adminUserCreateRequest.getLocation())
                .build();

        String stringRole = adminUserCreateRequest.getRole();
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
    public Response updateUser(@RequestBody @Valid AdminUserUpdateRequest adminUserUpdateRequest, Errors errors){
        fieldErrorChecker(errors);

        User user = userService.getById(adminUserUpdateRequest.getId());
        List<String> messages = new ArrayList<>();
        if (userService.existsByUsername(adminUserUpdateRequest.getUsername()) && user.getUsername() != adminUserUpdateRequest.getUsername())
            messages.add("Username is already taken");
        if (userService.existsByEmail(adminUserUpdateRequest.getEmail()) && user.getEmail() != adminUserUpdateRequest.getEmail())
            messages.add("Email is already in use");
        if(!messages.isEmpty())
            throw new FieldException(null, messages);

        String stringRole = adminUserUpdateRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (!Objects.isNull(stringRole)) {
            if ("admin".equals(stringRole)) {
                roles.add(roleService.findByName(RoleType.ROLE_ADMIN));
            }
        }
        roles.add(roleService.findByName(RoleType.ROLE_USER));

        user.setRoles(roles);
        user.setUsername(adminUserUpdateRequest.getUsername());
        user.setEmail(adminUserUpdateRequest.getEmail());
        user.setDisplayedName(adminUserUpdateRequest.getDisplayedName());
        user.setLocation(adminUserUpdateRequest.getLocation());
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

    @GetMapping("/get-all-groups")
    public List<AdminGroupsDTO> getAllGroups(HttpServletRequest req){
        List<Group> groups = groupService.getAllGroups();
        List<AdminGroupsDTO> adminGroupDTOS = new ArrayList<>();
        for(Group group: groups) {
            String ownerUserName = group.getOwner().getUsername();
            adminGroupDTOS.add(new AdminGroupsDTO(group.getId(), group.getGroupName(), group.getLocation(), group.getDescription(),ownerUserName));
        }
        return adminGroupDTOS;
    }

    /*
    @GetMapping("/get-group-details")
    public AdminGroupDetailsDTO getGroupDetails(@RequestParam("groupId") @NotEmpty @NotNull Long groupId, HttpServletRequest req){
        User user = userService.getById(userId);
        return AdminUserDetailsDTO.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).displayedName(user.getDisplayedName())
                .location(user.getLocation()).acceptedEvents(user.getAcceptedEvents()).groups(user.getGroups()).roles(user.getRoles()).build();
    }
     */

    @PostMapping("update-group-details")
    public Response updateGroup(@RequestBody @Valid GroupSaveRequest groupSaveRequest, Errors errors){
        fieldErrorChecker(errors);

        Group group = groupService.getById(groupSaveRequest.getGroupId());

        List<String> messages = new ArrayList<>();
        if (groupService.existsByGroupName(groupSaveRequest.getGroupName()) && group.getGroupName() != groupSaveRequest.getGroupName())
            messages.add("Group Name is already taken");
        if(!messages.isEmpty())
            throw new FieldException(null, messages);


        group.setGroupName(groupSaveRequest.getGroupName());
        group.setLocation(groupSaveRequest.getLocation());
        group.setDescription(groupSaveRequest.getDescription());
        groupService.save(group);
        response.setSuccess(true);
        response.setMessage("Group details updated successfully");
        return  response;
    }

    @DeleteMapping("/delete-group")
    public Response deleteGroup(@RequestParam("groupId") @NotEmpty @NotNull Long groupId){
        try {
            Group group = groupService.getById(groupId);
            groupService.delete(groupId);
            response.setSuccess(true);
            response.setMessage(String.format("The group (groupName: %s) has been deleted", group.getGroupName()));
        }
        catch (ObjectIsNullException e){
            response.setSuccess(false);
            response.setMessage("Something went wrong while deleting the group, Error: " + e.getMessage());
        }

        return response;
    }

}
