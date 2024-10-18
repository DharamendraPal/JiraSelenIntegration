package com.DKP.testcases;

import java.io.File;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class JiraAPI {

    private static final String BASE_URL = "";
    private static final String USERNAME = "";
    private static final String API_TOKEN = "";

 // Create a JIRA issue (Story or Bug)
    public static String createIssue(String requestBody ) {
        RestAssured.baseURI = BASE_URL;
        
        Response response = RestAssured
                .given()
                .auth().preemptive().basic(USERNAME, API_TOKEN)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post();

     // Check if the request was successful
        if (response.getStatusCode() != 201) {
            System.err.println("Failed to create issue. Status Code: " + response.getStatusCode());
            System.err.println("Response: " + response.getBody().asString());
            return null;  // Return null if issue creation fails
        }

        // Extract and return the issue ID from the response
        String issueId = response.jsonPath().getString("id");
        System.out.println("Issue created. Issue ID: " + issueId);
        System.out.println("Response: " + response.getBody().asString());
        return issueId;
    }

    // Attach a file (image or Excel) to a JIRA issue
    public static void attachFileToIssue(String issueId, File file) {
        RestAssured.baseURI = "" + issueId + "/attachments";

        RestAssured
                .given()
                .auth().preemptive().basic(USERNAME, API_TOKEN)
                .header("X-Atlassian-Token", "no-check")
                .multiPart("file", file)
                .post()
                .then()
                .statusCode(200)
                .log().all();
    }

}
