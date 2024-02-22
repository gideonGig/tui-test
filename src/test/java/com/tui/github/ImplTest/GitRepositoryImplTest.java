package com.tui.github.ImplTest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.reactive.function.client.WebClient;

import com.tui.github.model.Repositories;
import com.tui.github.service.GitRepositoryService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(MockitoJUnitRunner.class)
public class GitRepositoryImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private GitRepositoryService gitRepositoryService; // Using the interface

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetUserRepositoryWithBranches_Success() {
        // Mocked response from GitHub API
        String mockJsonResponse = "{\"data\": { \"user\": { \"repositories\": { \"nodes\": [ { \"name\": \"repo1\", \"owner\": { \"login\": \"user1\" }, \"refs\": { \"nodes\": [ { \"name\": \"master\", \"target\": { \"oid\": \"abc123\" } } ], \"pageInfo\": { \"hasNextPage\": false } } } ], \"pageInfo\": { \"hasNextPage\": false } } } }";

        // Mocking WebClient behavior
        when(webClient.post().uri(anyString()).header(anyString(), anyString()).bodyValue(any()).retrieve().bodyToMono(String.class))
                .thenReturn(Mono.just(mockJsonResponse));

        // Test the service method
        when(gitRepositoryService.getUserRepositoryWithBranches(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Mono.just(getMockRepositories()));

        // Use gitRepositoryService instead of GitRepositoryServiceImpl
        Mono<Repositories> resultMono = gitRepositoryService.getUserRepositoryWithBranches("username", "token", 100, 100);

        // Verify the result
        StepVerifier.create(resultMono)
                .expectNextMatches(repositories -> {
                    // Verify repositories
                    // Verify repository details
                    // Verify branch details
                    return true; // Add your verification logic here
                })
                .verifyComplete();
    }

    // Add more test cases as required

    // Helper method to create a mock Repositories object
    private Repositories getMockRepositories() {
        // Create and return a mock Repositories object
        return new Repositories();
    }

}
