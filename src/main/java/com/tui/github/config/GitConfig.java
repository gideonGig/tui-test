package com.tui.github.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;



@Configuration
public class GitConfig {

    private final GitSetting _gitSetting;

    @Autowired
    public GitConfig(GitSetting gitSetting) {
        _gitSetting = gitSetting;
    }

    @Bean
    public WebClient githubWebClient() {
       return WebClient.builder().baseUrl(_gitSetting.getBaseUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }

}

