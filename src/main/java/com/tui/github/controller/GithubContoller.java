package com.tui.github.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tui.github.model.Repositories;
import com.tui.github.service.GitRepositoryService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping(value = "api/v1/github", produces = MediaType.APPLICATION_JSON_VALUE)
public class GithubContoller {

    private final GitRepositoryService _gitRepositoryService;

    public GithubContoller(GitRepositoryService gitRepositoryService) {
        _gitRepositoryService = gitRepositoryService;
    }

    @GetMapping("/gitrespository/{username}")
    public Mono<Repositories> getRepository( 
           @RequestHeader("Authorization") String authToken,
           @PathVariable("username") String userName,
           @RequestParam(value="pageSizeOfRepository", defaultValue = "100") int pageSizeOfRepository,
           @RequestParam(value="pageSizeOfBranch", defaultValue = "100") int pageSizeOfBranch) {
        log.info(String.format("Getting %s repository with its branches", userName));
        return _gitRepositoryService.getUserRepositoryWithBranches(userName, authToken, pageSizeOfRepository, pageSizeOfBranch);
    }

}
