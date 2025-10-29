package tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import utilities.ConfigReader;
import utilities.Driver;

import java.util.HashMap;
import java.util.Map;


public class LoginTest {

    Map <String, String> credentials = new HashMap<>();
    LoginPage loginPage;


    @BeforeMethod
    public void setUp() {
        Driver.get().get(ConfigReader.getProperty("url"));
        credentials = new LoginPage().getCredentialsFromPage();
    }

    @AfterMethod
    public  void tearDown() {

       Driver.get().quit();
    }

    @Test
    public void loginTest(){
        loginPage = new LoginPage();
        loginPage.getCredentialsFromPage();

        for (Map.Entry <String, String> each : credentials.entrySet()){
            String userName = each.getKey();
            String password = each.getValue();
            if (userName.equals("locked_out_user")){
                continue;
            }
            loginPage.login(userName, password);
            String expectedTitle = "Swag Labs";
            String actual = Driver.get().getTitle();
            Assert.assertEquals(expectedTitle, actual);
            loginPage.logout();
        }
        Assert.assertTrue(loginPage.loginText.isDisplayed());







    }



}









