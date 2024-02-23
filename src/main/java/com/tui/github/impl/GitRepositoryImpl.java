package com.tui.github.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.tui.github.exception.GitResponseStatusException;
import com.tui.github.model.QueryRequest;
import com.tui.github.model.Repositories;
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
    public Mono<Repositories> getAllUserRepositoryWithBranches(String userName, String authorizationToken, int pageSizeOfRepository, int pageSizeOfBranch) {
       return  getAllRepositories(userName, authorizationToken, pageSizeOfRepository, pageSizeOfBranch, null);
    }
 
   
    private Mono<Repositories> getUserRepositoryWithBranches(String userName, String authorizationToken, int pageSizeOfRepository, int pageSizeOfBranch, String endCursor) {
        QueryRequest query = new QueryRequest();
        query.setQuery(getQuery(userName, pageSizeOfRepository, pageSizeOfBranch, endCursor));
        return getUser(userName, authorizationToken)
                .flatMap(userResponse -> {
                    return webClient.post()
                            .uri("/graphql")
                            .header("Authorization", authorizationToken)
                            .bodyValue(query)
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(Repositories::mapResponse)
                            .onErrorResume(error -> {
                                log.error(String.format("Error occurred while processing GraphQL response %s: response is : %s", error));
                                return Mono.error(
                                        new RuntimeException("Error occurred while processing GraphQL response"));
                            });
                });
    }
   
    /** this returns a  list of all repsoitories of the user, github has a limit of 100 repsoitories per page */
    private Mono<Repositories> getAllRepositories(String userName, String authorizationToken, int pageSizeOfRepository, int pageSizeOfBranch, String endCursor) {
        return getUserRepositoryWithBranches(userName, authorizationToken, pageSizeOfRepository, pageSizeOfBranch, endCursor)
                .flatMap(repositories -> {
                    if (repositories.isRepositoryHasNextPage()) {
                        return getAllRepositories(userName, authorizationToken, pageSizeOfRepository, pageSizeOfBranch, repositories.getEndCursor())
                                .map(additionalRepositories -> Repositories.mergeRepositories(repositories, additionalRepositories));
                    } else {
                        return Mono.just(repositories);
                    }
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
    private String getQuery(String userName, int pageSizeOfRepository, int pageSizeOfBranch, String endCursor) {
        String afterClause = (endCursor != null) ? ", after: \"" + endCursor + "\"" : "";

        return String.format(
                "query {" +
                        "   user(login: \"%s\") {" +
                        "    repositories(isFork: false,  first: %d%s) {" +
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
                userName, pageSizeOfRepository, afterClause, pageSizeOfBranch);

    }

}
