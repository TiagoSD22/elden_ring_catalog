package eldenring.poc.services;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.ArmorBase;
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
 * Web scraper for extracting armor information from Fextralife wiki.
 * Scrapes the Armor page.
 */
public class ArmorScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(ArmorScraper.class.getName());
    private static final String ARMOR_PAGE_URL = ApiConfig.getBaseUrl() + "/Armor";

    public ArmorScraper() {
        super(LOGGER);
    }

    /**
     * Scrapes all armor items from the Fextralife wiki.
     *
     * @return List of ArmorBase objects containing title and image URL
     */
    public List<ArmorBase> scrapeArmors() {
        List<ArmorBase> armors = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting armor scraping from: " + ARMOR_PAGE_URL);

            driver = createWebDriver();
            driver.get(ARMOR_PAGE_URL);

            // Wait for page to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Find the main content area: div#sub-main > div#wiki-content-block > div.col-sm-9
            WebElement subMain = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("sub-main")));
            WebElement wikiContentBlock = subMain.findElement(By.id("wiki-content-block"));
            WebElement mainContent = wikiContentBlock.findElement(By.cssSelector("div[class*='col-sm-9']"));

            logger.info("Found main content area, extracting armor items...");

            // Find all rows within the main content
            List<WebElement> rows = mainContent.findElements(By.cssSelector("div[class*='row']"));

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
                            ArmorBase armor = new ArmorBase(title, fullImageUrl);
                            armors.add(armor);
                            logger.fine("Scraped armor: " + title + " with image: " + fullImageUrl);
                        }

                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to extract armor from col element", e);
                    }
                }
            }

            logger.info("Successfully scraped " + armors.size() + " armor items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape armors", e);
        } finally {
            quitDriver(driver);
        }

        return armors;
    }
}

