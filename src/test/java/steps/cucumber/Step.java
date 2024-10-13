package steps.cucumber;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.yaml.snakeyaml.Yaml;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Step {
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    public static void setUp() throws MalformedURLException {
        if (driver == null) {
            System.out.println("SETUP BROWSER");
            String browser = System.getenv("BROWSER");
            String hubHost = System.getenv("HUB_HOST");
            String hubPort = System.getenv("HUB_PORT");
            String hubUrl = (hubPort != null && !hubPort.isEmpty()) ? hubHost + ":" + hubPort : hubHost;
            System.out.println("HUB URL: " + hubUrl);
            AbstractDriverOptions<?> options;
            if (browser == null) {
                browser = "firefox-local";
            }
            if (browser.equalsIgnoreCase("chrome")) {
                options = new ChromeOptions();
                driver = new RemoteWebDriver(new URL("http://" + hubUrl + "/wd/hub"), options);
            } else if (browser.equalsIgnoreCase("firefox")) {
                options = new FirefoxOptions();
                driver = new RemoteWebDriver(new URL("http://" + hubUrl + "/wd/hub"), options);
            } else if (browser.startsWith("browserstack")) {
                Map<String, Object> browserstackConfig = loadBrowserStackConfig();
                String browserstackUser = (String) browserstackConfig.get("userName");
                String browserstackKey = (String) browserstackConfig.get("accessKey");
                String browserstackBuild = (String) browserstackConfig.get("buildName");
                String browserstackBrowserName = (String) browserstackConfig.get("browserName");
                String browserstackOs = (String) browserstackConfig.get("os");
                if (browserstackBrowserName.equalsIgnoreCase("chrome")) {
                    options = new ChromeOptions();
                } else if (browserstackBrowserName.equalsIgnoreCase("firefox")) {
                    options = new FirefoxOptions();
                } else {
                    throw new IllegalArgumentException("Browser not supported: " + browserstackBrowserName);
                }
                options.setCapability("browserstack.user", browserstackUser);
                options.setCapability("browserstack.key", browserstackKey);
                options.setCapability("build", browserstackBuild);
                options.setCapability("browserName", browserstackBrowserName);
                options.setCapability("os", browserstackOs);
                driver = new RemoteWebDriver(new URL("http://" + hubUrl + "/wd/hub"), options);
            } else if (browser.equalsIgnoreCase("chrome-local")) {
                WebDriverManager.chromedriver().clearDriverCache().setup();
                driver = new ChromeDriver();
            } else if (browser.equalsIgnoreCase("firefox-local")) {
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
            } else {
                throw new IllegalArgumentException("Browser not supported: " + browser);
            }
            driver.manage().window().maximize();
            wait = new WebDriverWait(driver, 10);
            System.out.println("SETUP BROWSER DONE!");
        }
    }

    private static Map<String, Object> loadBrowserStackConfig() {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream("browserstack.yml")) {
            return yaml.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load BrowserStack configuration", e);
        }
    }


    public static void tearDown() {
        if (driver != null) {
            System.out.println("TEARING DOWN BROWSER");
            driver.quit();
            driver= null;
        }
    }
}
