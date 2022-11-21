package stepdefinitions;
//
//
import base.Base;
import base.BaseUtil;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

//
public class Hooks extends BaseUtil {

    private String scenarioName;
    private BaseUtil base;
    Base bse = new Base(driver);


    public Hooks(BaseUtil base) {
        this.base = base;
    }

    @Before
    public void InitializeTest(Scenario scenario) throws IOException {

//        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
//        base.driver = new ChromeDriver();
//        System.setProperty("webdriver.edge.driver", "msedgedriver.exe");
//        base.driver = new EdgeDriver();
//        base.driver.manage().window().maximize();
          scenarioName = scenario.getName();
//          System.out.println("Executing Scenario: "+scenarioName);
//          System.out.println();

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new
                FileOutputStream(FileDescriptor.out), "ASCII"), 512);
        out.write("Executing Scenario: "+scenarioName);
        out.write('\n');
        out.flush();

          if (scenarioName.equalsIgnoreCase("Oxid to SAP e2e testing (Staging)"))
          {
              final String InputFile = "src/test/java/InputFiles/OxidtoSap_Input.xlsx";
              FileInputStream file = new FileInputStream(new File(InputFile));
              Allure.addAttachment("Input file: ",file);
          }
          else if (scenarioName.equalsIgnoreCase("Oxid to Sage e2e testing (Staging)"))
          {
              final String InputFile = "src/test/java/InputFiles/OxidToSage_Input.xlsx";
              FileInputStream file = new FileInputStream(new File(InputFile));
              Allure.addAttachment("Input file: ",file);
          }
          else if (scenarioName.equalsIgnoreCase("Plenty Market to SAP e2e testing (Staging)")) {
              final String InputFile = "src/test/java/InputFiles/PlentyToSap_Input.xlsx";
              FileInputStream file = new FileInputStream(new File(InputFile));
              Allure.addAttachment("Input file: ", file);
          }

          else if (scenarioName.equalsIgnoreCase("Plenty Market to Sage e2e testing (Staging)"))
        {
            final String InputFile = "src/test/java/InputFiles/PlentyToSage_Input.xlsx";
            FileInputStream file = new FileInputStream(new File(InputFile));
            Allure.addAttachment("Input file: ",file);
        }

          else if (scenarioName.equalsIgnoreCase("Wilson to SAP return order (Staging)") || scenarioName.equalsIgnoreCase("Wilson to SAP return inspection (Staging)"))
          {
              bse.launchBrowser();
          }
    }

    @After
    public void TearDownTest(Scenario scenario) throws IOException, InvalidFormatException {

        if (scenarioName.equalsIgnoreCase("Oxid to SAP e2e testing (Staging)"))
        {
            final String InputFile = "src/test/java/outputfiles/OxidToSapStagging_Output.xlsx";
            FileInputStream file = new FileInputStream(new File(InputFile));
            Allure.addAttachment("Test Report: ",file);
            File filelink = new File(InputFile);
            System.out.println();
            System.out.println("Test Report link: "+filelink.getAbsolutePath());
        }
        else if (scenarioName.equalsIgnoreCase("Oxid to Sage e2e testing (Staging)"))
        {
            final String InputFile = "src/test/java/outputfiles/OxidToSageStagging_Output.xlsx";
            FileInputStream file = new FileInputStream(new File(InputFile));
            Allure.addAttachment("Test Report: ",file);
            File filelink = new File(InputFile);
            System.out.println();
            System.out.println("Test Report link: "+filelink.getAbsolutePath());
        }
        else if (scenarioName.equalsIgnoreCase("Plenty Market to SAP e2e testing (Staging)")) {

            final String InputFile = "src/test/java/outputfiles/PlentyToSapStagging_Output.xlsx";
            FileInputStream file = new FileInputStream(new File(InputFile));
            Allure.addAttachment("Test Report: ",file);
            File filelink = new File(InputFile);
            System.out.println();
            System.out.println("Test Report link: "+filelink.getAbsolutePath());

        }

        else if (scenarioName.equalsIgnoreCase("Plenty Market to Sage e2e testing (Staging)"))
        {
            final String InputFile = "src/test/java/outputfiles/PlentyToSageStagging_Output.xlsx";
            FileInputStream file = new FileInputStream(new File(InputFile));
            Allure.addAttachment("Test Report: ",file);
            File filelink = new File(InputFile);
            System.out.println();
            System.out.println("Test Report link: "+filelink.getAbsolutePath());
        }
        else if (scenarioName.equalsIgnoreCase("Wilson to SAP return order (Staging)") || scenarioName.equalsIgnoreCase("Wilson to SAP return inspection (Staging)"))
        {
            bse.quitDriver();
        }

//        if (scenario.isFailed()){
//        //Take screenshot
//        base.driver.close();
//        base.driver.quit();
    }
}

