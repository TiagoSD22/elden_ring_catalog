package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.TalismanBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TalismanScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(TalismanScraper.class.getName());
    private static final String TALISMAN_PAGE_URL = ApiConfig.getBaseUrl() + "/Talismans";

    public TalismanScraper() {
        super(LOGGER);
    }

    public List<TalismanBase> scrapeTalismans() {
        List<TalismanBase> talismans = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting talisman scraping from: " + TALISMAN_PAGE_URL);

            driver = createWebDriver();
            driver.get(TALISMAN_PAGE_URL);

            Thread.sleep(1000);

            List<WebElement> tabContents = driver.findElements(By.cssSelector("div[class*='tabcontent']"));

            for (WebElement tabContent : tabContents) {
                List<WebElement> rows = tabContent.findElements(By.cssSelector("div[class*='row']"));

                for (WebElement row : rows) {
                    List<WebElement> cols = row.findElements(By.cssSelector("div[class*='col']"));

                    for (WebElement col : cols) {
                        try {
                            WebElement tooltipElement = col.findElement(By.cssSelector("a.wiki_tooltip"));
                            String title = tooltipElement.getText().trim();

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
                                TalismanBase talisman = new TalismanBase(title, fullImageUrl);
                                talismans.add(talisman);
                                logger.fine("Scraped talisman: " + title + " with image: " + fullImageUrl);
                            }

                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Failed to extract talisman from col element", e);
                        }
                    }
                }
            }

            logger.info("Successfully scraped " + talismans.size() + " talisman items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape talismans", e);
        } finally {
            quitDriver(driver);
        }

        return talismans;
    }
}

