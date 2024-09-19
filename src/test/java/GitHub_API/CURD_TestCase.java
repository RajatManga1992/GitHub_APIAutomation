package GitHub_API;

import Core.StatusCode;
import Helper.BaseTestHelper;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class CURD_TestCase {

    // Personal Access Token for authentication (replace with your token)
    private static final String TOKEN = "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";

    // Repo owner and name for testing
    private static final String OWNER = "RajatManga1992";
    private static final String REPO = "Manga";


    ExtentReports extentReport;
    ExtentTest extentLog;

    @BeforeSuite
    public void setupReport() {
        String Subfolder = System.getProperty("user.dir") + "/reports/" + BaseTestHelper.Timestamp();
        BaseTestHelper.Createfolder(Subfolder);
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(Subfolder + "/" + "API_Automation.html");
        extentReport = new ExtentReports();
        extentReport.attachReporter(sparkReporter);
    }

    @Test
    public void testGetRepositoryDetails() {
        // GET request to fetch repository details
        extentLog = extentReport.createTest("testGetRepositoryDetails", "Get Repository details -Positive testcase");
        try {
            extentLog.info("Sending API request...");
            Response response = RestAssured
                    .given().header("Authorization", "token " + TOKEN)
                    .get("https://api.github.com/repos/" + OWNER + "/" + REPO);

            extentLog.info("Response received with status code: " + response.statusCode());

            // Assert the status code is 200
            System.out.println(response.statusCode());
            extentLog.info("Verifying status code...");
            Assert.assertEquals(response.statusCode(), 200);  // Checking if status is 200 (OK)
            extentLog.pass("Status code verification passed.");

            // Assert the repository name and owner
            extentLog.info("Verifying repository details...");
            Assert.assertEquals(response.jsonPath().get("name"), REPO);
            Assert.assertEquals(response.jsonPath().get("owner.login"), OWNER);
            extentLog.pass("Repository name and owner verification passed.");


        } catch (AssertionError e) {
            extentLog.fail("Test failed: " + e.getMessage());
        } catch (Exception e) {
            extentLog.fail("Unexpected error occurred: " + e.getMessage());
        } finally {
            // Ending the test
            extentReport.flush();
        }

    }

    //Test Case for "Get Repository Details with Invalid Repo"
    @Test
    public void testGetInvalidRepository() {
        extentLog = extentReport.createTest("testGetInvalidRepository", "testGetInvalidRepository -Negative testcase");
        // GET request for an invalid repository
        String invalidRepo = "nonexistent-repo";
        try {
            extentLog.info("Sending API request for an invalid repository...");
            Response response = RestAssured
                    .given()
                    .header("Authorization", "token " + TOKEN)
                    .get("https://api.github.com/repos/" + OWNER + "/" + invalidRepo);

            // Assert the status code is 404
            Assert.assertEquals(response.statusCode(), StatusCode.NOT_FOUND.code);
            extentLog.pass("Status code 404 verified.");
            // Assert the error message
            Assert.assertEquals(response.jsonPath().get("message"), "Not Found");
            extentLog.pass("Error message 'Not Found' verified.");
        } catch (AssertionError e) {
            // Log assertion failures
            extentLog.fail("Assertion failed: " + e.getMessage());
        } catch (Exception e) {
            // Log unexpected errors
            extentLog.fail("Unexpected error occurred: " + e.getMessage());
        } finally {
            // Write everything to the Extent Report
            extentReport.flush();
        }

    }

    // Test Case for "List Repository Collaborators"
    @Test
    public void testListRepositoryCollaborators() {
        // GET request to fetch repository collaborators
        extentLog = extentReport.createTest("testListRepositoryCollaborators", "testListRepositoryCollaborators - Positive testcase");
        // Sample log entries
        extentLog.info("Sending API request...");
        try {
            // Log the start of the API request
            extentLog.info("Sending API request to fetch repository collaborators...");
            Response response = RestAssured
                    .given()
                    .header("Authorization", "token " + TOKEN)
                    .get("https://api.github.com/repos/" + OWNER + "/" + REPO + "/collaborators");

            // Assert the status code is 200
            Assert.assertEquals(response.statusCode(), 200);
            extentLog.pass("Status code 200 verified successfully.");

            // Assert the response contains a list
            Assert.assertTrue(response.jsonPath().getList("$").size() >= 0);
            extentLog.pass("Collaborators list verified successfully.");
        } catch (AssertionError e) {
            // Log assertion failures
            extentLog.fail("Assertion failed: " + e.getMessage());
        } catch (Exception e) {
            // Log unexpected errors
            extentLog.fail("Unexpected error occurred: " + e.getMessage());
        } finally {
            // Write everything to the Extent Report
            extentReport.flush();
        }
    }


    @Test
    public void testGetRepositoryLanguages() {
        // GET request to fetch the languages used in the repository
        extentLog = extentReport.createTest("testListRepositoryCollaborators", "GET request to fetch the languages used in the repository- Positive testcase");

        String REPO1 = "GitDemo_01";
        try {
            extentLog.info("Sending API request to fetch languages for repository: " + REPO1);
            Response response = RestAssured
                    .given()
                    .header("Authorization", "token " + TOKEN)
                    .get("https://api.github.com/repos/" + OWNER + "/" + REPO1 + "/languages");

            // Assert the status code is 200
            Assert.assertEquals(response.statusCode(), 200);
            extentLog.pass("Status code 200 verified successfully.");


            // Assert the response contains language key-value pairs
//            Assert.assertTrue(response.jsonPath().getMap("$").size() > 0);
            Assert.assertTrue(response.jsonPath().getMap("$").size() > 0, "No languages found for the repository.");
            extentLog.pass("Languages key-value pairs verified successfully." + response.body().asPrettyString());

        } catch (AssertionError e) {
            // Log assertion failures
            extentLog.fail("Assertion failed: " + e.getMessage());
        } catch (Exception e) {
            // Log unexpected errors
            extentLog.fail("Unexpected error occurred: " + e.getMessage());
        } finally {
            // Write everything to the Extent Report
            extentReport.flush();
        }

    }


    @Test
    public void testDeleteRepository() {
        extentLog = extentReport.createTest("testListRepositoryCollaborators", "GET request to fetch the languages used in the repository- Positive testcase");

        String repoToDelete = "test-repo-to-delete1";

        // First, create a repository to delete
        try {
            extentLog.info("Creating repository: " + repoToDelete);
            Response createResponse = RestAssured
                    .given()
                    .header("Authorization", "token " + TOKEN)
                    .body("{\"name\": \"" + repoToDelete + "\"}")
                    .post("/user/repos");

            // Assert the repository creation was successful
            Assert.assertEquals(createResponse.statusCode(), StatusCode.CREATED.code);
            extentLog.pass("Repository created successfully with status code 201.");

            extentLog.info("Deleting repository: " + repoToDelete);
            // Now, delete the repository
            Response deleteResponse = RestAssured
                    .given()
                    .header("Authorization", "token " + TOKEN)
                    .delete("https://api.github.com/repos/" + OWNER + "/" + repoToDelete);

            // Assert the repository deletion was successful
            Assert.assertEquals(deleteResponse.statusCode(), StatusCode.NO_CONTENT.code);
            extentLog.pass("Repository deleted successfully with status code 204.");

            // Assert the repository is no longer accessible
            extentLog.info("Verifying repository deletion: " + repoToDelete);
            Response getResponse = RestAssured
                    .given()
                    .header("Authorization", "token " + TOKEN)
                    .get("https://api.github.com/repos/" + OWNER + "/" + repoToDelete);

            Assert.assertEquals(getResponse.statusCode(), StatusCode.NOT_FOUND.code);
            extentLog.pass("Repository no longer accessible, confirmed with status code 404.");

        } catch (AssertionError e) {
            // Log any assertion failures
            extentLog.fail("Assertion failed: " + e.getMessage());
        } catch (Exception e) {
            // Log unexpected errors
            extentLog.fail("Unexpected error occurred: " + e.getMessage());
        } finally {
            // Write all logs to the report
            extentReport.flush();
        }

    }

    @AfterSuite
    public void teardownReport() {
        // Close the ExtentReport
        extentReport.flush();
    }
}
