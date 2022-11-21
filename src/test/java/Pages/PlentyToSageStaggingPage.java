package Pages;

import base.Base;
import com.google.gson.Gson;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlentyToSageStaggingPage extends Base {
    float referrerId = 0;
    String OrderID_request;
    int response_id;
    int MAX_DB_COUNT = 300;
    String esbMessageID_pmDB = null;
    String OrderId_pmDB  = null;
    boolean test_case_status = true;
    String CifNo_SapDB = null;
    String Message_SapDB = null;
    String Sap_id = null;
    String Referenzzeichen_SageDB = null;
    String Message_SageDB = null;
    int Total_Combinations = 0;
    List<Float> referrerIdlst = new ArrayList<>();
    String access_token = null;
    String testCase_comment = null;
    boolean scenario_status = true;

    public PlentyToSageStaggingPage(WebDriver driver) {
        super(driver);
    }

    public String getAuthToken() throws IOException {
        try {
            String payload = new String(Files.readAllBytes(Paths.get("src/test/java/jsonfiles/authtoken.json")));
            StringEntity entity = new StringEntity(payload,
                    ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("https://gev70dw63sws.c01-15.plentymarkets.com/rest/login");
            request.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            request.addHeader("Postman-Token", "<calculated when request is sent>");
            request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.addHeader(HttpHeaders.USER_AGENT, "PostmanRuntime/7.28.4");
            request.addHeader(HttpHeaders.ACCEPT, "*/*");
            request.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");
            request.addHeader(HttpHeaders.CONNECTION, "keep-alive");
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                String access_token = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                access_token = access_token.substring(58, 1035).replace("\"","");
                return access_token;
            } else {
                test_case_status = false;
                testCase_comment = "Unable to get auth token for API Call!";
                System.out.println(testCase_comment);
            }
        } catch (Exception e) {
            test_case_status = false;
            testCase_comment = "Unable to get auth token for API Call!";
            System.out.println(testCase_comment);
        }
        return null;
    }


    public void getOrderNumber()
    {
        test_case_status = true;
        testCase_comment = "";
        OrderID_request = String.valueOf(randomOrderNumberGenerate());
    }

    public void getInputFromSheet() throws IOException {
        final String InputFile = "src/test/java/InputFiles/PlentyToSage_Input.xlsx";
        FileInputStream file = new FileInputStream(new File(InputFile));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
//        System.out.println(workbook.getSheetAt(0).getRow(1).getCell(0).toString().replace(".0",""));
        Total_Combinations = sheet.getLastRowNum();
        for (int i = 1; i<= Total_Combinations; i++)
        {
            referrerIdlst.add(Float.valueOf(workbook.getSheetAt(0).getRow(i).getCell(0).toString()));
        }
    }

    public void APIRequestWriter() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/java/jsonfiles/plantyjson1.json");
        Object obj = jsonParser.parse(reader);
        JSONObject attjsonobj = (JSONObject) obj;
        JSONArray jsonArray = (JSONArray)attjsonobj.get("orderItems");
        JSONArray jsonArray2 = (JSONArray)attjsonobj.get("properties");
        Gson json = new Gson();
        for (Object o : jsonArray) {
            JSONObject att = (JSONObject) o;
            att.replace("referrerId", referrerId);
        }

        JSONObject att = (JSONObject) jsonArray2.get(2);
        att.put("value",OrderID_request);

        try
        {
            FileWriter fileWriter = new FileWriter("src/test/java/jsonfiles/plantyjson1.json");
            fileWriter.append(attjsonobj.toString());
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    public void pushOrderPlentyMarketToMiddlewareByAPICall() throws IOException {
        String payload = new String(Files.readAllBytes(Paths.get("src/test/java/jsonfiles/plantyjson1.json")));
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://gev70dw63sws.c01-15.plentymarkets.com/rest/orders");
        request.addHeader("Cookie","plentyID=eyJpdiI6IldIUlNlTWZBaTNleU50N21sbkMxTWc9PSIsInZhbHVlIjoiT1A0Nm43dG12bldTaGFhTkxuWGZZWFhtaWZ1c1lnWlBiY0lyMWlSVUZYRHBPRXFrTWhabHI1T24wK2l3aHNZTyIsIm1hYyI6ImM1YmU1NzMzYTViZDg1ZWU4YWU3ZmE4OWVhYTJkMzNhYTM0ZTZlNTI1NDliOThlYjJhY2M3ZmM3N2JjOGI0MjcifQ%3D%3D");
        request.addHeader(HttpHeaders.CACHE_CONTROL,"no-cache");
        request.addHeader("Postman-Token","<calculated when request is sent>");
        request.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
        request.addHeader(HttpHeaders.USER_AGENT,"PostmanRuntime/7.28.4");
        request.addHeader(HttpHeaders.ACCEPT,"*/*");
        request.addHeader(HttpHeaders.ACCEPT_ENCODING,"gzip, deflate, br");
        request.addHeader(HttpHeaders.CONNECTION,"keep-alive");
        request.addHeader(HttpHeaders.AUTHORIZATION,"Bearer "+access_token);
        request.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println("PM to Middleware API call response: "+ response.getStatusLine().getStatusCode()+ " "+response.getStatusLine().getReasonPhrase());
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                responseBody = responseBody.substring(6,12).replace(",","");
                response_id = Integer.parseInt(responseBody);
            }
            else
            {
                test_case_status = false;
                testCase_comment = "Unable to generate Plenty market orders from API!";
                System.out.println(testCase_comment);
            }
        } catch (Exception e){
            test_case_status = false;
            testCase_comment = "Unable to generate Plenty market orders from API!";
            System.out.println(testCase_comment);
        }
    }

    public void APIRequestWriterAmazon() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/java/jsonfiles/PlantyAmazon.json");
        Object obj = jsonParser.parse(reader);
        JSONObject attjsonobj = (JSONObject) obj;
        attjsonobj.replace("referrerId", referrerId);
        JSONArray jsonArray = (JSONArray)attjsonobj.get("orderItems");
        JSONArray jsonArray2 = (JSONArray)attjsonobj.get("properties");
        Gson json = new Gson();
        for (Object o : jsonArray) {
            JSONObject att = (JSONObject) o;
            att.replace("referrerId", referrerId);
        }

        JSONObject att = (JSONObject) jsonArray2.get(2);
        att.put("value",OrderID_request);

        try
        {
            FileWriter fileWriter = new FileWriter("src/test/java/jsonfiles/PlantyAmazon.json");
            fileWriter.append(attjsonobj.toString());
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    public void amazonPushOrderPlentyMarketToMiddlewareByAPICall() throws IOException {
        String payload = new String(Files.readAllBytes(Paths.get("src/test/java/jsonfiles/PlantyAmazon.json")));
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://gev70dw63sws.c01-15.plentymarkets.com/rest/orders");
        request.addHeader("Cookie","plentyID=eyJpdiI6IldIUlNlTWZBaTNleU50N21sbkMxTWc9PSIsInZhbHVlIjoiT1A0Nm43dG12bldTaGFhTkxuWGZZWFhtaWZ1c1lnWlBiY0lyMWlSVUZYRHBPRXFrTWhabHI1T24wK2l3aHNZTyIsIm1hYyI6ImM1YmU1NzMzYTViZDg1ZWU4YWU3ZmE4OWVhYTJkMzNhYTM0ZTZlNTI1NDliOThlYjJhY2M3ZmM3N2JjOGI0MjcifQ%3D%3D");
        request.addHeader(HttpHeaders.CACHE_CONTROL,"no-cache");
        request.addHeader("Postman-Token","<calculated when request is sent>");
        request.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
        request.addHeader(HttpHeaders.USER_AGENT,"PostmanRuntime/7.28.4");
        request.addHeader(HttpHeaders.ACCEPT,"*/*");
        request.addHeader(HttpHeaders.ACCEPT_ENCODING,"gzip, deflate, br");
        request.addHeader(HttpHeaders.CONNECTION,"keep-alive");
        request.addHeader(HttpHeaders.AUTHORIZATION,"Bearer "+access_token);
        request.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println("PM to Middleware API call response: "+ response.getStatusLine().getStatusCode()+ " "+response.getStatusLine().getReasonPhrase());
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                responseBody = responseBody.substring(6,12).replace(",","");
                response_id = Integer.parseInt(responseBody);
            }
            else
            {
                test_case_status = false;
                testCase_comment = "Unable to generate Plenty market orders from API!";
                System.out.println(testCase_comment);
            }
        } catch (Exception e){
            test_case_status = false;
            testCase_comment = "Unable to generate Plenty market orders from API!";
            System.out.println(testCase_comment);
        }
    }

    public void GettingDataFromPmDB()
    {
        if (test_case_status) {
            int count =0;
            String url = "jdbc:sqlserver://10.100.6.106\\SQLEXPRESS;databaseName=PmDatabase;portNumber=59232";
            String username = "";
            String password = "";

            try {
                Connection connection = DriverManager.getConnection(url,username,password);
                do {
                    try {
                        count++;
                        Thread.sleep(1000);
                        OrderId_pmDB = getOrderIDFromPmDB(connection);
                        esbMessageID_pmDB = getEsbMessageIDFromPmDB(connection);
                        if (!OrderId_pmDB.equalsIgnoreCase("") && !OrderId_pmDB.equalsIgnoreCase("null"))
                            break;
                    } catch (Exception e) {
                        continue;
                    }
                } while (count < MAX_DB_COUNT);

            } catch (SQLException e) {
                test_case_status = false;
                testCase_comment = "Not connected to the PM DB!";
                System.out.println(testCase_comment);
            }
        }
    }

    private String getOrderIDFromPmDB(Connection connection) throws SQLException {
        String esbMessageID = null;
        PreparedStatement stmt = connection.prepareStatement("select OrderId from PmDatabase.PlentyMarket.OrderSyncLog with (NoLock) where OrderId='"+response_id+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            esbMessageID = resultSet.getString(1);
        }
        return esbMessageID;
    }

    private String getEsbMessageIDFromPmDB(Connection connection) throws SQLException {
        String esbMessageID = null;
        PreparedStatement stmt = connection.prepareStatement("select EsbMessageId from PmDatabase.PlentyMarket.OrderSyncLog with (NoLock) where OrderId='"+response_id+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            esbMessageID = resultSet.getString(1);
        }
        return esbMessageID;
    }

    private void verifyTheDataFromPmDB()
    {
        if (test_case_status) {
            try {
                if (response_id !=Integer.parseInt(OrderId_pmDB) || esbMessageID_pmDB.equalsIgnoreCase("") || esbMessageID_pmDB.equalsIgnoreCase("null"))
                {
                    test_case_status = false;
                    testCase_comment = "The order data is not correct in PM DB!";
                    System.out.println(testCase_comment);
                }
            } catch (Exception e) {
                test_case_status = false;
                testCase_comment = "The order data is not correct in PM DB!";
                System.out.println(testCase_comment);
            }
        }
    }

    public void gettingDataFromSageDB() throws InterruptedException {
        if (test_case_status) {
            int db_count = 0;
            String url = "";

            do {
                try {
                    db_count++;
                    Connection connection = DriverManager.getConnection(url);
                    test_case_status = true;
                    testCase_comment = "";
                    sageDBEntities(connection);
                    break;
                }
                catch (SQLException e) {
                    test_case_status = false;
                    testCase_comment = "Not successfully Connected to Sage DB!";
                    Thread.sleep(500);
                    continue;
                }
            } while (db_count < 10);
            System.out.println(testCase_comment);
        }
    }

    private void sageDBEntities(Connection connection) {
        int count = 0;
        do {
            try {
                count++;
                Thread.sleep(1000);
                Message_SageDB = MessageIDFromSageDB(connection);
                Referenzzeichen_SageDB = ReferenzzeichenFromSageDB(connection);
                if (!Referenzzeichen_SageDB.equalsIgnoreCase("null") && !Referenzzeichen_SageDB.equalsIgnoreCase(""))
                    break;
            } catch (Exception e) {
                continue;
            }
        } while (count < MAX_DB_COUNT);
    }


    public String MessageIDFromSageDB(Connection connection) throws SQLException {

        String EsbOrderJson = null;
        PreparedStatement stmt = connection.prepareStatement("select Message from [OLReweAbf].[dbo].[LbCustomImportBelege_ImportLog] where EsbOrderJson like '%"+esbMessageID_pmDB+"%';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            EsbOrderJson = resultSet.getString(1);
        }
        return EsbOrderJson;
    }

    public String ReferenzzeichenFromSageDB(Connection connection) throws SQLException {

        String Referenzzeichen = null;
        PreparedStatement stmt = connection.prepareStatement("select Referenzzeichen from [OLReweAbf].[dbo].[LbCustomImportBelege_ImportLog] where EsbOrderJson like '%"+esbMessageID_pmDB+"%';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            Referenzzeichen = resultSet.getString(1);
        }
        return Referenzzeichen;
    }

    public void verifyTheOrderInSageDB()
    {
        if (test_case_status) {
            try {
                if ((Referenzzeichen_SageDB.equalsIgnoreCase("null") || Referenzzeichen_SageDB.equalsIgnoreCase("")))
                {
                    testCase_comment = "Either the order is not processed by the Sage ERP or not existed in Sage DB!";
                    test_case_status = false;
                    System.out.println(testCase_comment);
                }
                try {
                    if ((!Message_SageDB.equalsIgnoreCase("")) && (!Message_SageDB.equalsIgnoreCase("null")))
                    {
                        testCase_comment = "Either the order is not processed by the Sage ERP or not existed in Sage DB!";
                        test_case_status = false;
                        System.out.println(testCase_comment);
                    }
                } catch (Exception e) {

                }

            } catch (Exception e) {
                testCase_comment = "Either the order is not processed by the Sage ERP or not existed in Sage DB!";
                test_case_status = false;
                System.out.println(testCase_comment);
            }
        }
    }

    public void gettingDataFromSAPDB()
    {
//        if (test_case_status) {
            int count =0;
            String url = "jdbc:sqlserver://10.100.6.106\\SQLEXPRESS;databaseName=SAPManagerService;portNumber=59232";
            String username = "";
            String password = "";
            try {
                Connection connection = DriverManager.getConnection(url,username,password);
                do {
                    try {
                        Thread.sleep(1000);
                        count++;
                        CifNo_SapDB = getCifNoFromSAPDB(connection);
                        //  Message_SapDB = getSapMessageFromSAPDB(connection);
                        if ((CifNo_SapDB.equalsIgnoreCase("null") ||  Message_SapDB.equalsIgnoreCase("null") || CifNo_SapDB.equalsIgnoreCase("") ||  Message_SapDB.equalsIgnoreCase("")))
                            continue;
                        else
                            break;
                    } catch (Exception e) {
                        continue;
                    }
                } while (count < MAX_DB_COUNT);

            } catch (SQLException e) {
                test_case_status=false;
                testCase_comment = "Not successfully Connected to SAP DB!";
                System.out.println(testCase_comment);
            }
//        }
    }

    private String getCifNoFromSAPDB(Connection connection) throws SQLException {
        String cifNo = null;
        PreparedStatement stmt = connection.prepareStatement("select SapCifNo from [SAPManagerService].[sm].[SapOrderImportLog] where EsbMessageId='"+esbMessageID_pmDB+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            cifNo = resultSet.getString(1);
        }
        return cifNo;
    }

    private String getSapMessageFromSAPDB(Connection connection) throws SQLException {

        String sapMessage = null;
        PreparedStatement stmt = connection.prepareStatement("select SapMessage from [SAPManagerService].[sm].[SapOrderImportLog] where EsbMessageId='"+esbMessageID_pmDB+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            sapMessage = resultSet.getString(1);
        }
        return sapMessage;
    }

    public void verifyTheSameOrderNotPresentInSapDb()
    {
//        if (test_case_status) {
            try {
                if (!CifNo_SapDB.equalsIgnoreCase("") && !CifNo_SapDB.equalsIgnoreCase("null")){
                    test_case_status = false;
                    testCase_comment = "Same order is also processed by SAP ERP";
                    System.out.println(testCase_comment);
                }
            } catch (Exception e) {
//                testCase_comment = "Order successfully processed by Sage ERP and not found in SAP DB";
//                System.out.println(testCase_comment);
            }
       // }
    }

    public void generateReport(int index) throws IOException {
        final String File = "src/test/java/outputfiles/PlentyToSageStagging_Output.xlsx";
        String Time = LocalDateTime.now().toString().replaceAll("[^A-Za-z0-9]", "-");
        FileInputStream file = new FileInputStream(new File(File));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int column = sheet.getRow(0).getPhysicalNumberOfCells();
        Row row = sheet.createRow(index+1);
        row.createCell(0).setCellValue(referrerId);
        row.createCell(1).setCellValue(response_id);
        row.createCell(2).setCellValue(esbMessageID_pmDB);
        row.createCell(3).setCellValue(Referenzzeichen_SageDB);
        row.createCell(4).setCellValue(Message_SageDB);
        if (test_case_status)
            row.createCell(5).setCellValue("PASS");
        else
            row.createCell(5).setCellValue("FAILED");
        row.createCell(6).setCellValue(testCase_comment);
        row.createCell(7).setCellValue(Time);
        for (int i=0;i<column;i++)
        {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOutputStream = new FileOutputStream(File);
        workbook.write(fileOutputStream);
    }

    public void  removeThePreviousDatainOutputsheet() throws IOException {
        final String File = "src/test/java/outputfiles/PlentyToSageStagging_Output.xlsx";
        String Time = LocalDateTime.now().toString().replaceAll("[^A-Za-z0-9]", "-");
        FileInputStream file = new FileInputStream(new File(File));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i=1; i<=referrerIdlst.size();i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < 8; j++) {
                row.createCell(j).setCellValue("");
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(File);
        workbook.write(fileOutputStream);
    }

    public void PlentyToSageTestCall() throws IOException, ParseException, InterruptedException {
        access_token =  getAuthToken();
        System.out.println("The access token is: "+access_token);
//        getInputFromSheet();
//        removeThePreviousDatainOutputsheet();
//        for (int i=0; i<referrerIdlst.size()-1; i++) {
//            referrerId = referrerIdlst.get(i);
//            getOrderNumber();
//            APIRequestWriter();
//            pushOrderPlentyMarketToMiddlewareByAPICall();
//            GettingDataFromPmDB();
//            verifyTheDataFromPmDB();
//            gettingDataFromSageDB();
//            verifyTheOrderInSageDB();
//            gettingDataFromSAPDB();
//            verifyTheSameOrderNotPresentInSapDb();
//            if (test_case_status)
//            {
//                testCase_comment = "Order successfully processed by Sage ERP and not found in SAP DB";
//                System.out.println(testCase_comment);
//            }
//            generateReport(i);
//            System.out.println();
//            System.out.println("Referrer Id:"+referrerId);
//            System.out.println("Order Id: "+response_id);
//            System.out.println("EsbMessage ID: "+esbMessageID_pmDB);
//            System.out.println("Referenzzeichen No.: "+Referenzzeichen_SageDB);
//            System.out.println("Sage Message:"+Message_SageDB);
//            System.out.println("-------------------------------------------------");
//            if (scenario_status)
//                scenario_status = test_case_status;
//        }
//        for (int i=referrerIdlst.size()-1; i<referrerIdlst.size(); i++) {
//            referrerId = referrerIdlst.get(i);
//            getOrderNumber();
//            APIRequestWriterAmazon();
//            amazonPushOrderPlentyMarketToMiddlewareByAPICall();
//            GettingDataFromPmDB();
//            verifyTheDataFromPmDB();
//            gettingDataFromSageDB();
//            verifyTheOrderInSageDB();
//            gettingDataFromSAPDB();
//            verifyTheSameOrderNotPresentInSapDb();
//            if (test_case_status)
//            {
//                testCase_comment = "Order successfully processed by Sage ERP and not found in SAP DB";
//                System.out.println(testCase_comment);
//            }
//            generateReport(i);
//            System.out.println();
//            System.out.println("Referrer Id:"+referrerId);
//            System.out.println("Response Id: "+response_id);
//            System.out.println("EsbMessage ID: "+esbMessageID_pmDB);
//            System.out.println("Referenzzeichen No.: "+Referenzzeichen_SageDB);
//            System.out.println("Sage Message:"+Message_SageDB);
//            System.out.println("-------------------------------------------------");
//            if (scenario_status)
//                scenario_status = test_case_status;
//        }
    }
    public boolean verifyTheScenario()
    {
        return scenario_status;
    }
}
