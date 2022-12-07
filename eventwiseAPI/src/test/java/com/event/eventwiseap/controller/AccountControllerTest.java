package com.event.eventwiseap.controller;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.given;

import com.event.eventwiseap.dto.RegisterRequest;
import com.event.eventwiseap.model.Role;
import com.event.eventwiseap.model.RoleType;
import com.event.eventwiseap.model.User;
import com.event.eventwiseap.security.AuthEntryPointJWT;
import com.event.eventwiseap.security.JWTUtils;
import com.event.eventwiseap.service.RoleService;
import com.event.eventwiseap.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean private UserService userService;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private JWTUtils jwtUtils;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private AuthEntryPointJWT authEntryPointJWT;
    @MockBean private PasswordEncoder encoder;
    @MockBean private RoleService roleService;

    private static final Set<Role> adminRoles;
    private static final Set<Role> userRoles;
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
    }


    @Test
    void check_register_user_with_correct_user_information() throws Exception{
        // given

        User user = User
                .builder()
                .username("balik18")
                .email("balik18@itu.edu.tr")
                .displayedName("balon")
                .location("ISTANBUL")
                .acceptedEvents(new HashSet<>())
                .groups(new HashSet<>())
                .roles(adminRoles)
                .build();

        given(userService.existsByUsername(user.getUsername())).willReturn(false);
        given(userService.existsByUsername(user.getEmail())).willReturn(false);
        given(roleService.findByName(RoleType.ROLE_USER)).willReturn(new Role(RoleType.ROLE_USER));
        given(userService.save(any(User.class))).willReturn(user);


        // when
        RegisterRequest content = RegisterRequest.builder()
                .username("balik18")
                .email("balik18@itu.edu.tr")
                .displayedName("baloon")
                .password("hellofrom")
                .confirmPassword("hellofrom")
                .location("Istanbul")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

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

        User user = User
                .builder()
                .username("balik18")
                .email("balik18@itu.edu.tr")
                .displayedName("balon")
                .location("ISTANBUL")
                .acceptedEvents(new HashSet<>())
                .groups(new HashSet<>())
                .roles(adminRoles)
                .build();

        given(userService.existsByUsername(user.getUsername())).willReturn(false);
        given(userService.existsByUsername(user.getEmail())).willReturn(false);
        given(roleService.findByName(RoleType.ROLE_USER)).willReturn(new Role(RoleType.ROLE_USER));
        given(userService.save(any(User.class))).willReturn(user);


        // when
        RegisterRequest content = RegisterRequest.builder()
                .username("balik18")
                .email("balik18")
                .displayedName("baloon")
                .password("hellofrom")
                .confirmPassword("hellofrom")
                .location("Istanbul")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Not a proper E-mail")).andReturn();

    }
}