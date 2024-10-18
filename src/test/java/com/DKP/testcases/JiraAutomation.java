package com.DKP.testcases;


public class JiraAutomation {

    public static void main(String[] args) throws Exception {
        ExcelReader excelReader = new ExcelReader();
        String excelFilePath = "D:\\JAVASeleniumPractice\\JiraSelenIntegration\\src\\test\\resources\\JIRATestData.xlsx";
        excelReader.readExcelDataAndProcess(excelFilePath);
    }
}

