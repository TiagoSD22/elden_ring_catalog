package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.AshesOfWarBase;
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

/**
 * Web scraper for extracting Ashes of War information from Fextralife wiki.
 * Scrapes the Ashes of War page.
 */
public class AshesOfWarScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(AshesOfWarScraper.class.getName());
    private static final String ASHES_OF_WAR_PAGE_URL = ApiConfig.getBaseUrl() + "/Ashes+of+War";

    public AshesOfWarScraper() {
        super(LOGGER);
    }

    /**
     * Scrapes all Ashes of War items from the Fextralife wiki.
     *
     * @return List of AshesOfWarBase objects containing title and image URL
     */
    public List<AshesOfWarBase> scrapeAshesOfWar() {
        List<AshesOfWarBase> ashesOfWar = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting Ashes of War scraping from: " + ASHES_OF_WAR_PAGE_URL);

            driver = createWebDriver();
            driver.get(ASHES_OF_WAR_PAGE_URL);

            // Wait for page to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Find and click the "Ashes of War Gallery" tab button
            logger.info("Looking for 'Ashes of War Gallery' tab...");
            WebElement tabButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class, 'tabtitle') and contains(., 'Ashes of War Gallery')]")
            ));
            tabButton.click();
            logger.info("Clicked 'Ashes of War Gallery' tab");

            // Wait for the tab content to be visible
            Thread.sleep(1000); // Give time for the tab content to render

            // Find the tab content div
            WebElement tabContent = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class, 'tabcontent') and contains(@class, '1-tab')]")
            ));

            logger.info("Found tab content, extracting Ashes of War items...");

            // Find all gallery rows within the tab content
            List<WebElement> rows = tabContent.findElements(By.cssSelector("div[class*='row'][class*='gallery']"));

            for (WebElement row : rows) {
                // Find all col divs within each row
                List<WebElement> cols = row.findElements(By.cssSelector("div[class*='col']"));

                for (WebElement col : cols) {
                    try {
                        // Find h4 element containing the title
                        WebElement h4Element = col.findElement(By.tagName("h4"));
                        String title = h4Element.getText().trim();

                        // Find img element containing the image URL
                        WebElement imgElement = col.findElement(By.tagName("img"));
                        String imgSrc = imgElement.getAttribute("data-src");
                        if (imgSrc == null || imgSrc.isEmpty()) {
                            imgSrc = imgElement.getAttribute("src");
                        }

                        // Make sure image URL is absolute
                        String fullImageUrl = imgSrc;
                        if (imgSrc != null && imgSrc.startsWith("/")) {
                            fullImageUrl = ApiConfig.getBaseUrl() + imgSrc;
                        }

                        if (title != null && !title.isEmpty() && fullImageUrl != null && !fullImageUrl.isEmpty()) {
                            AshesOfWarBase ashOfWar = new AshesOfWarBase(title, fullImageUrl);
                            ashesOfWar.add(ashOfWar);
                            logger.fine("Scraped Ash of War: " + title + " with image: " + fullImageUrl);
                        }

                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to extract Ash of War from col element", e);
                    }
                }
            }

            logger.info("Successfully scraped " + ashesOfWar.size() + " Ashes of War items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape Ashes of War", e);
        } finally {
            quitDriver(driver);
        }

        return ashesOfWar;
    }
}

