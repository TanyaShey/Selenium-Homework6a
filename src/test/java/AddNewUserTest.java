import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.UUID;


public class AddNewUserTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://auto.pragmatic.bg/manage/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testAddNewUser() {
        //Login to admin panel
        WebElement username = driver.findElement(By.id("input-username"));
        username.sendKeys("admin");
        WebElement password = driver.findElement(By.id("input-password"));
        password.sendKeys("parola123!");
        WebElement loginButton = driver.findElement(By.cssSelector("#form-login > div.text-end > button"));
        loginButton.click();

        //Navigate to menu Customers -> Customers
        WebElement customersButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#menu-customer > a")));
        customersButton.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='menu-customer']//li[1]/a")));
        
        WebElement customersDropdown = driver.findElement(By.xpath("//*[@id='menu-customer']//li[1]/a"));
        customersDropdown.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//i[@class='fa-solid fa-plus']/ ..")));

        //Add new customer
        WebElement plusButton = driver.findElement(By.xpath("//i[@class='fa-solid fa-plus']/ .."));
        plusButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-firstname")));

        WebElement firstnameInputField = driver.findElement(By.id("input-firstname"));
        firstnameInputField.sendKeys("Tanya");

        WebElement lastnameInputField = driver.findElement(By.id("input-lastname"));
        lastnameInputField.sendKeys("Sheytanova");

//        String randomPrefix = RandomStringUtils.randomAlphabetic(5);
//        String randomDomain = RandomStringUtils.randomAlphabetic(4);
//        String randomEmail = "tanya_" + randomPrefix +"@" + randomDomain + ".com";

        String randomEmail = "tanya_" + UUID.randomUUID().toString().substring(0, 5) + "@test.com";
        System.out.println(randomEmail);
        WebElement emailInputField = driver.findElement(By.id("input-email"));
        emailInputField.sendKeys(randomEmail);

        WebElement newPassword = driver.findElement(By.id("input-password"));
        newPassword.sendKeys("password123@");
        WebElement confirmPassword = driver.findElement(By.id("input-confirm"));
        confirmPassword.sendKeys("password123@");

        WebElement buttonSave = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//i[@class='fa-solid fa-floppy-disk']/..")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", buttonSave);
        buttonSave.click();

        //Assert the customer is added
        WebElement customerSubMenu = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#menu-customer  li.active > a")));
        customerSubMenu.click();
        WebElement filterByEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email")));
        filterByEmail.sendKeys(randomEmail);

        WebElement filterButton = driver.findElement(By.id("button-filter"));
        try {
            filterButton.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            js.executeScript("arguments[0].scrollIntoView()", filterButton);
            wait.until(ExpectedConditions.visibilityOf(filterButton));
            filterButton.click();
        }

        js.executeScript("arguments[0].scrollIntoView()", driver.findElement(By.xpath("//*[@id='form-customer']/div[1]/table/tbody/tr/td[3]")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[@id='form-customer']/div[1]/table/tbody/tr/td[3]"), randomEmail));
        WebElement actualEmail = driver.findElement(By.xpath("//*[@id='form-customer']/div[1]/table/tbody/tr/td[3]"));
        String actualEmailString = actualEmail.getText().trim();
        System.out.println("Actual Email in Table: " + actualEmailString);
        Assert.assertEquals(actualEmailString, randomEmail,  "The filtered email (" + actualEmailString + ") does not match the added email (" + randomEmail + ").");

    }
}
