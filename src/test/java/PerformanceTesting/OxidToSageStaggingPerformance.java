package PerformanceTesting;

import base.Base;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OxidToSageStaggingPerformance extends Base {
    int Total_Combinations;
    int MallId_Request;
    int ShopId_Request;
    int OrderID_request;
    int langID_request;
    String esbMessageID_ShopDB = null;
    String MallID_ShopDB = null;
    String ShopID_ShopDB=null;
    String CifNo_SapDB=null;
    String Message_SapDB=null;
    String esbMessage_SageDB=null;
    int Max_DB_Waiting_time = 90;
    Boolean test_case_status = true;
    String testCase_comment = "";
    List<Integer> mallID_lst = new ArrayList<>();
    List<Integer> shopID_lst = new ArrayList<>();
    List<Integer> langID_lst = new ArrayList<>();
    int output_sheet_row = 1;
    String Message_SageDB =null;
    String Referenzzeichen_SageDB = null;
    boolean scenario_status = true;
    String api_response = null;

    public OxidToSageStaggingPerformance(WebDriver driver) throws AWTException {
        super(driver);
    }
    public void getInputFromSheet() throws IOException {
        final String InputFile = "src/test/java/InputFiles/OxidToSage_Input.xlsx";
        FileInputStream file = new FileInputStream(new File(InputFile));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
//        System.out.println(workbook.getSheetAt(0).getRow(1).getCell(0).toString().replace(".0",""));
        Total_Combinations = sheet.getLastRowNum();
        for (int i = 1; i<= Total_Combinations; i++)
        {
            mallID_lst.add(Integer.parseInt(workbook.getSheetAt(0).getRow(i).getCell(0).toString().replace(".0","")));
            shopID_lst.add(Integer.parseInt(workbook.getSheetAt(0).getRow(i).getCell(1).toString().replace(".0","")));
            langID_lst.add(Integer.parseInt(workbook.getSheetAt(0).getRow(i).getCell(2).toString().replace(".0","")));
        }
    }

    public void getOrderNumber()
    {
        test_case_status = true;
        testCase_comment = "";
        OrderID_request =  randomOrderNumberGenerate();
    }

    public void pushOrderOxidToMiddlewareByAPICall() throws IOException {
        String payload = new String(Files.readAllBytes(Paths.get("src/test/java/jsonfiles/763029.json")));
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://10.100.6.106:755/api/OxidOrder/"+ MallId_Request +"/"+ ShopId_Request +"?apikey=483346b4-1955-4971-b896-2508b37c53da");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        System.out.println("OxidtoMiddleware API call response: "+ response.getStatusLine().getStatusCode()+ " "+response.getStatusLine().getReasonPhrase());
        api_response = response.getStatusLine().getStatusCode()+ " "+response.getStatusLine().getReasonPhrase();
        if (response.getStatusLine().getStatusCode() != 200 )
        {
            test_case_status = false;
        }
    }

    public void APIRequestWriter() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/java/jsonfiles/763029.json");

        Object obj = jsonParser.parse(reader);
        JSONObject attjsonobj = (JSONObject) obj;
        Gson json = new Gson();

        attjsonobj.put("MallId", MallId_Request);
        attjsonobj.put("ShopId", ShopId_Request);
        attjsonobj.put("orderNumber", OrderID_request);
        attjsonobj.put("langId", langID_request);

        try
        {
            FileWriter fileWriter = new FileWriter("src/test/java/jsonfiles/763029.json");
            fileWriter.append(attjsonobj.toString());
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }



    public void  removeThePreviousDatainOutputsheet() throws IOException {
        final String File = "src/test/java/output_performance_Testing/OxidToSageStage_Performance_output.xlsx";
        String Time = LocalDateTime.now().toString().replaceAll("[^A-Za-z0-9]", "-");
        FileInputStream file = new FileInputStream(new File(File));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i=1; i<=mallID_lst.size();i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < 9; j++) {
                row.createCell(j).setCellValue("");
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(File);
        workbook.write(fileOutputStream);
    }

    public void generateReport(int index) throws IOException {
        final String File = "src/test/java/output_performance_Testing/OxidToSageStage_Performance_output.xlsx";
        String Time = LocalDateTime.now().toString().replaceAll("[^A-Za-z0-9]", "-");
        FileInputStream file = new FileInputStream(new File(File));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int column = sheet.getRow(0).getPhysicalNumberOfCells();
        Row row = sheet.createRow(index+1);
        row.createCell(0).setCellValue(MallId_Request);
        row.createCell(1).setCellValue(ShopId_Request);
        row.createCell(2).setCellValue(OrderID_request);
        row.createCell(3).setCellValue(api_response);
        if (test_case_status)
            row.createCell(4).setCellValue("PASS");
        else
            row.createCell(4).setCellValue("FAILED");
        row.createCell(5).setCellValue(Time);
        for (int i=0;i<column;i++)
        {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOutputStream = new FileOutputStream(File);
        workbook.write(fileOutputStream);
    }

    public void oxidtoSageTestCall() throws IOException, ParseException, InterruptedException {
        getInputFromSheet();
        removeThePreviousDatainOutputsheet();
        for (int i = 0; i < mallID_lst.size(); i++) {
            MallId_Request = mallID_lst.get(i);
            ShopId_Request = shopID_lst.get(i);
            langID_request = langID_lst.get(i);
            getOrderNumber();
            APIRequestWriter();
            pushOrderOxidToMiddlewareByAPICall();
            generateReport(i);
            System.out.println();
            System.out.println("Mall ID:" + MallId_Request);
            System.out.println("Shop ID: " + ShopId_Request);
            System.out.println("OxidOrderID: " + OrderID_request);
            System.out.println("test case status: " + test_case_status);
            System.out.println("-------------------------------------------------");
        }
    }
}
