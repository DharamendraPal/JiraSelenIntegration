package com.DKP.testcases;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class JiraApiTokenTest {

    private static final String USERNAME = "";  // Replace with your JIRA email
    private static final String API_TOKEN = "";  // Replace with your API token
    private static final String JIRA_BASE_URL = "";

    public static void main(String[] args) {
        // Test the API token by getting user details
        RestAssured.baseURI = JIRA_BASE_URL;
        
        Response response = RestAssured
            .given()
            .auth().preemptive().basic(USERNAME, API_TOKEN)
            .header("Content-Type", "application/json")
            .get("/rest/api/2/search");
            //.get("/rest/agile/1.0/board/1/sprint\r\n");
            //.get("/rest/api/3/myself");
        
        

        // Print the response
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
        
        // Check if the status code is 200 (successful authentication)
        if (response.getStatusCode() == 200) {
            System.out.println("API token is valid and authentication is successful!");
        } else {
            System.out.println("Failed to authenticate. Check your API token or credentials.");
        }
    }
}

