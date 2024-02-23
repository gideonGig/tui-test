package com.tui.github.ImplTest;

import com.tui.github.config.ResourceWebPropertiesConfig;
import com.tui.github.controller.GithubController;
import com.tui.github.exception.GlobalExceptionHandler;
import com.tui.github.model.Repositories;
import com.tui.github.service.GitRepositoryService;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
@WebFluxTest(controllers=GithubController.class)
@RunWith(SpringRunner.class)
public class GithubControllerTest {
    @Autowired
    private  WebTestClient webTestClient;

    @MockBean
    private  GitRepositoryService gitRepositoryService;

    @MockBean
    private GlobalExceptionHandler exceptionHandler;

    @MockBean
    private ResourceWebPropertiesConfig resourceWebPropertiesConfig;

    @Test
    public void test_that_when_application_json_header_is_valid() {
        // Arrange
        String authToken = "your-auth-token";
        String userName = "test-user";
        int pageSizeOfRepository = 100;
        int pageSizeOfBranch = 100;

        Repositories expectedRepositories = new Repositories();

        when(gitRepositoryService.getAllUserRepositoryWithBranches(userName, authToken, pageSizeOfRepository, pageSizeOfBranch))
                .thenReturn(Mono.just(expectedRepositories));

        // Act & Assert
        webTestClient.get().uri("/api/v1/github/gitrespository/{username}", userName)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Repositories.class);

    }

    @Test
    public void test_that_when_application_xml_header_is_invalid() {
        // Arrange
        String authToken = "your-auth-token";
        String userName = "test-user";
        int pageSizeOfRepository = 100;
        int pageSizeOfBranch = 100;

        Repositories expectedRepositories = new Repositories();

        when(gitRepositoryService.getAllUserRepositoryWithBranches(userName, authToken, pageSizeOfRepository, pageSizeOfBranch))
                .thenReturn(Mono.just(expectedRepositories));

        // Act & Assert
        webTestClient.get().uri("/api/v1/github/gitrespository/{username}", userName)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .header(HttpHeaders.ACCEPT, "application/xml")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(Repositories.class);

    }
}