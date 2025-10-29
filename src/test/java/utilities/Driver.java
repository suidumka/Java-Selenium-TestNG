package utilities;

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


        private Driver() {
        }

        private static InheritableThreadLocal <WebDriver> driverPool = new InheritableThreadLocal<>();

        static {
            Logger root = Logger.getLogger("");
            root.setLevel(Level.SEVERE);
            Arrays.stream(root.getHandlers()).forEach(h -> h.setLevel(Level.SEVERE));

            Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
            Logger.getLogger("org.openqa.selenium.devtools").setLevel(Level.SEVERE);
            Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder").setLevel(Level.SEVERE);
        }



    public static WebDriver get() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("autofill.profile_enabled", false);
        prefs.put("autofill.credit_card_enabled", false);

        String browserType = ConfigReader.getProperty("browser");

        ChromeOptions options = new ChromeOptions();

        options.setExperimentalOption("prefs", prefs);
        options.addArguments(
                "--disable-features=PasswordLeakDetection,PasswordManagerOnboarding"
        );
        options.addArguments("--disable-features=HttpsFirstMode,HttpsFirstModeV2");
        if (driverPool.get() == null) {
            switch (browserType.toLowerCase()) {
                case "chrome" -> {
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    driverPool.set(new ChromeDriver(options));
                }
                case "firefox" -> driverPool.set(new FirefoxDriver());
                case "safari" -> driverPool.set(new SafariDriver());
                case "headless" -> {
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    options.addArguments("--headless");
                    driverPool.set(new ChromeDriver(options));
                }
            }
            //assert driverPool != null;
            driverPool.get().manage().window().maximize();
        }
        return driverPool.get();
    }

    public static void closeDriver() {
        if (driverPool.get() != null) {
            driverPool.get().quit();
            driverPool.remove();
        }
    }
}