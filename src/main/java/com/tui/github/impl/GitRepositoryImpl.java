package com.tui.github.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.github.model.Branch;
import com.tui.github.model.QueryRequest;
import com.tui.github.model.Repository;
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
    public Mono<List<Repository>> getUserRepositoryWithBranches(String userName, String authorizationToken)  {
        ObjectMapper objectMapper = new ObjectMapper();
        QueryRequest query = new QueryRequest();
        query.setQuery(getQuery(userName));
        String jsonString = "";
        try{
            jsonString = objectMapper.writeValueAsString(query);

        } catch (JsonProcessingException exception){

        }
        return  webClient.post()
                .uri("/graphql")
                .header("Authorization", authorizationToken)
                .bodyValue(jsonString)
                .retrieve()
                .bodyToMono(String.class)
                .map(t -> {
                    try {
                        return mapResponse(t);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   return null;
                });
                
    }

    private void validateUser(String userName) {
        
    }

    /**private response handler for mapping Responses */
    private List<Repository> mapResponse(String jsonResponse) throws Exception {
        List<Repository> repositories = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode repositoriesNode = rootNode.path("data").path("user").path("repositories").path("nodes");
        
        for (JsonNode repositoryNode : repositoriesNode) {
            Repository repository = new Repository();
            repository.setName(repositoryNode.path("name").asText());
            repository.setOwnerLogin(repositoryNode.path("owner").path("login").asText());
            
            List<Branch> branches = new ArrayList<>();
            JsonNode branchesNode = repositoryNode.path("refs").path("nodes");
            for (JsonNode branchNode : branchesNode) {
                Branch branch = new Branch();
                branch.setName(branchNode.path("name").asText());
                branch.setLastCommitSha(branchNode.path("target").path("oid").asText());
                branches.add(branch);
            }
            repository.setBranches(branches);
            
            repositories.add(repository);
        }
        return repositories;
    }

    /** GraphQL query string, to avoid multiple HTTP calls */
    private String getQuery(String userName) {
          return String.format(
            "query {" +
            "   user(login: \"%s\") {" +
            "    repositories(isFork: false,  first: 100, after: null) {" +
            "       pageInfo {" +
            "            hasNextPage" +
            "            endCursor" +
            "          }" +
            "      nodes {" +
            "        name" +
            "        owner {" +
            "          login" +
            "        }" +
            "        refs(refPrefix: \"refs/heads/\", first: 100, after: null) {" +
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
            userName);

   
    }
    
}
