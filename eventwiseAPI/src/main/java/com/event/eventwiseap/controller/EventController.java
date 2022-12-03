package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.EventDetailsDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/event")
@PreAuthorize("hasRole('ROLE_USER')")
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
        User user = userService.getByUsername(username);
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

    @PostMapping("/update-event")
    public Response updateGroup(@RequestBody @Valid EventSaveRequest eventUpdateRequest, HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getByUsername(username);
        Group group = groupService.getById(eventUpdateRequest.getGroupId());
        if(Objects.isNull(group)){
            throw new GeneralException("This group does not exists");
        }
        if(!group.getGroupMembers().contains(user))
            throw new GeneralException("You are not a member of this group");
        Event event = eventService.getEventById(eventUpdateRequest.getEventId());
        if(Objects.isNull(event))
            throw new GeneralException("This event does not exists");
        if(!event.getOrganizer().equals(user))
            throw new GeneralException("Only the organizer can update the event");
        event.setName(eventUpdateRequest.getEventName());
        event.setDateTime(eventUpdateRequest.getDateTime());
        event.setLocation(eventUpdateRequest.getLocation());
        event.setType(eventUpdateRequest.getType());
        event.setDescription(eventUpdateRequest.getDescription());
        event = eventService.save(event);
        String msg = event.getName() + " updated by " + user.getUsername();
        Log log = Log.builder().group(group).logMessage(msg).build();
        logService.save(log);
        response.setSuccess(true);
        response.setMessage(msg);
        return response;
    }

    @GetMapping("/list-user-events")
    public List<EventsDTO> listUserEvents(HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getByUsername(username);
        List<Event> events = eventService.getEventsByUser(user);
        List<EventsDTO> eventsDTO = new ArrayList<>();
        for(Event event:events)
            eventsDTO.add(new EventsDTO(event.getId(), event.getName(), event.getDateTime()));
        return eventsDTO;
    }

    @GetMapping("/event-details")
    public EventDetailsDTO getEventDetails(@RequestParam("eventId") @NotEmpty @NotNull Long eventId,
                                           HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getByUsername(username);
        Event event = eventService.getEventById(eventId);
        if(Objects.isNull(event)){
            throw new GeneralException("This event does not exists");
        }
        if(!event.getGroup().getGroupMembers().contains(user))
            throw new GeneralException("You are not a member of this group");

        List<String> acceptedMembers = new ArrayList<>();

        for(User member: event.getAcceptedMembers())
            acceptedMembers.add(member.getUsername());
        return new EventDetailsDTO(
                eventId,
                event.getGroup().getId(),
                event.getOrganizer().getId(),
                event.getName(),
                event.getDateTime(),
                event.getLocation(),
                event.getType(),
                event.getDescription(),
                acceptedMembers,
                event.isAccepted(user)
        );
    }

    @DeleteMapping("/delete-event")
    public Response deleteEvent(@RequestParam("eventId") @NotEmpty @NotNull Long eventId,
                                HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getByUsername(username);
        Event event = eventService.getEventById(eventId);
        if(Objects.isNull(event)){
            throw new GeneralException("This event does not exists");
        }
        if(!event.getOrganizer().equals(user) || !event.getGroup().isOwner(user))
            throw new GeneralException("Only the organizer or the group owner can delete the event");
        String msg = event.getName() + " delete by " + user.getUsername();
        eventService.delete(eventId);
        Log log = Log.builder().group(event.getGroup()).logMessage(msg).build();
        logService.save(log);
        response.setSuccess(true);
        response.setMessage(msg);
        return response;
    }

    @PutMapping("/accept-event")
    public Response acceptEvent(@RequestParam("eventId") @NotEmpty @NotNull Long eventId,
                                HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getByUsername(username);
        Event event = eventService.getEventById(eventId);
        if(Objects.isNull(event))
            throw new GeneralException("This event does not exists");
        if(!event.getGroup().getGroupMembers().contains(user))
            throw new GeneralException("You are not a member of this group");
        if(event.isAccepted(user))
            throw new GeneralException("This event has already been accepted");

        event.acceptedBy(user);
        eventService.save(event);
        String msg = user.getUsername() + " accepted the event " + event.getName();
        Log log = Log.builder().group(event.getGroup()).logMessage(
                msg
        ).build();
        logService.save(log);
        response.setSuccess(true);
        response.setMessage(msg);
        return response;
    }

    @PutMapping("/reject-event")
    public Response rejectEvent(@RequestParam("eventId") @NotEmpty @NotNull Long eventId,
                                HttpServletRequest req){
        final HttpSession session = req.getSession();
        final String username = session.getAttribute(SESSION_USERNAME).toString();
        User user = userService.getByUsername(username);
        Event event = eventService.getEventById(eventId);
        if(Objects.isNull(event))
            throw new GeneralException("This event does not exists");
        if(!event.getGroup().getGroupMembers().contains(user))
            throw new GeneralException("You are not a member of this group");
        if(!event.isAccepted(user))
            throw new GeneralException("This event has already been rejected or not accepted yet");

        event.rejectedBy(user);
        eventService.save(event);
        String msg = user.getUsername() + " rejected the event " + event.getName();
        Log log = Log.builder().group(event.getGroup()).logMessage(
                msg
        ).build();
        logService.save(log);
        response.setSuccess(true);
        response.setMessage(msg);
        return response;
    }
}
