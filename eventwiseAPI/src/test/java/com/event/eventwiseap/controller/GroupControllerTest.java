package com.event.eventwiseap.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.given;

import com.event.eventwiseap.dto.GroupSaveRequest;
import com.event.eventwiseap.dto.MemberAddRemoveRequest;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(GroupController.class)
class GroupControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean private GroupService groupService;
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
    private User subject;
    private MockHttpSession session;
    private static final ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
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
        this.subject = User
                .builder()
                .id(2L)
                .username("testsub1")
                .email("test228@itu.edu.tr")
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
        session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());

    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_createGroup_with_field_error_should_return_bad_request() throws Exception{
        //given
        group.addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.save(any(Group.class))).willReturn(group);
        group.setGroupName("");
        GroupSaveRequest content = new GroupSaveRequest(0L,group.getGroupName(),group.getLocation(),group.getDescription());
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/create-group")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isBadRequest()).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_createGroup_with_field_error_should_return_OK() throws Exception{
        //given
        group.addMember(user);
        given(userService.getByUsername(this.user.getUsername())).willReturn(user);
        given(groupService.save(any(Group.class))).willReturn(group);
        GroupSaveRequest content = new GroupSaveRequest(0L,group.getGroupName(),group.getLocation(),group.getDescription());
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/create-group")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_updateGroup_with_null_id_should_return_bad_request() throws Exception{
        // given
        GroupSaveRequest content = new GroupSaveRequest(null,group.getGroupName(),group.getLocation(),group.getDescription());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/update-group")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("Group ID cannot be null while updating")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_updateGroup_with_nonexistent_group_should_return_bad_request() throws Exception{
        // given
        GroupSaveRequest content = new GroupSaveRequest(90L,group.getGroupName(),group.getLocation(),group.getDescription());
        given(groupService.getById(content.getGroupId())).willReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/update-group")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("This group does not exists")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_updateGroup_with_not_owner_should_return_bad_request() throws Exception{
        GroupSaveRequest content = new GroupSaveRequest(group.getId(),group.getGroupName(),group.getLocation(),group.getDescription());
        User owner = User.builder().id(90L).build();
        group.setOwner(owner);
        given(groupService.getById(content.getGroupId())).willReturn(group);
        given(userService.getByUsername(user.getUsername())).willReturn(user);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/update-group")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("Only group owner can update the group")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_updateGroup_with_correct_info_should_return_OK() throws Exception{
        GroupSaveRequest content = new GroupSaveRequest(group.getId(),group.getGroupName(),group.getLocation(),group.getDescription());
        given(groupService.getById(content.getGroupId())).willReturn(group);
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.save(any(Group.class))).willReturn(group);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/update-group")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_listUserGroups_with_correct_info_should_return_OK() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        List<Group> groups = new ArrayList<>();
        groups.add(group);
        given(groupService.getGroupsByMember(any(User.class))).willReturn(groups);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/group/list-user-groups")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_getGroupDetails_with_nonexistent_group_should_return_bad_request() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(1L)).willReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/group/group-details?groupId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("This group does not exists")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_getGroupDetails_with_not_group_member_should_return_bad_request() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(1L)).willReturn(group);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/group/group-details?groupId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("You are not a member of this group")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_getGroupDetails_with_correct_info_should_return_OK() throws Exception{
        group.addMember(user);
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(1L)).willReturn(group);
        List<Group> groups = new ArrayList<>();
        groups.add(group);
        given(groupService.getGroupsByMember(any(User.class))).willReturn(groups);
        List<Log> logs = new ArrayList<>();
        Log log = Log.builder().group(group).logMessage("testing").build();
        logs.add(log);
        given(logService.getAllByGroupId(group.getId())).willReturn(logs);
        List<Event> events = new ArrayList<>();
        Event event = Event.builder().id(1L).name("TESTING").dateTime(LocalDateTime.now()).build();
        events.add(event);
        given(eventService.getEventsByGroupId(group.getId())).willReturn(events);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/group/group-details?groupId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_deleteGroup_with_nonexistent_group_should_return_bad_request() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(1L)).willReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/group/delete-group?groupId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("This group does not exists")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_deleteGroup_with_not_owner_should_return_bad_request() throws Exception{
        User owner = User.builder().id(90L).build();
        group.setOwner(owner);
        given(groupService.getById(group.getId())).willReturn(group);
        given(userService.getByUsername(user.getUsername())).willReturn(user);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/group/delete-group?groupId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("You are not the owner of this group")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_deleteGroup_with_correct_info_should_return_OK() throws Exception{
        given(groupService.getById(group.getId())).willReturn(group);
        given(userService.getByUsername(user.getUsername())).willReturn(user);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/group/delete-group?groupId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_searchMember_with_nonexistent_group_should_return_bad_request() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(1L)).willReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/group/search-member?groupId=1&search=" + user.getUsername())
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("This group does not exists")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_searchMember_with_not_member_should_return_bad_request() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/group/search-member?groupId=1&search=" + user.getUsername())
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("You are not a member of this group")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_searchMember_with_search_null_should_return_OK() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        group.addMember(user);
        given(groupService.getById(group.getId())).willReturn(group);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/group/search-member?groupId=1&search=aaa")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).
                andExpect(jsonPath("$.found").value(false)).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_searchMember_with_search_should_return_OK() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        group.addMember(user);
        given(groupService.getById(group.getId())).willReturn(group);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/group/search-member?groupId=1&search=" + user.getUsername())
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).
                andExpect(jsonPath("$.found").value(true)).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_add_remove_member_with_nonexistent_group_should_return_bad_request() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(null);
        MemberAddRemoveRequest content = new MemberAddRemoveRequest(user.getId(),group.getId());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/add-remove-member")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("This group does not exists")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_add_remove_member_with_not_member_group_should_return_bad_request() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);
        MemberAddRemoveRequest content = new MemberAddRemoveRequest(user.getId(),group.getId());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/add-remove-member")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("You are not a member of this group")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_add_remove_member_remove_should_return_OK() throws Exception{
        group.addMember(user);
        group.addMember(subject);
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(userService.getById(subject.getId())).willReturn(subject);
        given(groupService.getById(group.getId())).willReturn(group);
        MemberAddRemoveRequest content = new MemberAddRemoveRequest(subject.getId(), group.getId());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/add-remove-member")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).
                andExpect(jsonPath("$.message").value("Member '" + subject.getUsername() + "' removed from group")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_add_remove_member_add_should_return_OK() throws Exception{
        group.addMember(user);
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(userService.getById(subject.getId())).willReturn(subject);
        given(groupService.getById(group.getId())).willReturn(group);
        MemberAddRemoveRequest content = new MemberAddRemoveRequest(subject.getId(), group.getId());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/add-remove-member")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).
                andExpect(jsonPath("$.message").value("Member '" + subject.getUsername() + "' added to group")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_add_remove_member_with_nonexistent_user_should_return_false() throws Exception{
        group.addMember(user);
        group.addMember(subject);
        subject.setGroups(new HashSet<>());
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(userService.getById(subject.getId())).willReturn(null);
        given(groupService.getById(group.getId())).willReturn(group);
        MemberAddRemoveRequest content = new MemberAddRemoveRequest(subject.getId(), group.getId());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/group/add-remove-member")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).
                andExpect(jsonPath("$.message").value("There is no such a user")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_exitFromGroup_with_non_existent_group_should_return_bad_request() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/group/exit-from-group?groupId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("This group does not exists")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_exitFromGroup_with_not_member_return_bad_request() throws Exception{
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/group/exit-from-group?groupId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.message").value("You are not a member of this group")).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_exitFromGroup_with_member_return_OK() throws Exception{
        group.addMember(user);
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(groupService.getById(group.getId())).willReturn(group);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/group/exit-from-group?groupId=1")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isOk()).andReturn();
    }
}