package com.tui.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    private String login;
    private String id;
    private String avatarUrl;
}    