package com.tui.github.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Getter
@Setter
@Slf4j
public class Repositories {
    private List<Repository> repositories;
    @JsonIgnore
    private boolean isRepositoryHasNextPage;
    @JsonIgnore
    private String endCursor;

    public static Mono<Repositories>  mapResponse(String jsonResponse) {
        log.info(String.format("response is %s", jsonResponse));
        Repositories repositories = new Repositories();
        ObjectMapper objectMapper = new ObjectMapper();
        return Mono.fromCallable(() -> {
            List<Repository> repositoryList = new ArrayList<>();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode repositoriesNode = rootNode.path("data").path("user").path("repositories").path("nodes");
            JsonNode isNextRepository = rootNode.path("data").path("user").path("repositories").path("pageInfo")
                    .path("hasNextPage");
            String endCursor = rootNode.path("data").path("user").path("repositories").path("pageInfo")
                    .path("endCursor").asText();

            if (endCursor.equals("") || endCursor.equals(null)) {
                endCursor = null;
            }

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
            repositories.setEndCursor(endCursor);
            return repositories;

        }).onErrorMap(JsonProcessingException.class, e -> {
            log.error("Error occurred while mapping JSON properties", e);
            return new RuntimeException("Error occurred while mapping JSON properties", e);
        });
    }

    public static Repositories mergeRepositories(Repositories repositories1, Repositories repositories2) {
        Repositories mergedRepositories = new Repositories();
        List<Repository> mergedList = new ArrayList<>(repositories1.getRepositories());
        mergedList.addAll(repositories2.getRepositories());
        mergedRepositories.setRepositories(mergedList);
        mergedRepositories.setRepositoryHasNextPage(repositories2.isRepositoryHasNextPage());
        return mergedRepositories;
    }
}
