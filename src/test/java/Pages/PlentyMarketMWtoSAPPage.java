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

public class PlentyMarketMWtoSAPPage extends Base {

    String esbMessageid_request = "d1f4fb2d-93f0-4aff-a6f1-";
    String mwrif_id = null;
    String ref_Num = null;
    float referrerId = 0;
    String OrderID_request;
    int response_id;
    int MAX_DB_COUNT = 500;
    String esbMessageID = null;
    String OrderId_pmDB  = null;
    boolean test_case_status = true;
    String CifNo_SapDB = null;
    String Message_SapDB = null;
    String Sap_id = null;
    String Referenzzeichen_SageDB = null;
    String Message_SageDB = null;
    int Total_Combinations = 0;
    List<Float> referrerIdlst = new ArrayList<>();
    List<String> mwreferrerlst = new ArrayList<>();
    String access_token = null;
    String testCase_comment = null;
    boolean scenario_status = true;


    public PlentyMarketMWtoSAPPage(WebDriver driver) {
        super(driver);
    }

    public String getAlphaNumbericid()
    {
        esbMessageid_request = esbMessageid_request+getRandomNumeric(6)+"d6a"+getRandomNumeric(3);
        return esbMessageid_request;
    }

    public String getDynamicRefnNumber()
    {
        ref_Num = getRandomNumeric(3)+"-"+getRandomNumeric(3)+"-"+getRandomNumeric(4);
        return ref_Num;
    }

    public void getOrderNumber()
    {
        test_case_status = true;
        testCase_comment = "";
        OrderID_request = String.valueOf(randomOrderNumberGenerate());
    }

    public void jsonReader() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/java/jsonfiles/PlentyToMW.json");
        Object obj = jsonParser.parse(reader);
        JSONObject attjsonobj = (JSONObject) obj;
        System.out.println("Id: "+attjsonobj.get("Id").toString());
        System.out.println("mwrfid: "+attjsonobj.get("mwrfid").toString());

