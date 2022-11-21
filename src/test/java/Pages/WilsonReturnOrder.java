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
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pageObject.WilsonReturnPageObject;
import properties.WilsonOrderProperty;

import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WilsonReturnOrder extends Base {

    WilsonReturnPageObject wilsonReturnPageObject = new WilsonReturnPageObject();
    String responseBody ;
    String requestEsbMessageID = "315d93c7-2bb2-4158-b5f2-0bdb0ad";
    String newRequestMessageID;
    WilsonOrderProperty wilsonOrderProperty = new WilsonOrderProperty();
    boolean testcaseStatus = true;
    boolean scenrioStatus = true;
    boolean initializer_flag = true;
    boolean clear_flag = false;

    public WilsonReturnOrder(WebDriver driver) throws AWTException, IOException {
        super(driver);
        PageFactory.initElements(getDriver(), wilsonReturnPageObject);
    }
    AuthenticationGetProperty authenticationGetProperty = new AuthenticationGetProperty();

    private void getRandomEsbMessageID() {
        newRequestMessageID = "";
        testcaseStatus = true;
        newRequestMessageID = requestEsbMessageID + getTwelveDigitsAlphaNumericCode(5);
    }

    private void APIRequestWriter() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/test/java/jsonfiles/wilsonReturnOrder.json");

        Object obj = jsonParser.parse(reader);
        JSONObject attjsonobj = (JSONObject) obj;
        Gson json = new Gson();

        attjsonobj.put("Id", newRequestMessageID);

        try
        {
            FileWriter fileWriter = new FileWriter("src/test/java/jsonfiles/wilsonReturnOrder.json");
            fileWriter.append(attjsonobj.toString());
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    private void pushOrderOxidToMiddlewareByAPICall() throws IOException {
        testcaseStatus = true;
        String payload = new String(Files.readAllBytes(Paths.get("src/test/java/jsonfiles/wilsonReturnOrder.json")));
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://wilson-pub-stage.azurewebsites.net/api/return-order?code=bbg.wilson.pub.stage.20211011&clientId="+authenticationGetProperty.wilsonPubStageKey());
        request.setEntity(entity);

        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            responseBody = responseBody.substring(8,45).replace(",","");

            if (responseBody.equalsIgnoreCase(""))
            {
                testcaseStatus = false;
            }
        }
        else
        {
            testcaseStatus = false;
        }
        System.out.println("Wilson to SAP return Order API response: "+response.getStatusLine().getStatusCode()+response.getStatusLine().getReasonPhrase());
    }

    private void openNewRelic() throws InterruptedException {
        if (testcaseStatus) {
            goToPage("https://one.eu.newrelic.com/launcher/logger.log-launcher?platform[accountId]=");
        }
    }

    private void loginToNewRelic()
    {
        if (testcaseStatus) {
            waitUntilPageReady();
            wilsonReturnPageObject.login_txtBox.sendKeys(wilsonOrderProperty.getEmail());
            waitUntilClickable(wilsonReturnPageObject.next_btn_By);
            wilsonReturnPageObject.next_btn.click();
            waitUntilClickable(wilsonReturnPageObject.password_txtBox_By);
            wilsonReturnPageObject.password_txtBox.sendKeys(wilsonOrderProperty.getPassword());
            waitUntilClickable(wilsonReturnPageObject.login_btn_By);
            wilsonReturnPageObject.login_btn.click();
            if (initializer_flag)
                initializer_flag = false;
        }
    }

    private void searchForLogsOnNewRelic() throws InterruptedException {
        if (testcaseStatus) {
            int tries = 0;
            waitUntilPageReady();
            if (clear_flag && wilsonReturnPageObject.search_Clear_btn.size() > 0)
                wilsonReturnPageObject.search_Clear_btn.get(0).click();
            waitUntilVisible(wilsonReturnPageObject.logs_search_bar);
            wilsonReturnPageObject.logs_search_bar.click();
            wilsonReturnPageObject.logs_search_bar.sendKeys("");
            wilsonReturnPageObject.logs_search_bar.clear();
            waitUntilVisible(wilsonReturnPageObject.logs_search_bar);
            wilsonReturnPageObject.logs_search_bar.sendKeys(newRequestMessageID);
            clear_flag = true;
            do {
                waitUntilClickable(wilsonReturnPageObject.search_btn_by);
                wilsonReturnPageObject.search_btn.click();
                Thread.sleep(5000);
                tries++;
            } while (!elementExist(wilsonReturnPageObject.cifNo_log) && tries < 20);
            wilsonReturnPageObject.search_attributes_link.click();
            waitUntilClickable(wilsonReturnPageObject.attribute_search_By);
            wilsonReturnPageObject.attribute_search_txtBox.sendKeys("response.CIFNR");
            waitUntilClickable(wilsonReturnPageObject.attribute_Element_link_By);
            wilsonReturnPageObject.attribute_Element_link.click();
            waitUntilVisible(wilsonReturnPageObject.sapCifNo_link);

        }
    }

    private void verifyTheCifNo() {
        if (testcaseStatus) {
            if (wilsonReturnPageObject.sapCifNo_link.getText().equalsIgnoreCase("") || wilsonReturnPageObject.sapCifNo_link.getText().equalsIgnoreCase("null")) {
                testcaseStatus = false;
            }
        }
    }

    public void executor() throws IOException, InterruptedException, ParseException {
        for (int i=0;i<3;i++) {
            getRandomEsbMessageID();
            APIRequestWriter();
            pushOrderOxidToMiddlewareByAPICall();
            if (initializer_flag) {
                openNewRelic();
                loginToNewRelic();
            }
            searchForLogsOnNewRelic();
            verifyTheCifNo();
            if (!testcaseStatus)
                scenrioStatus = false;
            try {
                System.out.println();
                System.out.println("ESB Message ID :"+newRequestMessageID);
                System.out.println("The Return Order CifNo.: "+(wilsonReturnPageObject.sapCifNo_link.getText()));
                System.out.println("------------------------------------------------------------");
            } catch (Exception e) {
                break;
            }
        }
    }

    public boolean scenarioStatus()
    {
       return scenrioStatus;
    }
}
