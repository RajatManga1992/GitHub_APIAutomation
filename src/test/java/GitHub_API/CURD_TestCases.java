package GitHub_API;

import Core.StatusCode;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.RestAssured.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class CURD_TestCases {

    // GitHub API Base URL
    private static final String BASE_URL = "https://api.github.com";

    // Personal Access Token for authentication (replace with your token)
    private static final String TOKEN = "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";

    // Repo owner and name for testing
    private static final String OWNER = "RajatManga1992";
    private static final String REPO = "Manga";

    @BeforeClass
    public void setup() {
        // Base URI for RestAssured
        RestAssured.baseURI = BASE_URL;
    }

    //Test Case for "Get Repository Details"
    @Test
    public void testGetRepositoryDetails() {
        // GET request to fetch repository details
        Response response = RestAssured
                .given()
                .header("Authorization", "token " + TOKEN)
                .get("/repos/" + OWNER + "/" + REPO);

        // Assert the status code is 200
        Assert.assertEquals(response.statusCode(), StatusCode.SUCCESS.code);

        // Assert the repository name and owner
        Assert.assertEquals(response.jsonPath().get("name"), REPO);
        Assert.assertEquals(response.jsonPath().get("owner.login"), OWNER);
    }

    //Test Case for "Get Repository Details with Invalid Repo"
    @Test
    public void testGetInvalidRepository() {
        // GET request for an invalid repository
        String invalidRepo = "nonexistent-repo";
        Response response = RestAssured
                .given()
                .header("Authorization", "token " + TOKEN)
                .get("/repos/" + OWNER + "/" + invalidRepo);

        // Assert the status code is 404
        Assert.assertEquals(response.statusCode(), StatusCode.NOT_FOUND.code);

        // Assert the error message
        Assert.assertEquals(response.jsonPath().get("message"), "Not Found");
    }

    // Test Case for "List Repository Collaborators"
    @Test
    public void testListRepositoryCollaborators() {
        // GET request to fetch repository collaborators
        Response response = RestAssured
                .given()
                .header("Authorization", "token " + TOKEN)
                .get("/repos/" + OWNER + "/" + REPO + "/collaborators");

        // Assert the status code is 200
        Assert.assertEquals(response.statusCode(), 200);

        // Assert the response contains a list
        Assert.assertTrue(response.jsonPath().getList("$").size() >= 0);
        //System.out.println(response.body().asPrettyString());
    }


    @Test
    public void testGetRepositoryLanguages() {
        // GET request to fetch the languages used in the repository
        String REPO1 = "GitDemo_01";
        Response response = RestAssured
                .given()
                .header("Authorization", "token " + TOKEN)
                .get("/repos/" + OWNER + "/" + REPO1 + "/languages");

        // Assert the status code is 200
        Assert.assertEquals(response.statusCode(), 200);

        // Assert the response contains language key-value pairs
        Assert.assertTrue(response.jsonPath().getMap("$").size() > 0);
    }


    @Test
    public void testDeleteRepository() {
        String repoToDelete = "test-repo-to-delete1";

        // First, create a repository to delete
        Response createResponse = RestAssured
                .given()
                .header("Authorization", "token " + TOKEN)
                .body("{\"name\": \"" + repoToDelete + "\"}")
                .post("/user/repos");

        // Assert the repository creation was successful
        Assert.assertEquals(createResponse.statusCode(), StatusCode.CREATED.code);

        // Now, delete the repository
        Response deleteResponse = RestAssured
                .given()
                .header("Authorization", "token " + TOKEN)
                .delete("/repos/" + OWNER + "/" + repoToDelete);

        // Assert the repository deletion was successful
        Assert.assertEquals(deleteResponse.statusCode(), StatusCode.NO_CONTENT.code);

        // Assert the repository is no longer accessible
        Response getResponse = RestAssured
                .given()
                .header("Authorization", "token " + TOKEN)
                .get("/repos/" + OWNER + "/" + repoToDelete);

        Assert.assertEquals(getResponse.statusCode(), StatusCode.NOT_FOUND.code);
    }


}
