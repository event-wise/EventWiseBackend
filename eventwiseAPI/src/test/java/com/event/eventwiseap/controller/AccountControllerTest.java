package com.event.eventwiseap.controller;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.given;

import com.event.eventwiseap.dto.LoginRequest;
import com.event.eventwiseap.dto.PasswordChangeRequest;
import com.event.eventwiseap.dto.ProfileUpdateRequest;
import com.event.eventwiseap.dto.RegisterRequest;
import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.security.AuthEntryPointJWT;
import com.event.eventwiseap.security.JWTUtils;
import com.event.eventwiseap.service.RoleService;
import com.event.eventwiseap.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)

class AccountControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean private UserService userService;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private JWTUtils jwtUtils;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private AuthEntryPointJWT authEntryPointJWT;
    @Autowired private PasswordEncoder encoder;
    @MockBean private RoleService roleService;

    private User admin;
    private User user;

    private static final Set<Role> adminRoles;
    private static final Set<Role> userRoles;
    private RegisterRequest content;
    private static final ObjectMapper objectMapper;
    static {
        adminRoles = new HashSet<Role>(){
            {
                new Role(RoleType.ROLE_USER);
                new Role(RoleType.ROLE_ADMIN);
            }
        };
        userRoles = new HashSet<Role>(){
            {
                new Role(RoleType.ROLE_USER);
            }
        };
        objectMapper = new ObjectMapper();

    }

    @BeforeEach
    public void setup(){
        this.user = User
                .builder()
                .username("balik18")
                .email("balik18@itu.edu.tr")
                .displayedName("balon")
                .location("ISTANBUL")
                .acceptedEvents(new HashSet<>())
                .groups(new HashSet<>())
                .roles(userRoles)
                .build();

        this.admin = user = User
                .builder()
                .username("balik18")
                .email("balik18@itu.edu.tr")
                .displayedName("balon")
                .location("ISTANBUL")
                .acceptedEvents(new HashSet<>())
                .groups(new HashSet<>())
                .roles(adminRoles)
                .build();

        this.content = RegisterRequest.builder()
                .username("balik18")
                .email("balik18@itu.edu.tr")
                .displayedName("baloon")
                .password("hellofrom")
                .confirmPassword("hellofrom")
                .location("Istanbul")
                .build();
    }

    @Test
    void check_register_user_with_correct_user_information() throws Exception{
        // given
        given(userService.existsByUsername(user.getUsername())).willReturn(false);
        given(userService.existsByUsername(user.getEmail())).willReturn(false);
        given(roleService.findByName(RoleType.ROLE_USER)).willReturn(new Role(RoleType.ROLE_USER));
        given(userService.save(any(User.class))).willReturn(user);
        // when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(jsonPath("$.success").value(true)).andReturn();
    }
    @Test
    void check_register_user_with_incorrect_field() throws Exception{
        // given
        given(userService.existsByUsername(user.getUsername())).willReturn(false);
        given(userService.existsByEmail(user.getEmail())).willReturn(false);
        given(roleService.findByName(RoleType.ROLE_USER)).willReturn(new Role(RoleType.ROLE_USER));
        given(userService.save(any(User.class))).willReturn(user);
        // when
        content.setEmail("unsatisfied");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Not a proper E-mail")).andReturn();
    }
    @Test
    void check_register_user_with_existing_username_email() throws Exception{
        //given
        given(userService.existsByUsername(user.getUsername())).willReturn(true);
        given(userService.existsByEmail(user.getEmail())).willReturn(true);
        given(roleService.findByName(RoleType.ROLE_USER)).willReturn(new Role(RoleType.ROLE_USER));
        given(userService.save(any(User.class))).willReturn(user);
        content.setConfirmPassword("incorrect");
        //when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Username is already taken"))
                .andExpect(jsonPath("$.messages[1]").value("Email is already in use"))
                .andExpect(jsonPath("$.messages[2]").value("New and confirm passwords do not match"))
                .andReturn();
    }
    @Test
    void check_login_with_nonexistent_account_should_return_bad_request() throws Exception{
        //given
        LoginRequest req = new LoginRequest("balik18", "string");
        given(userService.existsByUsername(req.getUsername())).willReturn(false);
        //when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/login")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("There is no such an account"))
                .andReturn();
    }

//    @Test
//    @WithUserDetails(value = "balik18", userDetailsServiceBeanName = "UserDetailsService")
//    void check_login_user_with_correct_information() throws Exception{
//        //given
//        LoginRequest req = new LoginRequest("balik18", "string");
//        //when
//        RequestBuilder request = MockMvcRequestBuilders
//                .post("/api/account/login")
//                .accept(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(content))
//                .contentType(MediaType.APPLICATION_JSON);
//        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
//    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_logout() throws Exception{
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        //when
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/account/logout")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_profile_request() throws Exception{
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        //when
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/account/profile")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"}, password = "string")
    void check_change_password_with_correct_information() throws Exception{
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());

        user.setPassword(encoder.encode("string"));
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(userService.save(any(User.class))).willReturn(user);
        PasswordChangeRequest content = new PasswordChangeRequest("string",
                "testing",
                "testing");
        //when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/change-password")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .content(objectMapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_change_password_with_incorrect_information() throws Exception{
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());

        user.setPassword(encoder.encode("string"));
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(userService.save(any(User.class))).willReturn(user);
        PasswordChangeRequest content = new PasswordChangeRequest("string2",
                "testing",
                "testing2");
        //when
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/change-password")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .content(objectMapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_delete_account_OK() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(userService.delete(any(Long.class))).willReturn(1l);
        //when
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/account/delete-account")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
    }
    @Test
    @WithMockUser(roles = {"USER"})
    void check_delete_account_BAD() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        given(userService.getByUsername(user.getUsername())).willReturn(null);
        given(userService.delete(any(Long.class))).willReturn(1l);
        //when
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/account/delete-account")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void check_update_profile_OK() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        given(userService.getByUsername(user.getUsername())).willReturn(user);
        given(userService.save(any(User.class))).willReturn(user);

        ProfileUpdateRequest content = new ProfileUpdateRequest("testing", "ISTANBUL");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/update-profile")
                .accept(MediaType.APPLICATION_JSON)
                .session(session)
                .content(objectMapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
    }
}