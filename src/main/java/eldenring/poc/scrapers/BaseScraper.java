package eldenring.poc.scrapers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.logging.Logger;

/**
 * Abstract base scraper with common Selenium configuration.
 * Provides headless Chrome WebDriver setup.
 */
public abstract class BaseScraper {
    protected final Logger logger;

    protected BaseScraper(Logger logger) {
        this.logger = logger;
    }

    /**
     * Creates a configured headless Chrome WebDriver instance.
     *
     * @return Configured WebDriver instance
     */
    protected WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        return new ChromeDriver(options);
    }

    /**
     * Safely quits the WebDriver if it's not null.
     *
     * @param driver WebDriver instance to quit
     */
    protected void quitDriver(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }
}

