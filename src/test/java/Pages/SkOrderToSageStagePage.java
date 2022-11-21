package Pages;

import base.Base;
import com.google.gson.Gson;
import getProperty.AuthenticationGetProperty;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class SkOrderToSageStagePage extends Base {

    String ref_Number = "3e3c27da_21" + randomOrderNumberGenerate();
    boolean test_case_status = true;
    String testCase_comment = null;
    String Message_SageDB = null;
    String order_reference_SageDB = null;
    int Max_DB_Waiting_time = 340;

    AuthenticationGetProperty authenticationGetProperty = new AuthenticationGetProperty();

    public SkOrderToSageStagePage(WebDriver driver) {
        super(driver);
    }

    private void skOrderToMiddlewareByAPICall() throws IOException {
        try {
            String payload = new String(Files.readAllBytes(Paths.get("src/test/java/jsonfiles/skOrders.json")));
            StringEntity entity = new StringEntity(payload,
                    ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("https://sk-pub-stage.azurewebsites.net/api/order?code="+authenticationGetProperty.skOrderkey()+"");
            request.setEntity(entity);

            HttpResponse response = httpClient.execute(request);
            System.out.println("SkOrder to Middleware API call response: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            test_case_status = false;
            testCase_comment = "Not successfully Connected to Sage DB!";
        }
    }

    private void APIRequestWriter() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/java/jsonfiles/skOrders.json");
        Object obj = jsonParser.parse(reader);
        JSONObject attjsonobj = (JSONObject) obj;
        JSONObject attjsonobj2 = (JSONObject) obj;
        attjsonobj2 = (JSONObject) attjsonobj.get("data");
        attjsonobj2.put("refn", ref_Number);

        try {
            FileWriter fileWriter = new FileWriter("src/test/java/jsonfiles/skOrders.json");
            fileWriter.append(attjsonobj.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception ex) {
            System.out.println(ex);
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
                } catch (SQLException e) {
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
                order_reference_SageDB = orderReferenceFromSageDB(connection);
                if (!order_reference_SageDB.equalsIgnoreCase(""))
                    break;
            } catch (Exception e) {
                continue;
            }
        } while (count < Max_DB_Waiting_time);
    }


    private String MessageIDFromSageDB(Connection connection) throws SQLException {

        String EsbOrderJson = null;
        PreparedStatement stmt = connection.prepareStatement("select Message from [OLReweAbf].[dbo].[LbCustomImportBelege_ImportLog] with (nolock) where OrderReference = '" + ref_Number + "';");
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            EsbOrderJson = resultSet.getString(1);
        }
        return EsbOrderJson;
    }

    private String orderReferenceFromSageDB(Connection connection) throws SQLException {

        String Referenzzeichen = null;
        PreparedStatement stmt = connection.prepareStatement("select OrderReference from [OLReweAbf].[dbo].[LbCustomImportBelege_ImportLog] with (nolock) where OrderReference ='" + ref_Number + "';");
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            Referenzzeichen = resultSet.getString(1);
        }
        return Referenzzeichen;
    }

    public void verifyTheOrderInSageDB() {
        if (test_case_status) {
            try {
                if (order_reference_SageDB.contains(ref_Number) && Message_SageDB.contains("Request processed correctly"))
                {
                    testCase_comment = "";
                    return;
                }
                else if ((order_reference_SageDB.equalsIgnoreCase("null") || order_reference_SageDB.equalsIgnoreCase(""))) {
                    testCase_comment = "Either the order is not processed by the Sage ERP or not existed in Sage DB!";
                    test_case_status = false;
                    System.out.println(testCase_comment);
                    return;
                } else if (!ref_Number.equalsIgnoreCase(order_reference_SageDB)) {
                    testCase_comment = "Either the order is not processed by the Sage ERP or not existed in Sage DB!";
                    test_case_status = false;
                    System.out.println(testCase_comment);
                    return;
                }

                try {
                    if ((!Message_SageDB.equalsIgnoreCase("")) && (!Message_SageDB.equalsIgnoreCase("null"))) {
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

    public void jsonReader() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/java/jsonfiles/skOrders.json");
        Object obj = jsonParser.parse(reader);
        JSONObject attjsonobj = (JSONObject) obj;
        JSONObject attjsonobj2 = (JSONObject) obj;
        attjsonobj2 = (JSONObject) attjsonobj.get("data");
        String refn =   attjsonobj2.get("refn").toString();
        System.out.println("The refn number is: " + refn);
    }

        public void skOrdertoMiddlewareTestExecutor () throws IOException, ParseException {
//            jsonReader();
        try {
            APIRequestWriter();
            skOrderToMiddlewareByAPICall();
            System.out.println("Order reference number: "+ref_Number);
            gettingDataFromSageDB();
            verifyTheOrderInSageDB();
            System.out.println();
            System.out.println("Sage Message: "+Message_SageDB);
            System.out.println("-------------------------------------------------");
        } catch (Exception e) {
            testCase_comment = "Either the order is not processed by the Sage ERP or not existed in Sage DB!";
            test_case_status = false;
            System.out.println(testCase_comment);
        }
        }

    public boolean verifyTheScenario()
    {
        return test_case_status;
    }
    }

