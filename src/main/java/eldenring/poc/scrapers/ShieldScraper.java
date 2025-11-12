package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.ShieldBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShieldScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(ShieldScraper.class.getName());
    private static final String SHIELD_PAGE_URL = ApiConfig.getBaseUrl() + "/Shields";

    public ShieldScraper() {
        super(LOGGER);
    }

    public List<ShieldBase> scrapeShields() {
        List<ShieldBase> shields = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting shield scraping from: " + SHIELD_PAGE_URL);

            driver = createWebDriver();
            driver.get(SHIELD_PAGE_URL);

            Thread.sleep(1000);

            List<WebElement> rows = driver.findElements(By.cssSelector("div.row"));

            for (WebElement row : rows) {
                List<WebElement> cols = row.findElements(By.cssSelector("div.col-xs-6.col-sm-2"));

                for (WebElement col : cols) {
                    try {
                        String title = "";
                        String imgSrc = null;

                        WebElement aElement = col.findElement(By.tagName("a"));
                        if (aElement != null) {
                            title = aElement.getText().trim();
                        }

                        WebElement imgElement = col.findElement(By.tagName("img"));
                        if (imgElement != null) {
                            if (title.isEmpty()) {
                                String imgTitle = imgElement.getAttribute("title");
                                if (imgTitle != null && !imgTitle.isEmpty()) {
                                    title = imgTitle.trim();
                                }
                            }

                            imgSrc = imgElement.getAttribute("data-src");
                            if (imgSrc == null || imgSrc.isEmpty()) {
                                imgSrc = imgElement.getAttribute("src");
                            }
                        }

                        String fullImageUrl = imgSrc;
                        if (imgSrc != null && imgSrc.startsWith("/")) {
                            fullImageUrl = ApiConfig.getBaseUrl() + imgSrc;
                        }

                        if (!title.isEmpty() && fullImageUrl != null && !fullImageUrl.isEmpty()) {
                            ShieldBase shield = new ShieldBase(title, fullImageUrl);
                            shields.add(shield);
                            logger.fine("Scraped shield: " + title + " with image: " + fullImageUrl);
                        }

                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to extract shield from col element", e);
                    }
                }
            }

            logger.info("Successfully scraped " + shields.size() + " shield items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape shields", e);
        } finally {
            quitDriver(driver);
        }

        return shields;
    }
}

