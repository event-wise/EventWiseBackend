package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.EventSaveRequest;
import com.event.eventwiseap.dto.GroupSaveRequest;
import com.event.eventwiseap.dto.Response;
import com.event.eventwiseap.dto.UserDTO;
import com.event.eventwiseap.dto.admin.*;
import com.event.eventwiseap.exception.FieldException;
import com.event.eventwiseap.exception.GeneralException;
import com.event.eventwiseap.exception.ObjectIsNullException;
import com.event.eventwiseap.model.*;
import com.event.eventwiseap.security.JWTUtils;
import com.event.eventwiseap.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    private final EventService eventService;
    private final LogService logService;
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
    public List<AdminUserDTO> getAllUsers(){
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
        if (userService.existsByUsername(adminUserUpdateRequest.getUsername()) && !user.getUsername().equals(adminUserUpdateRequest.getUsername()))
            messages.add("Username is already taken");
        if (userService.existsByEmail(adminUserUpdateRequest.getEmail()) && !user.getEmail().equals(adminUserUpdateRequest.getEmail()))
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

    @PostMapping("/delete-user")
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
    public List<AdminGroupsDTO> getAllGroups(){
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
        if (groupService.existsByGroupName(groupSaveRequest.getGroupName()) && !group.getGroupName().equals(groupSaveRequest.getGroupName()))
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

    @PostMapping("/delete-group")
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

    @PostMapping("/create-global-group")
    public Response createGlobalGroup(@RequestBody @Valid GroupSaveRequest groupSaveRequest, Errors errors,
                                      HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String admin_username = session.getAttribute(SESSION_USERNAME).toString();
        fieldErrorChecker(errors);

        List<String> messages = new ArrayList<>();
        if (groupService.existsByGroupName(groupSaveRequest.getGroupName()))
            messages.add("Group Name is already taken");
        if(!messages.isEmpty())
            throw new FieldException(null, messages);

        Group group = Group.builder()
                .groupName(groupSaveRequest.getGroupName())
                .groupMembers(new HashSet<>())
                .location(groupSaveRequest.getLocation())
                .description(groupSaveRequest.getDescription())
                .owner(userService.getByUsername(admin_username))
                .build();

        groupService.save(group);
        Set<User> all_users = new HashSet<>(userService.getAllUsers());
        group.setGroupMembers(all_users);
        groupService.save(group);
        response.setSuccess(true);
        response.setMessage("Global group is created successfully");
        return  response;
    }

    @GetMapping("/get-all-events")
    public List<AdminEventsDTO> getAllEvents(){
        List<Event> events = eventService.getAllEvents();
        List<AdminEventsDTO> adminEventsDTOS = new ArrayList<>();
        for(Event event: events){
            Group belongedGroup = event.getGroup();
            User organizer = event.getOrganizer();
            adminEventsDTOS.add(new AdminEventsDTO(event.getId(),event.getName(),belongedGroup.getId(),belongedGroup.getGroupName(),organizer.getId(), organizer.getUsername(), event.getDateTime(), event.getCreationTime(),event.getLocation(), event.getType(),event.getDescription()));
        }
        return adminEventsDTOS;
    }

    @PostMapping("/delete-event")
    public Response deleteEvent(@RequestParam("eventId") @NotEmpty @NotNull Long eventId){
        try {
            Event event = eventService.getEventById(eventId);
            eventService.delete(eventId);
            response.setSuccess(true);
            response.setMessage(String.format("The event (eventName: %s) has been deleted", event.getName()));
        }
        catch (ObjectIsNullException e){
            response.setSuccess(false);
            response.setMessage("Something went wrong while deleting the event, Error: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("update-event-details")
    public Response updateEvent(@RequestBody @Valid EventSaveRequest eventSaveRequest, Errors errors){
        fieldErrorChecker(errors);
        Event event = eventService.getEventById(eventSaveRequest.getEventId());
        if(Objects.isNull(event))
            throw new GeneralException("This event does not exists");

        event.setName(eventSaveRequest.getEventName());
        event.setDateTime(eventSaveRequest.getDateTime());
        event.setLocation(eventSaveRequest.getLocation());
        event.setType(eventSaveRequest.getType());
        event.setDescription(event.getDescription());

        eventService.save(event);
        response.setSuccess(true);
        response.setMessage("Event details updated successfully");
        return  response;
    }

    @PostMapping("/create-event")
    public Response createEvent(@RequestBody @Valid EventSaveRequest eventSaveRequest, Errors errors){
        fieldErrorChecker(errors);

        /*
        Create event for desired group and assign event owner as group creator
         */
        List<String> messages = new ArrayList<>();

        if(!groupService.existsByGroupId(eventSaveRequest.getGroupId()))
            messages.add("Given id doesn't correspond to any group");
        if(!messages.isEmpty())
            throw new FieldException(null, messages);

        Group group = groupService.getById(eventSaveRequest.getGroupId());
        User groupOwner = group.getOwner();
        Event event = Event.builder()
                .name(eventSaveRequest.getEventName())
                .group(group)
                .organizer(groupOwner)
                .acceptedMembers(new HashSet<>())
                .location(eventSaveRequest.getLocation())
                .description(eventSaveRequest.getDescription())
                .dateTime(eventSaveRequest.getDateTime())
                .type(eventSaveRequest.getType())
                .creationTime(LocalDateTime.now())
                .build();
        event = eventService.save(event);
        event.acceptedBy(groupOwner);
        event = eventService.save(event);
        String msg = event.getName() + " created by ADMIN and assigned to " + groupOwner.getUsername();
        Log log = Log.builder().group(group).logMessage(msg).build();
        logService.save(log);
        response.setSuccess(true);
        response.setMessage(msg);
        return response;
    }



}
