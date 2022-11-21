package Pages;

import base.Base;
import com.google.gson.Gson;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
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

public class OxidToSageStaggingPage extends Base {
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
    int Max_DB_Waiting_time = 240;
    Boolean test_case_status = true;
    String testCase_comment = "";
    List<Integer> mallID_lst = new ArrayList<>();
    List<Integer> shopID_lst = new ArrayList<>();
    List<Integer> langID_lst = new ArrayList<>();
    int output_sheet_row = 1;
    String Message_SageDB =null;
    String Referenzzeichen_SageDB = null;
    boolean scenario_status = true;
    String oxid_id = RandomStringUtils.randomAlphanumeric(32);

    public OxidToSageStaggingPage(WebDriver driver) throws AWTException {
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
        oxid_id = RandomStringUtils.randomAlphanumeric(32);
    }

    public void pushOrderOxidToMiddlewareByAPICall() throws IOException {
        String payload = new String(Files.readAllBytes(Paths.get("src/test/java/jsonfiles/552552.json")));
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://10.100.6.106:755/api/OxidOrder/"+ MallId_Request +"/"+ ShopId_Request +"?apikey=483346b4-1955-4971-b896-2508b37c53da");
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        System.out.println("OxidtoMiddleware API call response: "+ response.getStatusLine().getStatusCode()+ " "+response.getStatusLine().getReasonPhrase());
    }

    public void APIRequestWriter() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/java/jsonfiles/552552.json");

        Object obj = jsonParser.parse(reader);
        JSONObject attjsonobj = (JSONObject) obj;
        Gson json = new Gson();

        attjsonobj.put("MallId", MallId_Request);
        attjsonobj.put("ShopId", ShopId_Request);
        attjsonobj.put("orderNumber", OrderID_request);
        attjsonobj.put("langId", langID_request);
        attjsonobj.put("oxid", oxid_id);
        JSONArray jsonArray = (JSONArray)attjsonobj.get("orderDetails");
        for (Object o : jsonArray) {
            JSONObject att = (JSONObject) o;
            att.replace("orderId", oxid_id);
        }
        try
        {
            FileWriter fileWriter = new FileWriter("src/test/java/jsonfiles/552552.json");
            fileWriter.append(attjsonobj.toString());
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    public void GettingDataFromShopManagementDB()
    {
        int count =0;
        String url = "jdbc:sqlserver://10.100.6.106\\SQLEXPRESS;databaseName=ShopManagement;portNumber=59232";
        String username = "";
        String password = "";

        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            esbMessageID_ShopDB = getEsbMessageIDFromShopDB(connection);
            MallID_ShopDB = getMallIDFromShopDB(connection);
            ShopID_ShopDB = getShopIDFromShopDB(connection);
        } catch (SQLException e) {
            test_case_status = false;
            testCase_comment = "Not connected to Shop Management DB";
            System.out.println(testCase_comment);
        }
    }

    private String getEsbMessageIDFromShopDB(Connection connection) throws SQLException {
        String esbMessageID = null;
        PreparedStatement stmt = connection.prepareStatement("select EsbMessageId from [ShopManagement].[SM].[OxidOrderLog] where OxidOrderNumber='"+OrderID_request+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            esbMessageID = resultSet.getString(1);
        }
        return esbMessageID;
    }

    private String getMallIDFromShopDB(Connection connection) throws SQLException {
        String MallID = null;
        PreparedStatement stmt = connection.prepareStatement("select MallId from [ShopManagement].[SM].[OxidOrderLog] where OxidOrderNumber='"+OrderID_request+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            MallID = resultSet.getString(1);
        }
        return MallID;
    }

    private String getShopIDFromShopDB(Connection connection) throws SQLException {
        String ShopID = null;
        PreparedStatement stmt = connection.prepareStatement("select ShopId from [ShopManagement].[SM].[OxidOrderLog] where OxidOrderNumber='"+OrderID_request+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            ShopID = resultSet.getString(1);
        }
        return ShopID;
    }

