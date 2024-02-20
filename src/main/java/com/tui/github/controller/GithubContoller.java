package com.tui.github.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tui.github.model.Repository;
import com.tui.github.service.GitRepositoryService;

import io.netty.handler.codec.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping(value = "api/v1/github", produces = MediaType.APPLICATION_JSON_VALUE)
public class GithubContoller {


    private final GitRepositoryService _gitRepositoryService;
    
    @Autowired
	private HttpServletRequest _request;

    public GithubContoller(GitRepositoryService gitRepositoryService) {
        _gitRepositoryService = gitRepositoryService;
    }

    @GetMapping("/gitrespository/{username}")
    public Mono<List<Repository>> getRepository(@PathVariable("username") String userName) {
        String authorizationToken = _request.getHeader("Authorization");
        return _gitRepositoryService.getUserRepositoryWithBranches(userName, authorizationToken);
    }

}
