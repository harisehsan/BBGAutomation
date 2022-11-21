package stepdefinitions;

import Pages.*;
import PerformanceTesting.OxidToSageStaggingPerformance;
import PerformanceTesting.OxidToSapStaggingPerformance;
import base.BaseUtil;
import cucumber.api.java.en.Given;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.simple.parser.ParseException;
import org.testng.Assert;


import java.awt.*;
import java.io.IOException;

public class BbgMiddlewareSteps {

    private BaseUtil base;
    OxidToSapStaggingPage oxidtosap;
    OxidToSageStaggingPage oxidToSageStaggingPage;
    PleantyToSapStaggingPage pleantyToSapStaggingPage;
    PlentyToSageStaggingPage plentyToSageStaggingPage;
    OxidToSapStaggingPerformance oxidToSapStaggingPerformance;
    OxidToSageStaggingPerformance oxidToSageStaggingPerformance;
    CommercetoolsToSapPage commercetoolsToSapPage;
    SkOrderToSageStagePage skOrderToSageStagePage;
    WilsonReturnOrder wilsonReturnOrder;
    WilsonReturnInspectionPage wilsonReturnInspectionPage;
    PlentyMarketMWtoSAPPage plentyMarketMWtoSAPPage;
    PleantyToSapStaggingPage2 pleantyToSapStaggingPage2;

    public BbgMiddlewareSteps(BaseUtil base) throws AWTException, IOException {
        this.base = base;
        oxidtosap = new OxidToSapStaggingPage(base.driver);
        oxidToSageStaggingPage = new OxidToSageStaggingPage(base.driver);
        pleantyToSapStaggingPage = new PleantyToSapStaggingPage(base.driver);
        plentyToSageStaggingPage = new PlentyToSageStaggingPage(base.driver);
        oxidToSapStaggingPerformance = new OxidToSapStaggingPerformance(base.driver);
        oxidToSageStaggingPerformance = new OxidToSageStaggingPerformance(base.driver);
        commercetoolsToSapPage = new CommercetoolsToSapPage(base.driver);
        skOrderToSageStagePage = new SkOrderToSageStagePage(base.driver);
        wilsonReturnOrder = new WilsonReturnOrder(base.driver);
        wilsonReturnInspectionPage = new WilsonReturnInspectionPage(base.driver);
        plentyMarketMWtoSAPPage = new PlentyMarketMWtoSAPPage(base.driver);
        pleantyToSapStaggingPage2 = new PleantyToSapStaggingPage2(base.driver);
    }

    @Given("I perform the end-to-end testing of oxid to SAP")
    public void iVerifyTheEndToEndTestingOfOxidToSAPStagging() throws IOException, ParseException, InterruptedException {
         oxidtosap.oxidtoSAPTestCall();
//        oxidToSapStaggingPerformance.oxidtoSAPTestCall();

    }

    @Given("I perform the end-to-end testing of oxid to Sage")
    public void iVerifyTheEndToEndTestingOfOxidToSageStagging() throws IOException, ParseException, InterruptedException {
        oxidToSageStaggingPage.oxidtoSageTestCall();
//        oxidToSageStaggingPerformance.oxidtoSageTestCall();
        plentyMarketMWtoSAPPage.jsonReader();
    }

    @Given("I perform the end-to-end testing of Plenty Market to SAP")
    public void iVerifyTheEndToEndTestingOfPlentyMarketToSAPStagging() throws IOException, ParseException {
        pleantyToSapStaggingPage2.PlentyToSapTestCall();
     //   plentyMarketMWtoSAPPage.PlentyToSapTestCall();


    }

    @Given("I perform the end-to-end testing of Plenty Market to Sage")
    public void iVerifyTheEndToEndTestingOfPlentyMarketToSageStagging() throws IOException, ParseException, InterruptedException {
        plentyToSageStaggingPage.PlentyToSageTestCall();

    }

    @Then("I verify no issue occurred in all oxid to SAP end to end processes")
    public void iVerifyNoIssueoccurredInAllOxidToSAPEndToEndProcesses() {
       Assert.assertTrue(oxidtosap.verifyTheScenario(),"One or more scenario(s) is/are failed");
    }

    @Then("I verify no issue occurred in all oxid to sage end to end processes")
    public void iVerifyNoIssueoccurredInAllOxidToSageEndToEndProcesses() {
        Assert.assertTrue(oxidToSageStaggingPage.verifyTheScenario(),"One or more scenario(s) is/are failed");
    }

    @Then("I verify no issue occurred in all plenty to Sap end to end processes")
    public void iVerifyNoIssueoccurredInAllPlentyToSapEndToEndProcesses() {
        Assert.assertTrue(pleantyToSapStaggingPage.verifyTheScenario(),"One or more scenario(s) is/are failed");
     //   Assert.assertTrue(plentyMarketMWtoSAPPage.verifyTheScenario(),"One or more scenario(s) is/are failed");
    }

    @Then("I verify no issue occurred in all plenty to Sage end to end processes")
    public void iVerifyNoIssueoccurredInAllPlentyToSageEndToEndProcesses() {
        Assert.assertTrue(plentyToSageStaggingPage.verifyTheScenario(),"One or more scenario(s) is/are failed");
    }

    @Given("I performed end to end testing commerce tools to Sap")
    public void iPerformedEndToEndTestingCommerceToolsToSap() {
        commercetoolsToSapPage.executEventGrid();
    }

    @Given("I perform the end-to-end testing of skOrders to Sage")
    public void iPerformTheEndToEndTestingOfSkOrdersToSage() throws IOException, ParseException {
        skOrderToSageStagePage.skOrdertoMiddlewareTestExecutor();
    }

    @Then("I verify no issue occurred in all skOrders to Sage end to end processes")
    public void iVerifyNoIssueOccurredInAllSkOrdersToSageEndToEndProcesses() {
       Assert.assertTrue(skOrderToSageStagePage.verifyTheScenario(),"One or more scenario(s) is/are failed");
    }

    @Given("I use the api call to send the return order to SAP")
    public void iUseTheApiCallToSendTheReturnToSAP() throws IOException, InterruptedException, ParseException {
        wilsonReturnOrder.executor();
    }

    @Then("I should see the cifNo in new relic for return order")
    public void iShouldSeeTheCifNoInNewRelic() {
      Assert.assertTrue(wilsonReturnOrder.scenarioStatus(),"One or more scenario(s) is/are failed");
    }

    @Given("I use the api call to send the return inspection to SAP")
    public void iUseTheApiCallToSendTheReturnInspectionToSAP() throws IOException, ParseException, InterruptedException {
        wilsonReturnInspectionPage.executor();
    }

    @Then("I should see the cifNo in new relic for return inspection")
    public void iShouldSeeTheCifNoInNewRelicForReturnInspection() {
        Assert.assertTrue(wilsonReturnInspectionPage.scenarioStatus(),"One or more scenario(s) is/are failed");
    }
}
