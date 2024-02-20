package com.tui.github.service;

import java.util.List;

import com.tui.github.model.Repository;

import reactor.core.publisher.Mono;

public interface GitRepositoryService {
   Mono<List<Repository>> getUserRepositoryWithBranches(String userName, String authorizationToken);
}
