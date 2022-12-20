package com.event.eventwiseap.controller;

import com.event.eventwiseap.dto.EventSaveRequest;
import com.event.eventwiseap.model.*;
import com.event.eventwiseap.security.AuthEntryPointJWT;
import com.event.eventwiseap.security.JWTUtils;
import com.event.eventwiseap.service.EventService;
import com.event.eventwiseap.service.GroupService;
import com.event.eventwiseap.service.LogService;
import com.event.eventwiseap.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
class EventControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private GroupService groupService;
    @MockBean private UserService userService;
    @MockBean private EventService eventService;
    @MockBean private LogService logService;

    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private JWTUtils jwtUtils;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private AuthEntryPointJWT authEntryPointJWT;
    @MockBean private PasswordEncoder encoder;

    private Group group;
    private User user;
    private Event event;
    private MockHttpSession session;
    private static final ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    @BeforeEach
    public void setup(){
        HashSet<User> members = new HashSet<>();
        this.user = User
                .builder()
                .id(1L)
                .username("balik18")
                .email("balik18@itu.edu.tr")
                .displayedName("balon")
                .location("ISTANBUL")
                .acceptedEvents(new HashSet<>())
                .groups(new HashSet<>())
                .roles(new HashSet<Role>(){
                    {
                        new Role(RoleType.ROLE_USER);
                    }
                })
                .build();
        this.group = Group.builder()
                .id(1L)
                .groupName("GROUP1")
                .location("Istanbul")
                .description("TEST GROUP")
                .owner(this.user)
                .groupMembers(new HashSet<>())
                .build();
        this.event = Event.builder()
                .id(1L)
                .name("EVENT1")
                .group(group)
                .organizer(user)
                .acceptedMembers(new HashSet<>())
                .location("Istanbul")
                .description("Testing")
                .type("sport")
                .creationTime(LocalDateTime.now())
                .dateTime(LocalDateTime.now())
                .build();
        session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_createEvent_with_field_error_should_return_bad_request() throws Exception{
        //given
        group.addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);
        EventSaveRequest content = EventSaveRequest.builder().build();
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/event/create-event")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_createEvent_with_nonexistent_group_should_return_bad_request() throws Exception{
        //given
        group.addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(null);
        EventSaveRequest content = EventSaveRequest.builder()
                .groupId(event.getGroup().getId())
                .eventName(event.getName())
                .dateTime(event.getDateTime())
                .type(event.getType())
                .location(event.getLocation())
                .description(event.getDescription())
                .build();
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/event/create-event")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This group does not exists")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_createEvent_with_correct_info_should_return_OK() throws Exception{
        group.addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);
        given(eventService.save(any(Event.class))).willReturn(event);
        EventSaveRequest content = EventSaveRequest.builder()
                .groupId(event.getGroup().getId())
                .eventName(event.getName())
                .dateTime(event.getDateTime())
                .type(event.getType())
                .location(event.getLocation())
                .description(event.getDescription())
                .build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/event/create-event")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_updateEvent_with_nonexistent_group_should_return_bad_request() throws Exception{
        //given
        group.addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(null);
        EventSaveRequest content = EventSaveRequest.builder()
                .groupId(event.getGroup().getId())
                .eventName(event.getName())
                .dateTime(event.getDateTime())
                .type(event.getType())
                .location(event.getLocation())
                .description(event.getDescription())
                .build();
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/event/update-event")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This group does not exists")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_createEvent_with_not_member_should_return_bad_request() throws Exception{
        //given
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);
        EventSaveRequest content = EventSaveRequest.builder()
                .groupId(event.getGroup().getId())
                .eventName(event.getName())
                .dateTime(event.getDateTime())
                .type(event.getType())
                .location(event.getLocation())
                .description(event.getDescription())
                .build();
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/event/update-event")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You are not a member of this group")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_createEvent_with_nonexistent_event_should_return_bad_request() throws Exception{
        //given
        group.addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);
        EventSaveRequest content = EventSaveRequest.builder()
                .groupId(event.getGroup().getId())
                .eventName(event.getName())
                .dateTime(event.getDateTime())
                .type(event.getType())
                .location(event.getLocation())
                .description(event.getDescription())
                .build();
        given(eventService.getEventById(content.getEventId())).willReturn(null);
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/event/update-event")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This event does not exists")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_createEvent_with_not_organizer_should_return_bad_request() throws Exception{
        //given
        group.addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);
        EventSaveRequest content = EventSaveRequest.builder()
                .groupId(event.getGroup().getId())
                .eventName(event.getName())
                .dateTime(event.getDateTime())
                .type(event.getType())
                .location(event.getLocation())
                .description(event.getDescription())
                .build();
        event.setOrganizer(User.builder().id(2L).build());
        given(eventService.getEventById(content.getEventId())).willReturn(event);
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/event/update-event")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only the organizer can update the event")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_createEvent_with_organizer_should_return_OK() throws Exception{
        //given
        group.addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);
        EventSaveRequest content = EventSaveRequest.builder()
                .groupId(event.getGroup().getId())
                .eventName(event.getName())
                .dateTime(event.getDateTime())
                .type(event.getType())
                .location(event.getLocation())
                .description(event.getDescription())
                .build();
        given(eventService.getEventById(content.getEventId())).willReturn(event);
        given(eventService.save(any(Event.class))).willReturn(event);
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/event/update-event")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_listUserEvents_should_return_OK() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        List<Event> events = new ArrayList<>();
        events.add(event);
        given(eventService.getEventsByUser(any(User.class))).willReturn(events);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/event/list-user-events")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_eventDetails_with_nonexistent_event_should_return_bad_request() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/event/event-details?eventId=2")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This event does not exists")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_eventDetails_with_not_member_should_return_bad_request() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/event/event-details?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You are not a member of this group")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_eventDetails_with_member_should_return_bad_request() throws Exception{
        event.getGroup().addMember(user);
        user.acceptEvent(event);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/event/event-details?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_deleteEvent_with_nonexistent_event_should_return_bad_request() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/event/delete-event?eventId=2")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This event does not exists")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_deleteEvent_with_not_organizer_should_return_bad_request() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        event.setOrganizer(User.builder().id(2L).build());
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/event/delete-event?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only the organizer or the group owner can delete the event")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_deleteEvent_with_organizer_should_return_OK() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/event/delete-event?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_acceptEvent_with_nonexistent_event_should_return_bad_request() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/event/accept-event?eventId=2")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This event does not exists")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_rejectEvent_with_nonexistent_event_should_return_bad_request() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/event/reject-event?eventId=2")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This event does not exists")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_acceptEvent_with_not_member_should_return_bad_request() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/event/accept-event?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You are not a member of this group")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_rejectEvent_with_not_member_should_return_bad_request() throws Exception{
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/event/reject-event?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You are not a member of this group")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_acceptEvent_already_accepted_should_return_bad_request() throws Exception{
        event.getGroup().addMember(user);
        event.acceptedBy(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/event/accept-event?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This event has already been accepted")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_rejectEvent_already_rejected_should_return_bad_request() throws Exception{
        event.getGroup().addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/event/reject-event?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This event has already been rejected or not accepted yet")).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_acceptEvent_not_accepted_should_return_OK() throws Exception{
        event.getGroup().addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/event/accept-event?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_rejectEvent_rejected_should_return_bad_request() throws Exception{
        event.getGroup().addMember(user);
        event.acceptedBy(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(eventService.getEventById(event.getId())).willReturn(event);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/event/reject-event?eventId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

}