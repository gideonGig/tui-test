package com.tui.github.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.github.exception.GitResponseStatusException;
import com.tui.github.model.Branch;
import com.tui.github.model.Branches;
import com.tui.github.model.QueryRequest;
import com.tui.github.model.Repositories;
import com.tui.github.model.Repository;
import com.tui.github.model.UserResponse;
import com.tui.github.service.GitRepositoryService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GitRepositoryImpl implements GitRepositoryService {

    private final WebClient webClient;

    public GitRepositoryImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Repositories> getUserRepositoryWithBranches(String userName, String authorizationToken,
            int pageSizeOfRepository, int pageSizeOfBranch) {
        QueryRequest query = new QueryRequest();
        query.setQuery(getQuery(userName, pageSizeOfRepository, pageSizeOfBranch));
        return getUser(userName, authorizationToken)
                .flatMap(userResponse -> {
                    return webClient.post()
                            .uri("/graphql")
                            .header("Authorization", authorizationToken)
                            .bodyValue(query)
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(response -> mapResponse(response))
                            .onErrorResume(error -> {
                                log.error("Error occurred while processing GraphQL response", error);
                                return Mono.error(
                                        new RuntimeException("Error occurred while processing GraphQL response"));
                            });
                });
    }
    
    private Mono<Repositories> mapResponse(String jsonResponse) {
        Repositories repositories = new Repositories();
        ObjectMapper objectMapper = new ObjectMapper();
        return Mono.fromCallable(() -> {
            List<Repository> repositoryList = new ArrayList<>();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode repositoriesNode = rootNode.path("data").path("user").path("repositories").path("nodes");
            JsonNode isNextRepository = rootNode.path("data").path("user").path("repositories").path("pageInfo")
                    .path("hasNextPage");

            for (JsonNode repositoryNode : repositoriesNode) {
                Repository repository = new Repository();
                repository.setName(repositoryNode.path("name").asText());
                repository.setOwnerLogin(repositoryNode.path("owner").path("login").asText());

                Branches branches = new Branches();

                List<Branch> branchList = new ArrayList<>();
                JsonNode branchesNode = repositoryNode.path("refs").path("nodes");

                boolean isNextBranchNode = repositoryNode.path("refs").path("pageInfo").path("hasNextPage").asBoolean();
                for (JsonNode branchNode : branchesNode) {
                    Branch branch = new Branch();
                    branch.setName(branchNode.path("name").asText());
                    branch.setLastCommitSha(branchNode.path("target").path("oid").asText());
                    branchList.add(branch);
                }
                branches.setBranchHasNextPage(isNextBranchNode);
                branches.setBranches(branchList);
                repository.setBranches(branches);

                repositoryList.add(repository);
            }

            repositories.setRepositories(repositoryList);
            repositories.setRepositoryHasNextPage(isNextRepository.asBoolean());
            return repositories;

        }).onErrorMap(JsonProcessingException.class, e -> {
            log.error("Error occurred while mapping JSON properties", e);
            return new RuntimeException("Error occurred while mapping JSON properties", e);
        });
    }

    private Mono<UserResponse> getUser(String userName, String authorizationToken) {
        return webClient.get()
                .uri("/users/{username}", userName)
                .header("Authorization", authorizationToken)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError(),
                        clientResponse -> Mono
                                .error(new GitResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .bodyToMono(UserResponse.class);
    }

    /** GraphQL query string, to avoid multiple HTTP calls */
    private String getQuery(String userName, int pageSizeOfRepository, int pageSizeOfBranch) {
        return String.format(
                "query {" +
                        "   user(login: \"%s\") {" +
                        "    repositories(isFork: false,  first: %d, after: null) {" +
                        "       pageInfo {" +
                        "            hasNextPage" +
                        "            endCursor" +
                        "          }" +
                        "      nodes {" +
                        "        name" +
                        "        owner {" +
                        "          login" +
                        "        }" +
                        "        refs(refPrefix: \"refs/heads/\", first: %d, after: null) {" +
                        "          pageInfo {" +
                        "            hasNextPage" +
                        "            endCursor" +
                        "          }" +
                        "          nodes {" +
                        "            name" +
                        "            target {" +
                        "              ... on Commit {" +
                        "                oid" +
                        "                history(first: 1) {" +
                        "                  nodes {" +
                        "                    oid" +
                        "                  }" +
                        "                }" +
                        "              }" +
                        "            }" +
                        "          }" +
                        "        }" +
                        "      }" +
                        "    }" +
                        "  }" +
                        "}",
                userName, pageSizeOfRepository, pageSizeOfBranch);

    }

}
