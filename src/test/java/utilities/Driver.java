package com.company.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Driver {

    /*
    Creating a private constructor, so we are closing access to the object of this class from outside the class
     */

    private Driver() {
    }

    /*
    Making our driver instance private, so it is not reachable from outside the class
    We make it static because we want it to run before everything else, and we will use it in a static method
     */

    //private static WebDriver driver;
    // implement threadLocal to archive multi thread locally
    private static InheritableThreadLocal <WebDriver> driverPool = new InheritableThreadLocal<>();

    static {
        Logger root = Logger.getLogger("");
        root.setLevel(Level.SEVERE);
        Arrays.stream(root.getHandlers()).forEach(h -> h.setLevel(Level.SEVERE));

        Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
        Logger.getLogger("org.openqa.selenium.devtools").setLevel(Level.SEVERE);
        Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder").setLevel(Level.SEVERE);
    }


    /*
    Creating a reusable method that will return the same driver instance every time when we call it
     */

    /**
     * Singleton pattern
     *
     * @return
     */

    public static WebDriver getDriver() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("autofill.profile_enabled", false);
        prefs.put("autofill.credit_card_enabled", false);

        ChromeOptions options = new ChromeOptions();

        options.setExperimentalOption("prefs", prefs);
        options.addArguments(
                "--disable-features=PasswordLeakDetection,PasswordManagerOnboarding"
        );
        options.addArguments("--disable-features=HttpsFirstMode,HttpsFirstModeV2");
        if (driverPool.get() == null) {

            // Read from -Dbrowser, then BROWSER env, else default to chrome
            String browserType = System.getProperty("browser");
            if (browserType == null || browserType.isBlank()) {
                browserType = System.getenv("BROWSER");
            }
            if (browserType == null || browserType.isBlank()) {
                browserType = "chrome";
            }
            browserType = browserType.trim().toLowerCase();

            switch (browserType) {
                case "chrome" -> {
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    driverPool.set(new ChromeDriver(options));
                }
                case "firefox" -> driverPool.set(new FirefoxDriver());
                case "safari" -> driverPool.set(new SafariDriver());
                case "headless" -> {
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    options.addArguments("--headless"); // kept exactly as you had it
                    driverPool.set(new ChromeDriver(options));
                }
                default -> { // unknown value -> default to chrome
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    driverPool.set(new ChromeDriver(options));
                }
            }
            //assert driverPool != null;
            driverPool.get().manage().window().maximize();
           // driverPool.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(DocuportConstants.LARGE));
            driverPool.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.valueOf(ConfigurationReader.getProperties("timeouts"))));
        }
        return driverPool.get();
    }

    public static void closeDriver() {
        if (driverPool.get() != null) {
            driverPool.get().quit();
            driverPool.remove(); // we assign it back to null so that next time we call getDriver(), a new instance will be created
        }
    }
}
