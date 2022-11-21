package pageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class WilsonReturnPageObject {

    @FindBy(id ="login_email") public WebElement login_txtBox;
    @FindBy(id="login_submit") public WebElement next_btn;
    @FindBy(id="login_password") public WebElement password_txtBox;
    @FindBy(id="login_submit") public WebElement login_btn;
  //  @FindBy(xpath="/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[6]/div[1]/section[1]/div[1]/div[1]/div[1]/div[1]/div[1]/section[1]/div[1]/span[1]/div[1]/input[1]") public List <WebElement> logs_search_bar;
    @FindBy(className="searchbar-input") public WebElement logs_search_bar;
    @FindBy (className= "search-button") public WebElement search_btn;
 //  @FindBy (xpath = "//*[@id=\"root\"]/div/div/div/div[6]/div/section/div/div/div/div[1]/div[2]/section/div/div[3]/div[1]/div/div/div[1]/div/div[2]/div/div[9]/div[3]/div/div") public List <WebElement> cifNo_log;
    @FindBy (css = ".ReactVirtualized__Table__row:nth-child(9) span:nth-child(3)") public List <WebElement> cifNo_log;
    @FindBy (xpath = "//span[normalize-space()='Add Column']") public WebElement search_attributes_link;
    @FindBy(xpath = "//div[2]/div/input") public WebElement attribute_search_txtBox;
    @FindBy (css=".response\\.CIFNR .clickable-attribute") public WebElement sapCifNo_link;
    @FindBy(xpath = "//li") public WebElement attribute_Element_link;
    @FindBy(xpath = "//*[@id=\"root\"]/div/div/div/div[6]/div/section/div/div/div/div[1]/div[1]/section/div/span/div/span[2]/button/span[1]") public List<WebElement> search_Clear_btn;

    public By next_btn_By = By.id("login_submit");
    public By attribute_search_By = By.xpath("//div[2]/div/input");
    public By password_txtBox_By = By.id("login_password");
    public By login_btn_By = By.id("login_submit");
    public By logs_search_bar_by = By.id("downshift-0-input");
    public By search_btn_by = By.className("search-button");
    public By cifNo_log_By = By.cssSelector(".ReactVirtualized__Table__row:nth-child(9) span:nth-child(3)");
    public By sapCifNo_By = By.cssSelector(".response\\.CIFNR .clickable-attribute");
    public By attribute_Element_link_By = By.xpath("//li");
}
