package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.Driver;

import java.time.Duration;
import java.util.*;


public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    public WebElement userNameField;

    @FindBy(id = "password")
    public WebElement passwordField;

    @FindBy(id = "login-button")
    public WebElement loginButton;

    @FindBy(id = "react-burger-menu-btn")
    public WebElement burgerMenuBtn;

    @FindBy(id = "logout_sidebar_link")
    public WebElement logoutButton;

    @FindBy(xpath = "//input[@value='Login']")
    public WebElement loginText;


    Map<String, String> credentials;

    public Map<String, String> getCredentialsFromPage() {

        WebElement usernames = driver.findElement(By.xpath("//div[@class='login_credentials']"));
        String userName = usernames.getText();

        WebElement password = driver.findElement(By.xpath("//div[@class='login_password']"));
        String passwordStr = password.getText();

        userName = userName.substring(userName.indexOf(":") + 1);
        passwordStr = passwordStr.substring(passwordStr.indexOf(":") + 1);


        List<String> users = new ArrayList<>(Arrays.asList(userName.split("\n")));
        //System.out.println(users.size());
        users.remove(0);
        credentials = new HashMap<>();

        for (String user : users) {
            credentials.put(user, passwordStr);
        }
        System.out.println(credentials);

        return credentials;

    }

    public void login(String username, String password) {
        userNameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
    }

    public void logout() {
        WebDriverWait wait = new WebDriverWait(Driver.get(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(burgerMenuBtn));

        burgerMenuBtn.click();

        wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();

    }
}





















