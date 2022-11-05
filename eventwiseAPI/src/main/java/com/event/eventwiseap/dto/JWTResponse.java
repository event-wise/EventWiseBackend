package com.event.eventwiseap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class JWTResponse {
    private Long id;

    private String token;

    private String username;

    private String email;

    private List<String> roles;

    @Builder.Default
    private String authType = "Bearer";
}
