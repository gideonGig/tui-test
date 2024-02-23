package com.tui.github.service;

import com.tui.github.model.Repositories;

import reactor.core.publisher.Mono;

public interface GitRepositoryService {
    Mono<Repositories> getAllUserRepositoryWithBranches(String userName, String authorizationToken, int pageSizeOfRepository, int pageSizeOfBranch);
}
