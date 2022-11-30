package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.*;
import com.event.eventwiseap.exception.GeneralException;
import com.event.eventwiseap.model.Event;
import com.event.eventwiseap.model.Group;
import com.event.eventwiseap.model.Log;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.service.EventService;
import com.event.eventwiseap.service.GroupService;
import com.event.eventwiseap.service.LogService;
import com.event.eventwiseap.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;
    private final EventService eventService;
    private final LogService logService;
    private static final String SESSION_USERNAME = "username";
    private static Response response;
    static {
        response = new Response();
    }

    @PostMapping("/create-group")
    public Response createGroup(@RequestBody @Valid GroupSaveRequest groupCreationRequest, HttpServletRequest req){
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

    @PostMapping("/update-group")
    public Response updateGroup(@RequestBody @Valid GroupSaveRequest groupUpdateRequest, HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(groupUpdateRequest.getOwnerId());
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        if(Objects.isNull(groupUpdateRequest.getGroupId()))
            throw new GeneralException("Group ID cannot be null while updating");
        Group group = groupService.getById(groupUpdateRequest.getGroupId());
        if(Objects.isNull(group)){
            throw new GeneralException("This group does not exists");
        }
        group.setGroupName(groupUpdateRequest.getGroupName());
        group.setLocation(groupUpdateRequest.getLocation());
        group.setDescription(groupUpdateRequest.getDescription());
        group = groupService.save(group);

        response.setSuccess(true);
        response.setMessage(String.format("The group (group name: %s, group id: %s) has been updated successfully",
                group.getGroupName(),group.getId()));
        return response;
    }

    @GetMapping("/list-user-groups")
    public Set<GroupsDTO> listUserGroups(@RequestParam("userId") @NotEmpty @NotNull Long userId, HttpServletRequest req){
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

    @GetMapping("/group-details")
    public GroupDetailsDTO getGroupDetails(@RequestParam("userId") @NotEmpty @NotNull Long userId,
                                    @RequestParam("groupId") @NotEmpty @NotNull Long groupId,
                                    HttpServletRequest req) {
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(userId);
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        Group group = groupService.getById(groupId);
        if(Objects.isNull(group)){
            throw new GeneralException("This group does not exists");
        }
        if(!group.getGroupMembers().contains(user)){
            throw new GeneralException("You are not a member of this group");
        }
        List<Log> logs = logService.getAllByGroupId(groupId);
        List<Event> events = eventService.getEventsByGroupId(groupId);

        List<EventsDTO> eventsDTOs = new ArrayList<>();
        List<String> logMessages = new ArrayList<>();
        Set<String> members = new HashSet<>();

        for(Event event: events)
            eventsDTOs.add(new EventsDTO(event.getId(),event.getName(), event.getDateTime()));
        for(Log log:logs)
            logMessages.add(log.getLogMessage());
        for(User member:group.getGroupMembers())
            members.add(member.getUsername());

        return new GroupDetailsDTO(group.getId(),
                group.getGroupName(), group.getLocation(), group.getDescription(),
                members,eventsDTOs,logMessages);
    }

    @DeleteMapping("/delete-group")
    public Response deleteGroup(@RequestParam("userId") @NotEmpty @NotNull Long userId,
                                @RequestParam("groupId") @NotEmpty @NotNull Long groupId,
                                HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(userId);
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        Group group = groupService.getById(groupId);
        if(Objects.isNull(group)){
            throw new GeneralException("This group does not exists");
        }
        if(!group.isOwner(user)){
            throw new GeneralException("You are not the owner of this group");
        }
        groupService.delete(groupId);
        log.info("User '{}' deleted the group with ID = {}", username, groupId);
        response.setSuccess(true);
        response.setMessage("The group " + group.getGroupName() + " has been deleted");
        return response;
    }

    @GetMapping("/search-member")
    public SearchResponse searchMember(@RequestParam("userId") @NotEmpty @NotNull Long userId,
                                @RequestParam("groupId") @NotEmpty @NotNull Long groupId,
                                @RequestParam("search") @NotEmpty @NotNull String search,
                                HttpServletRequest req) {
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(userId);
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        Group group = groupService.getById(groupId);
        if(Objects.isNull(group)){
            throw new GeneralException("This group does not exists");
        }
        if(!group.getGroupMembers().contains(user)){
            throw new GeneralException("You are not a member of this group");
        }

        User searchedUser = userService.getByUsername(search);
        SearchResponse searchResponse;
        if(!Objects.isNull(searchedUser)){
            searchResponse = new
                    SearchResponse(true, group.getGroupMembers().contains(searchedUser), searchedUser.getUsername());
        }
        else{
            searchResponse = new SearchResponse(false, false, "-");
        }
        return searchResponse;
    }

    @PostMapping("/add-remove-member")
    public Response addRemoveMember(@RequestBody @Valid MemberAddRemoveRequest memberAddRemoveRequest,
                                    HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(memberAddRemoveRequest.getActorUserId());
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        Group group = groupService.getById(memberAddRemoveRequest.getGroupId());
        if(Objects.isNull(group)){
            throw new GeneralException("This group does not exists");
        }
        if(!group.getGroupMembers().contains(user)){
            throw new GeneralException("You are not a member of this group");
        }

        User subject = userService.getById(memberAddRemoveRequest.getSubjectUserId());

        if(!Objects.isNull(subject)){

            if(group.getGroupMembers().contains(subject) && group.removeMember(subject))
            {
                response.setSuccess(true);
                response.setMessage("Member '" + subject.getUsername() + "' removed from group");
                groupService.save(group);
            }
            else if(!group.getGroupMembers().contains(subject) && group.addMember(subject)){
                response.setSuccess(true);
                response.setMessage("Member '" + subject.getUsername() + "' added to group");
                groupService.save(group);
            }
            else{
                response.setSuccess(false);
                response.setMessage("Something went wrong");
            }
        }
        else{
            response.setSuccess(false);
            response.setMessage("There is no such a user");
        }
        return  response;
    }

    @PutMapping("/exit-from-group")
    public Response exitFromGroup(@RequestParam("userId") @NotEmpty @NotNull Long userId,
                                  @RequestParam("groupId") @NotEmpty @NotNull Long groupId,
                                  HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(userId);
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        Group group = groupService.getById(groupId);
        if(Objects.isNull(group)){
            throw new GeneralException("This group does not exists");
        }
        if(!group.getGroupMembers().contains(user)){
            throw new GeneralException("You are not a member of this group");
        }

        response.setSuccess(group.removeMember(user));
        groupService.save(group);
        response.setMessage("You left the group '" + group.getGroupName() + "'");

        return response;
    }

}