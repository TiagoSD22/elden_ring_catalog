package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.AmmoBase;
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
 * Web scraper for extracting ammo information from Fextralife wiki.
 * Scrapes the "Arrows and Bolts" gallery page.
 */
public class AmmoScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(AmmoScraper.class.getName());
    private static final String AMMO_PAGE_URL = ApiConfig.getBaseUrl() + "/Arrows+and+Bolts";

    public AmmoScraper() {
        super(LOGGER);
    }

    /**
     * Scrapes all ammo items from the Fextralife wiki gallery.
     *
     * @return List of AmmoBase objects containing name and image URL
     */
    public List<AmmoBase> scrapeAmmos() {
        List<AmmoBase> ammos = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting ammo scraping from: " + AMMO_PAGE_URL);

            driver = createWebDriver();
            driver.get(AMMO_PAGE_URL);

            // Wait for page to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Find and click the "Arrow and Bolt Gallery" tab
            WebElement galleryTab = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'tabtitle') and contains(., 'Arrow and Bolt Gallery')]")
            ));

            LOGGER.info("Found gallery tab, clicking...");
            galleryTab.click();

            // Wait for tab content to appear
            Thread.sleep(1000); // Small delay to ensure content is visible

            // Find the tab content (class contains both "tabcontent" and "1-tab")
            WebElement tabContent = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'tabcontent') and contains(@class, '1-tab')]")
            ));

            logger.info("Tab content loaded, extracting items...");

            // Find all gallery rows
            List<WebElement> galleryRows = tabContent.findElements(By.cssSelector("div.row"));

            for (WebElement row : galleryRows) {
                // Find all wiki_link elements within the row, regardless of nesting structure
                List<WebElement> wikiLinks = row.findElements(By.cssSelector("[class*='wiki_link']"));

                for (WebElement wikiLink : wikiLinks) {
                    try {
                        // Extract image from wiki_link element
                        WebElement imgElement = wikiLink.findElement(By.tagName("img"));

                        String imgSrc = imgElement.getAttribute("data-src");
                        if (imgSrc == null || imgSrc.isEmpty()) {
                            imgSrc = imgElement.getAttribute("src");
                        }

                        // Make sure image URL is absolute
                        String fullImageUrl = imgSrc;
                        if (imgSrc != null && imgSrc.startsWith("/")) {
                            fullImageUrl = ApiConfig.getBaseUrl() + imgSrc;
                        }

                        // Extract name from wiki_link text content
                        String rawText = wikiLink.getText();

                        // Clean up the name - remove any extra whitespace and line breaks
                        String name = rawText.replaceAll("\\s+", " ").trim();

                        if (name != null && !name.isEmpty() && fullImageUrl != null && !fullImageUrl.isEmpty()) {
                            AmmoBase ammo = new AmmoBase(name, fullImageUrl);
                            ammos.add(ammo);
                            logger.fine("Scraped ammo: " + name + " with image: " + fullImageUrl);
                        }

                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to extract ammo from wiki_link element", e);
                    }
                }
            }

            logger.info("Successfully scraped " + ammos.size() + " ammo items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape ammos", e);
        } finally {
            quitDriver(driver);
        }

        return ammos;
    }
}

