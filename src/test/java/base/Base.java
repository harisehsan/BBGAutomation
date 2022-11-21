package base;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Base extends BaseUtil {

//    public WebDriver driver;
    private static final int DEFAULT_TIMEOUT = 120;
    String dvr;

    public Base(WebDriver driver) {

    }

    protected void waitUntilPageReady(){
        new WebDriverWait(driver, DEFAULT_TIMEOUT).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    protected void hover(WebElement ele){
        Actions action = new Actions(driver);
        action.moveToElement(ele).build().perform();
    }

    protected void waitUntilClickable(By by) {
        waitUntilClickable(by,DEFAULT_TIMEOUT);
    }

    protected void waitUntilClickable(By by, int timeOutInSeconds) {
        new WebDriverWait(driver, timeOutInSeconds)
                .until(ExpectedConditions.elementToBeClickable(by));
    }

    protected void waitUntilVisible(WebElement ele) {
        new WebDriverWait(driver, DEFAULT_TIMEOUT).until(ExpectedConditions.visibilityOf(ele));
    }

    protected void goToPage(String url)
    {
        driver.navigate().to(url);
    }

    protected void scrollToView(WebElement ele) throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", ele);
        Thread.sleep(3000);
    }

    protected boolean booleanwaitUntilPresentOfElementBy(By by, int timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.presenceOfElementLocated(by));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void HandleBrowserAuthentication() throws IOException {
       Runtime.getRuntime().exec("src\\HandleAuthticationAlert.exe");
    }

    protected int randomOrderNumberGenerate()
    {
        int number = 0;
        Random rand = new Random();
        do {
            number = rand.nextInt(999999);
        } while (number < 100000 );
        return number;
    }

    public void launchBrowser()
    {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\m.ehsan\\IdeaProjects\\BBGAutomation\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    public WebDriver getDriver()
    {

        return driver;
    }

    public boolean elementExist(List<WebElement> ele)
    {
         return (ele.size() > 0);
    }

    public void quitDriver()
    {
        driver.quit();
    }

    public String getTwelveDigitsAlphaNumericCode(int length)
    {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public String getRandomNumeric(int length)
    {
        return RandomStringUtils.randomNumeric(length);
    }


}
