package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.ClassBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(ClassScraper.class.getName());
    private static final String CLASS_PAGE_URL = ApiConfig.getBaseUrl() + "/Classes";

    public ClassScraper() {
        super(LOGGER);
    }

    public List<ClassBase> scrapeClasses() {
        List<ClassBase> classes = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting class scraping from: " + CLASS_PAGE_URL);

            driver = createWebDriver();
            driver.get(CLASS_PAGE_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.row")));

            Thread.sleep(1000);

            List<WebElement> rows = driver.findElements(By.cssSelector("div.row"));

            for (WebElement row : rows) {
                List<WebElement> cols = row.findElements(By.cssSelector("div.col-sm-3"));

                for (WebElement col : cols) {
                    try {
                        WebElement h3Element = col.findElement(By.tagName("h3"));
                        String title = h3Element.getText().trim();

                        WebElement imgElement = col.findElement(By.tagName("img"));
                        String imgSrc = imgElement.getAttribute("data-src");
                        if (imgSrc == null || imgSrc.isEmpty()) {
                            imgSrc = imgElement.getAttribute("src");
                        }

                        String fullImageUrl = imgSrc;
                        if (imgSrc != null && imgSrc.startsWith("/")) {
                            fullImageUrl = ApiConfig.getBaseUrl() + imgSrc;
                        }

                        if (title != null && !title.isEmpty() && fullImageUrl != null && !fullImageUrl.isEmpty()) {
                            ClassBase classItem = new ClassBase(title, fullImageUrl);
                            classes.add(classItem);
                            logger.fine("Scraped class: " + title + " with image: " + fullImageUrl);
                        }

                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to extract class from col element", e);
                    }
                }
            }

            logger.info("Successfully scraped " + classes.size() + " class items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape classes", e);
        } finally {
            quitDriver(driver);
        }

        return classes;
    }
}

