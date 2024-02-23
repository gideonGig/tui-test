package com.tui.github.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class GitSetting {
    @Value("${github.authBaseurl}")
    public String authBaseurl;
    @Value("${github.baseurl}")
    public String baseUrl;
}