        JSONObject att = (JSONObject) attjsonobj.get("data");
        System.out.println("cnl: "+ att.get("cnl"));
        System.out.println("custGr: "+ att.get("custGr"));
        System.out.println("refn: "+ att.get("refn"));
        }


    public void getInputFromSheet() throws IOException {
        final String InputFile = "src/test/java/InputFiles/MWPlentyInput_Sap.xlsx";
        FileInputStream file = new FileInputStream(new File(InputFile));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
//        System.out.println(workbook.getSheetAt(0).getRow(1).getCell(0).toString().replace(".0",""));
        Total_Combinations = sheet.getLastRowNum();
        for (int i = 1; i<= Total_Combinations; i++)
        {
            referrerIdlst.add(Float.valueOf(workbook.getSheetAt(0).getRow(i).getCell(0).toString()));
            mwreferrerlst.add(workbook.getSheetAt(0).getRow(i).getCell(0).toString());
        }
    }

    public void APIRequestWriter() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/java/jsonfiles/PlentyToMW.json");
        Object obj = jsonParser.parse(reader);
        JSONObject attjsonobj = (JSONObject) obj;
        Gson json = new Gson();
        attjsonobj.put("Id", getAlphaNumbericid());
        attjsonobj.put("mwrfid", mwrif_id);
        JSONObject att = (JSONObject) attjsonobj.get("data");
        att.put("refn",getDynamicRefnNumber());
        att.put("payref",ref_Num);
        try
        {
            FileWriter fileWriter = new FileWriter("src/test/java/jsonfiles/PlentyToMW.json");
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
        String payload = new String(Files.readAllBytes(Paths.get("src/test/java/jsonfiles/PlentyToMW.json")));
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://pm-pub-stage.azurewebsites.net/api/order?code=5MTSoXarGrDkyCfQLJi7ppDE2STfbQo1qMXnnvOA3vdhITtS8nSgWw==");
      //  request.addHeader("Cookie","plentyID=eyJpdiI6IldIUlNlTWZBaTNleU50N21sbkMxTWc9PSIsInZhbHVlIjoiT1A0Nm43dG12bldTaGFhTkxuWGZZWFhtaWZ1c1lnWlBiY0lyMWlSVUZYRHBPRXFrTWhabHI1T24wK2l3aHNZTyIsIm1hYyI6ImM1YmU1NzMzYTViZDg1ZWU4YWU3ZmE4OWVhYTJkMzNhYTM0ZTZlNTI1NDliOThlYjJhY2M3ZmM3N2JjOGI0MjcifQ%3D%3D");
        request.addHeader(HttpHeaders.CACHE_CONTROL,"no-cache");
        request.addHeader("Postman-Token","<calculated when request is sent>");
        request.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
        request.addHeader(HttpHeaders.USER_AGENT,"PostmanRuntime/7.28.4");
        request.addHeader(HttpHeaders.ACCEPT,"*/*");
        request.addHeader(HttpHeaders.ACCEPT_ENCODING,"gzip, deflate, br");
        request.addHeader(HttpHeaders.CONNECTION,"keep-alive");
        request.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println("PM to Middleware API call response: "+ response.getStatusLine().getStatusCode()+ " "+response.getStatusLine().getReasonPhrase());
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                esbMessageID = responseBody.substring(8,45).replace(",","");
                System.out.println(esbMessageID);
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

    public void gettingDataFromSAPDB()
    {
        if (test_case_status) {
            int count =0;
            String url = "jdbc:sqlserver://10.100.6.106\\SQLEXPRESS;databaseName=SAPManagerService;portNumber=59232";
            String username = "";
            String password = "";
            try {
                Connection connection = DriverManager.getConnection(url,username,password);
                do {
                    try {
                        count++;
                        Thread.sleep(1000);
                        Sap_id = getIdFromSapDb(connection);
                        if ((Sap_id.equalsIgnoreCase("null") ||  Sap_id.equalsIgnoreCase("")))
                            continue;
                        CifNo_SapDB = getCifNoFromSAPDB(connection);
                        Message_SapDB = getSapMessageFromSAPDB(connection);
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
        }
    }

    private String getIdFromSapDb(Connection connection) throws SQLException
    {
        String sapId = null;
        PreparedStatement stmt = connection.prepareStatement("SELECT Id FROM [SAPManagerService].[sapm].[SapOrderImportLog] with (nolock) where EsbMessageId ='"+esbMessageID+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            sapId = resultSet.getString(1);
        }
        return sapId;
    }

    private String getCifNoFromSAPDB(Connection connection) throws SQLException {
        String cifNo = null;
        PreparedStatement stmt = connection.prepareStatement("SELECT SapCifNo FROM [SAPManagerService].[sapm].[SapOrderImportLog] with (nolock) where EsbMessageId='"+esbMessageID+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            cifNo = resultSet.getString(1);
        }
        return cifNo;
    }

    private String getSapMessageFromSAPDB(Connection connection) throws SQLException {

        String sapMessage = null;
        PreparedStatement stmt = connection.prepareStatement("SELECT SapMessage FROM [SAPManagerService].[sapm].[SapOrderImportLog] with (nolock) where EsbMessageId ='"+esbMessageID+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            sapMessage = resultSet.getString(1);
        }
        return sapMessage;
    }

    public void verifyTheOrderInSapDB()
    {
        if (test_case_status) {
            try {
                if (CifNo_SapDB.equalsIgnoreCase("") || CifNo_SapDB.equalsIgnoreCase("null") || !Message_SapDB.equalsIgnoreCase("success")){
                    test_case_status = false;
                    testCase_comment = "Either the order is not processed by SAP ERP or not correctly shown in SAP DB!";
                    System.out.println(testCase_comment);
                }
            } catch (Exception e) {
                test_case_status = false;
                testCase_comment = "Either the order is not processed by SAP ERP or not correctly shown in SAP DB!";
                System.out.println(testCase_comment);
            }
        }
    }

    public void gettingDataFromSageDB()
    {
        int db_count=0;
        if (test_case_status) {
            String url = "";

            do {
                try {
                    db_count++;
                    Connection connection = DriverManager.getConnection(url);
                    test_case_status = true;
                    testCase_comment = "";
                    sageDBEntites(connection);
                    break;
                } catch (SQLException e) {
                    test_case_status = false;
                    testCase_comment = "Not successfully Connected to Sage DB!";
                    continue;
                }
            } while (db_count < 10);
            System.out.println(testCase_comment);
        }
    }

    private void sageDBEntites(Connection connection) {
        try {
            Message_SageDB = MessageIDFromSageDB(connection);
            Referenzzeichen_SageDB = ReferenzzeichenFromSageDB(connection);
        } catch (SQLException e) {
//                   testCase_comment = "Order successfully Processed by SAP ERP and NOT found in Sage DB!";
//            System.out.println(testCase_comment);
        }
    }

    public String MessageIDFromSageDB(Connection connection) throws SQLException {

        String EsbOrderJson = null;
        PreparedStatement stmt = connection.prepareStatement("select Messsage from [OLReweAbf].[dbo].[LbCustomImportBelege_ImportLog] where EsbOrderJson like '%"+ esbMessageID +"%';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            EsbOrderJson = resultSet.getString(1);
        }
        return EsbOrderJson;
    }

    public String ReferenzzeichenFromSageDB(Connection connection) throws SQLException {

        String Referenzzeichen = null;
        PreparedStatement stmt = connection.prepareStatement("select Referenzzeichen from [OLReweAbf].[dbo].[LbCustomImportBelege_ImportLog] where EsbOrderJson like '%"+ esbMessageID +"%';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            Referenzzeichen = resultSet.getString(1);
        }
        return Referenzzeichen;
    }

    public void verifyTheEsbOrderIDShouldNotPresentInSageDB()
    {
        if (test_case_status) {
            try {
                if ((!(Referenzzeichen_SageDB.equalsIgnoreCase("")) && !(Referenzzeichen_SageDB.equalsIgnoreCase("null"))) && Message_SageDB.equalsIgnoreCase(""))
                {
                    testCase_comment = "The Same order is existed in Sage DB as well!";
                    test_case_status = false;
                    System.out.println(testCase_comment);
                }

            } catch (Exception e) {
//                testCase_comment = "Order successfully Processed by SAP ERP and NOT found in Sage DB!";
//                System.out.println(testCase_comment);
            }
        }
    }
    public void generateReport(int index) throws IOException {
        final String File = "src/test/java/outputfiles/PlentyToSapStagging_Output.xlsx";
        String Time = LocalDateTime.now().toString().replaceAll("[^A-Za-z0-9]", "-");
        FileInputStream file = new FileInputStream(new File(File));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int column = sheet.getRow(0).getPhysicalNumberOfCells();
        Row row = sheet.createRow(index+1);
        row.createCell(0).setCellValue(referrerId);
        row.createCell(1).setCellValue(response_id);
        row.createCell(2).setCellValue(esbMessageID);
        row.createCell(3).setCellValue(CifNo_SapDB);
        row.createCell(4).setCellValue(Message_SapDB);
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
        final String File = "src/test/java/outputfiles/PlentyToSapStagging_Output.xlsx";
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

    public void PlentyToSapTestCall() throws IOException, ParseException {

        getInputFromSheet();
        removeThePreviousDatainOutputsheet();
//        for (int i = 0; i < referrerIdlst.size() - 7; i++) {
        for (int i = 0; i < 1; i++) {
            referrerId = referrerIdlst.get(i);
            mwrif_id = mwreferrerlst.get(i);

            getOrderNumber();
            APIRequestWriter();
            pushOrderPlentyMarketToMiddlewareByAPICall();

//            gettingDataFromSAPDB();
//            verifyTheOrderInSapDB();
//            if (test_case_status) {
//                testCase_comment = "Order successfully Processed by SAP ERP and NOT found in Sage DB!";
//                System.out.println(testCase_comment);
//            }
//            generateReport(i);
//            System.out.println();
//
//            System.out.println("SAPCif No.: " + CifNo_SapDB);
//            System.out.println("SAP Message:" + Message_SapDB);
//            System.out.println("-------------------------------------------------");
//            if (scenario_status)
//                scenario_status = test_case_status;
        }
//        for (int i=8; i<referrerIdlst.size()-1; i++)
//        {
//            referrerId = referrerIdlst.get(i);
//            getOrderNumber();
//            APIRequestWriterAmazonAuna();
//            amazonAunaPushOrderPlentyMarketToMiddlewareByAPICall();
//            gettingDataFromSAPDB();
//            verifyTheOrderInSapDB();
//            gettingDataFromSageDB();
//            verifyTheEsbOrderIDShouldNotPresentInSageDB();
//            if (test_case_status){
//                testCase_comment = "Order successfully Processed by SAP ERP and NOT found in Sage DB!";
//                System.out.println(testCase_comment);
//            }
//            generateReport(i);
//            System.out.println();
//            System.out.println();
//            System.out.println("Referrer Id:" + referrerId);
//            System.out.println("EsbMessage ID: " + esbMessageID);
//            System.out.println("SAPCif No.: " + CifNo_SapDB);
//            System.out.println("SAP Message:" + Message_SapDB);
//            System.out.println("-------------------------------------------------");
//            if (scenario_status)
//                scenario_status = test_case_status;
//        }
//
//        for (int i=referrerIdlst.size()-1; i<referrerIdlst.size(); i++)
//        {
//            referrerId = referrerIdlst.get(i);
//            getOrderNumber();
//            APIRequestWriterAmazonHomeSprit();
//            amazonHomeSpritPushOrderPlentyMarketToMiddlewareByAPICall();
//            gettingDataFromSAPDB();
//            verifyTheOrderInSapDB();
//            gettingDataFromSageDB();
//            verifyTheEsbOrderIDShouldNotPresentInSageDB();
//            if (test_case_status){
//                testCase_comment = "Order successfully Processed by SAP ERP and NOT found in Sage DB!";
//                System.out.println(testCase_comment);
//            }
//            generateReport(i);
//            System.out.println();
//            System.out.println();
//            System.out.println("Referrer Id:" + referrerId);
//            System.out.println("EsbMessage ID: " + esbMessageID);
//            System.out.println("SAPCif No.: " + CifNo_SapDB);
//            System.out.println("SAP Message:" + Message_SapDB);
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
