package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.EventSaveRequest;
import com.event.eventwiseap.dto.EventsDTO;
import com.event.eventwiseap.dto.Response;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/event")
public class EventController {
    private final EventService eventService;
    private final LogService logService;
    private final GroupService groupService;
    private final UserService userService;
    private static final String SESSION_USERNAME = "username";
    private static Response response;
    static {
        response = new Response();
    }

    @PostMapping("/create-event")
    public Response createEvent(@RequestBody @Valid EventSaveRequest eventCreationRequest, HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getById(eventCreationRequest.getOrganizerId());
        if(Objects.isNull(user)){
            throw new GeneralException("This profile does not exists");
        }
        if(!user.getUsername().equals(username)){
            throw new GeneralException("Session invalid");
        }
        Group group = groupService.getById(eventCreationRequest.getGroupId());
        if(Objects.isNull(group)){
            throw new GeneralException("This group does not exists");
        }

        Event event = Event.builder()
                .name(eventCreationRequest.getEventName())
                .group(group)
                .organizer(user)
                .acceptedMembers(new HashSet<>())
                .location(eventCreationRequest.getLocation())
                .description(eventCreationRequest.getDescription())
                .dateTime(eventCreationRequest.getDateTime())
                .type(eventCreationRequest.getType())
                .creationTime(LocalDateTime.now())
                .build();
        event = eventService.save(event);
        event.acceptedBy(user);
        event = eventService.save(event);
        String msg = event.getName() + " created by " + user.getUsername();
        Log log = Log.builder().group(group).logMessage(msg).build();
        logService.save(log);
        response.setSuccess(true);
        response.setMessage(msg);
        return response;
    }
//    @GetMapping("/list-user-events")
//    public List<EventsDTO> listUserEvents(@RequestParam("userId") @NotEmpty @NotNull Long userId, HttpServletRequest req){
//
//    }
}
