package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.GroupCreationRequest;
import com.event.eventwiseap.dto.GroupsDTO;
import com.event.eventwiseap.dto.Response;
import com.event.eventwiseap.exception.GeneralException;
import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.service.GroupService;
import com.event.eventwiseap.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;
    private static final String SESSION_USERNAME = "username";
    private static Response response;
    static {
        response = new Response();
    }

    @PostMapping("/create-group")
    public Response createGroup(@RequestBody @Valid GroupCreationRequest groupCreationRequest, HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(groupCreationRequest.getOwnerId());
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        Group group = Group.builder()
                .groupName(groupCreationRequest.getGroupName())
                .description(groupCreationRequest.getDescription())
                .location(groupCreationRequest.getLocation())
                .groupMembers(new HashSet<>())
                .owner(user)
                .build();
        group.addMember(user);
        group = groupService.save(group);
        response.setSuccess(true);
        response.setMessage(group.getGroupName() + " has been created successfully.");
        return response;
    }

    @GetMapping("/list-user-groups")
    Set<GroupsDTO> listUserGroups(@RequestParam("userId") @NotEmpty @NotNull Long userId, HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(userId);
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        Set<Group> groups = groupService.getGroupsByMember(user);
        Set<GroupsDTO> groupsDTOs = new HashSet<>();

        for(Group group : groups){
            groupsDTOs.add(new GroupsDTO(group.getId(), group.getGroupName(), group.getLocation()));
        }
        return groupsDTOs;
    }
}
