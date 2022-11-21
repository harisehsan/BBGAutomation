package runConfig;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/resources/features/GherkinFiles",
		tags="@scenario1_stage",
		glue = {"stepdefinitions"},
		plugin = {
				"io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm",
				"pretty",
				"html:target/cucumber-reports/cucumber-pretty",
				"json:target/cucumber-reports/json-reports/CucumberTestReport.json",
				"rerun:target/cucumber-reports/rerun-reports/rerun.txt"
		}

)
public class RunTest {

}