    public void verifyNewOrderDataFromShopManagementDB()
    {
        if (test_case_status) {
            try {
                if (MallId_Request != Integer.parseInt(MallID_ShopDB) || ShopId_Request != Integer.parseInt(ShopID_ShopDB) || esbMessageID_ShopDB.equalsIgnoreCase("") || esbMessageID_ShopDB.equalsIgnoreCase("NULL")) {
                    test_case_status=false;
                    testCase_comment = "The oxid order data is not correctly shown in Shop Management DB";
                    System.out.println(testCase_comment);
                }
            }
            catch(Exception ex)
            {
                test_case_status=false;
                testCase_comment = "The oxid order data is not correctly shown in Shop Management DB";
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
        } while (count < Max_DB_Waiting_time);
    }


    public String MessageIDFromSageDB(Connection connection) throws SQLException {

        String EsbOrderJson = null;
        PreparedStatement stmt = connection.prepareStatement("select Message from [OLReweAbf].[dbo].[LbCustomImportBelege_ImportLog] where EsbOrderJson like '%"+esbMessageID_ShopDB+"%';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            EsbOrderJson = resultSet.getString(1);
        }
        return EsbOrderJson;
    }

    public String ReferenzzeichenFromSageDB(Connection connection) throws SQLException {

        String Referenzzeichen = null;
        PreparedStatement stmt = connection.prepareStatement("select Referenzzeichen from [OLReweAbf].[dbo].[LbCustomImportBelege_ImportLog] where EsbOrderJson like '%"+esbMessageID_ShopDB+"%';");
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
                    if ((!Message_SageDB.equalsIgnoreCase("")) && (!Message_SageDB.equalsIgnoreCase("null") && (!Message_SageDB.contains("Request processed correctly"))))
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
                } while (count < Max_DB_Waiting_time);

            } catch (SQLException e) {
                test_case_status=false;
                testCase_comment = "Not successfully Connected to SAP DB!";
                System.out.println(testCase_comment);
            }
      //  }
    }

    private String getCifNoFromSAPDB(Connection connection) throws SQLException {
        String cifNo = null;
        PreparedStatement stmt = connection.prepareStatement("select SapCifNo from [SAPManagerService].[sm].[SapOrderImportLog] where EsbMessageId='"+esbMessageID_ShopDB+"';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            cifNo = resultSet.getString(1);
        }
        return cifNo;
    }

    private String getSapMessageFromSAPDB(Connection connection) throws SQLException {

        String sapMessage = null;
        PreparedStatement stmt = connection.prepareStatement("select SapMessage from [SAPManagerService].[sm].[SapOrderImportLog] where EsbMessageId='"+esbMessageID_ShopDB+"';");
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
                if (!CifNo_SapDB.equalsIgnoreCase("")){
                    test_case_status = false;
                    testCase_comment = "Same order is also processed by SAP ERP";
                    System.out.println(testCase_comment);
                }

            } catch (Exception e) {
//                testCase_comment = "Order successfully processed by Sage ERP and not found in SAP DB";
//                System.out.println(testCase_comment);
            }
//        }
    }

    public String getEsbMessageIDFromSageDB(Connection connection) throws SQLException {

        String EsbOrderJson = null;
        PreparedStatement stmt = connection.prepareStatement("select Message from [OLReweAbf].[dbo].[LbCustomImportBelege_ImportLog] where EsbOrderJson LIKE '%"+esbMessageID_ShopDB+"%';");
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next())
        {
            EsbOrderJson = resultSet.getString(1);
        }
        return EsbOrderJson;
    }

    public void  removeThePreviousDatainOutputsheet() throws IOException {
        final String File = "src/test/java/outputfiles/OxidToSageStagging_Output.xlsx";
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
        final String File = "src/test/java/outputfiles/OxidToSageStagging_Output.xlsx";
        String Time = LocalDateTime.now().toString().replaceAll("[^A-Za-z0-9]", "-");
        FileInputStream file = new FileInputStream(new File(File));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int column = sheet.getRow(0).getPhysicalNumberOfCells();
        Row row = sheet.createRow(index+1);
        row.createCell(0).setCellValue(MallId_Request);
        row.createCell(1).setCellValue(ShopId_Request);
        row.createCell(2).setCellValue(OrderID_request);
        row.createCell(3).setCellValue(esbMessageID_ShopDB);
        row.createCell(4).setCellValue(Message_SageDB);
        row.createCell(5).setCellValue(Referenzzeichen_SageDB);
        if (test_case_status)
            row.createCell(6).setCellValue("PASS");
        else
            row.createCell(6).setCellValue("FAILED");
        row.createCell(7).setCellValue(testCase_comment);
        row.createCell(8).setCellValue(Time);
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
        for (int i = 0; i < mallID_lst.size()-89; i++) {
            MallId_Request = mallID_lst.get(i);
            ShopId_Request = shopID_lst.get(i);
            langID_request = langID_lst.get(i);
            getOrderNumber();
            APIRequestWriter();
            pushOrderOxidToMiddlewareByAPICall();
            GettingDataFromShopManagementDB();
            verifyNewOrderDataFromShopManagementDB();
            gettingDataFromSageDB();
            verifyTheOrderInSageDB();
            gettingDataFromSAPDB();
            verifyTheSameOrderNotPresentInSapDb();
            if (test_case_status)
            {
                testCase_comment = "Order successfully processed by Sage ERP and not found in SAP DB";
                System.out.println(testCase_comment);
            }
            generateReport(i);
            System.out.println();
            System.out.println("Mall ID:"+MallId_Request);
            System.out.println("Shop ID: "+ShopId_Request);
            System.out.println("OxidOrderID: "+OrderID_request);
            System.out.println("EsbMessageID: "+esbMessageID_ShopDB);
            System.out.println("Referenzzeichen No.: "+Referenzzeichen_SageDB);
            System.out.println("Sage Message.: "+Message_SageDB);
            System.out.println("-------------------------------------------------");
            if (scenario_status)
                scenario_status = test_case_status;
        }
    }

    public boolean verifyTheScenario()
    {
        return scenario_status;
    }
}
