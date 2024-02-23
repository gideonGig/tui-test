package com.tui.github.ImplTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.tui.github.model.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import com.tui.github.exception.GitResponseStatusException;
import com.tui.github.model.Repositories;
import com.tui.github.service.GitRepositoryService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class GitRepositoryImplTest {
    @Mock
    private GitRepositoryService gitRepositoryService;

    @Test
    public void test_that_when_success_all_repsoitories_are_merged() {

        Repositories repositories1 = new Repositories();
        repositories1.setRepositoryHasNextPage(true);
        repositories1.setEndCursor("cursor1");
        Repositories repositories2 = new Repositories();
        repositories2.setRepositoryHasNextPage(false);
        repositories1.setEndCursor("cursor2");

        when(gitRepositoryService.getAllUserRepositoryWithBranches(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Mono.just(repositories1))
                .thenReturn(Mono.just(repositories2));

        // Invoke the method to be tested
        Mono<Repositories> result = gitRepositoryService.getAllUserRepositoryWithBranches("username", "token", 20, 20);

        // Verify the result using StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(repositories -> repositories != null && repositories.getEndCursor().equals("cursor2"))
                .verifyComplete();
    }

   @Test
    public void test_user_not_found_throw_error() {
    
        // Define test data
        String userName = "username";
        String authToken = "invalidToken"; // Invalid or unauthorized token

        // Mock the behavior of the service method to throw an exception when an unauthorized user is detected
        when(gitRepositoryService.getAllUserRepositoryWithBranches(anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(new GitResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Invoke the method with the invalid token and verify that it throws the expected exception
        assertThrows(GitResponseStatusException.class, () -> {
            Mono<Repositories> result = gitRepositoryService.getAllUserRepositoryWithBranches(userName, authToken, 20, 20);
        });
    }


    @Test
    public void test_map_response_success() throws Exception {
        String jsonString = "{" +
                "\"data\": {" +
                "\"user\": {" +
                "\"repositories\": {" +
                "\"pageInfo\": {" +
                "\"hasNextPage\": true," +
                "\"endCursor\": \"Y3Vyc29yOnYyOpHOHcbR1w==\"" +
                "}," +
                "\"nodes\": [" +
                "{" +
                "\"name\": \"gidcode\"," +
                "\"owner\": {" +
                "\"login\": \"gideonGig\"" +
                "}," +
                "\"refs\": {" +
                "\"pageInfo\": {" +
                "\"hasNextPage\": false," +
                "\"endCursor\": null" +
                "}," +
                "\"nodes\": []" +
                "}" +
                "}" +
                "]" +
                "}}}}";


      Mono<Repositories> repositories =  Repositories.mapResponse(jsonString);
      assertEquals(repositories.block().getRepositories().size(), 1);
      assertEquals(repositories.block().isRepositoryHasNextPage(), true);
      assertEquals(repositories.block().getEndCursor(), "Y3Vyc29yOnYyOpHOHcbR1w==");

    }

    @Test
    public void testMergeRepositories() {

        Repository repo1 = new Repository();
        repo1.setName("branch1");
        repo1.setOwnerLogin("ownwer1");
        Repository repo2 = new Repository();
        repo2.setName("branch2");
        repo2.setOwnerLogin("ownwer2");


        Repositories repositories1 = new Repositories();
        repositories1.setRepositories(Arrays.asList(repo1));
        repositories1.setRepositoryHasNextPage(true);

        Repositories repositories2 = new Repositories();
        repositories2.setRepositories(Arrays.asList(repo2));
        repositories2.setRepositoryHasNextPage(false);

        // Call the mergeRepositories method
        Repositories mergedRepositories = Repositories.mergeRepositories(repositories1, repositories2);

        // Verify the merged repositories
        List<Repository> mergedList = mergedRepositories.getRepositories();
        assertEquals(2, mergedList.size());
        assertEquals("branch1", mergedList.get(0).getName());
        assertEquals("branch2", mergedList.get(1).getName());

        //assert first repository Nextpage is set at True
        assertTrue(repositories1.isRepositoryHasNextPage());

        //assert that merged repository nextpage is set at the repository2
        assertFalse(mergedRepositories.isRepositoryHasNextPage());
    }



}
