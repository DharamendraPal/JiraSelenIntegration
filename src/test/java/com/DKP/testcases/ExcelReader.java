package com.DKP.testcases;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ExcelReader {

	// Function to read and process all rows from the Excel file
    public void readExcelDataAndProcess(String filePath) throws Exception {
        FileInputStream fis = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        // Read header row (first row)
        Row headerRow = sheet.getRow(0);
        int numColumns = headerRow.getPhysicalNumberOfCells();
        String[] headers = new String[numColumns];

        // Store headers in an array
        for (int i = 0; i < numColumns; i++) {
            headers[i] = headerRow.getCell(i).getStringCellValue();
        }

        // Iterate through each row starting from the second row (index 1)
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            Map<String, String> rowData = new HashMap<>();

            // Store the data of the current row in a HashMap
            for (int j = 0; j < numColumns; j++) {
                Cell cell = row.getCell(j);
                String cellValue = (cell != null) ? cell.toString() : "";
                rowData.put(headers[j], cellValue);  // Store header as key and cell value as value
            }

            // Process the current row (create JIRA story or bug, attach files)
            processRowData(rowData);
        }

        workbook.close();
        fis.close();
    }

    // Function to process each row's data (create JIRA issue and attach files)
    private void processRowData(Map<String, String> rowData) {
        String summary = rowData.get("Summary");
        String description = rowData.get("Description");
        String issueType = rowData.get("Issue Type");
        String imagePath = rowData.get("Image Path");
        String excelFilePath = rowData.get("Excel File Path");
        String assignee = rowData.get("Assignee");
        String labels = rowData.get("Labels");
        String parentIssue = rowData.get("Parent Issue");
        String team = rowData.get("Team");
        String sprint = rowData.get("Sprint");
        String storyPoints = rowData.get("Story Points");
        String epicName = rowData.get("Epic Name");
        String linkedIssues = rowData.get("Linked Issues");
        String restrictedTo = rowData.get("Restricted To");

        // Construct request body with additional fields
        StringBuilder requestBody = new StringBuilder("{\n" +
            "  \"fields\": {\n" +
            "    \"project\": {\n" +
            "      \"key\": \"SCRUM\"\n" +  // Use your correct project key
            "    },\n" +
            "    \"summary\": \"" + summary + "\",\n" +
            "    \"description\": {\n" +
            "      \"type\": \"doc\",\n" +
            "      \"version\": 1,\n" +
            "      \"content\": [\n" +
            "        {\n" +
            "          \"type\": \"paragraph\",\n" +
            "          \"content\": [\n" +
            "            {\n" +
            "              \"text\": \"" + description + "\",\n" +
            "              \"type\": \"text\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    \"issuetype\": {\n" +
            "      \"name\": \"" + issueType + "\"\n" +
            "    }");

        // Add Assignee
        if (assignee != null && !assignee.isEmpty()) {
            requestBody.append(",\n\"assignee\": {\"name\": \"").append(assignee).append("\"}");
        }

        // Add Labels
        if (labels != null && !labels.isEmpty()) {
            String[] labelArray = labels.split(",");
            requestBody.append(",\n\"labels\": ").append(arrayToJsonList(labelArray));
        }

        // Add Parent Issue
        //if (parentIssue != null && !parentIssue.isEmpty()) {
          //  requestBody.append("\"parent\": {\"key\": \"" + parentIssue + "\"},\n");
        //}

        // Add Team (custom field)
       // if (team != null && !team.isEmpty()) {
         //   requestBody.append("\"customfield_10000\": {\"value\": \"" + team + "\"},\n");  // Replace with your custom field ID
        //}

        // Add Sprint (custom field)
        //if (sprint != null && !sprint.isEmpty()) {
          //  requestBody.append(",\n\"1\": \"").append(sprint).append("\"");  // Replace with your custom field ID for Sprint
        //}

        // Add Story Points (custom field)
        //if (storyPoints != null && !storyPoints.isEmpty()) {
          //  requestBody.append("\"customfield_10002\": " + storyPoints + ",\n");  // Replace with your custom field ID for Story Points
        //}

        // Add Epic Name (if creating an Epic)
        //if (epicName != null && !epicName.isEmpty()) {
          //  requestBody.append("\"customfield_10003\": \"" + epicName + "\",\n");  // Replace with your custom field ID for Epic Name
        //}

        // Add Linked Issues
        /*
        if (linkedIssues != null && !linkedIssues.isEmpty()) {
            String[] linkedIssueArray = linkedIssues.split(",");
            requestBody.append("\"issuelinks\": [\n");
            for (String linkedIssue : linkedIssueArray) {
                requestBody.append("{\n" +
                    "  \"type\": {\"name\": \"Relates\"},\n" +  // Change "Relates" to other types if necessary
                    "  \"outwardIssue\": {\"key\": \"" + linkedIssue + "\"}\n" +
                    "},\n");
            }
            requestBody.append("],\n");
        }*/

        // Add Restricted To (Security Level)
      //  if (restrictedTo != null && !restrictedTo.isEmpty()) {
        //    requestBody.append("\"security\": {\"name\": \"" + restrictedTo + "\"},\n");
        //}

        requestBody.append("\n  }\n}");

        
     // Print generated JSON for debugging
        System.out.println("Generated JSON Request Body: " + requestBody);
        
        
        // Execute the JIRA API request
        String issueId = JiraAPI.createIssue(requestBody.toString());

        // Attach files (image and Excel file) to the created issue
        if (!imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            JiraAPI.attachFileToIssue(issueId, imageFile);
        }

        if (!excelFilePath.isEmpty()) {
            File excelFile = new File(excelFilePath);
            JiraAPI.attachFileToIssue(issueId, excelFile);
        }
        System.out.println("Issue created and files attached for issue ID: " + issueId);
    }

    
    private String arrayToJsonList(String[] array) {
        StringBuilder jsonList = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            jsonList.append("\"").append(array[i]).append("\"");
            if (i < array.length - 1) {
                jsonList.append(", ");
            }
        }
        jsonList.append("]");
        return jsonList.toString();
    }
}
