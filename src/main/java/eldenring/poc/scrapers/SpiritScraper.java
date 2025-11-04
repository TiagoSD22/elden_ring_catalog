package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.SpiritBase;
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

public class SpiritScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(SpiritScraper.class.getName());
    private static final String SPIRIT_PAGE_URL = ApiConfig.getBaseUrl() + "/Spirit+Ashes";

    public SpiritScraper() {
        super(LOGGER);
    }

    public List<SpiritBase> scrapeSpirits() {
        List<SpiritBase> spirits = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting spirit scraping from: " + SPIRIT_PAGE_URL);

            driver = createWebDriver();
            driver.get(SPIRIT_PAGE_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement galleryTab = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(text(), 'Spirits Gallery')]")
            ));

            LOGGER.info("Found 'Spirits Gallery' tab, clicking...");
            galleryTab.click();

            Thread.sleep(1000);

            List<WebElement> tabContents = driver.findElements(By.cssSelector("div[class*='tabcontent']"));

            for (WebElement tabContent : tabContents) {
                List<WebElement> rows = tabContent.findElements(By.cssSelector("div[class*='row']"));

                for (WebElement row : rows) {
                    List<WebElement> cols = row.findElements(By.cssSelector("div[class*='col']"));

                    for (WebElement col : cols) {
                        try {
                            String title = col.getText().trim();

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
                                SpiritBase spirit = new SpiritBase(title, fullImageUrl);
                                spirits.add(spirit);
                                logger.fine("Scraped spirit: " + title + " with image: " + fullImageUrl);
                            }

                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Failed to extract spirit from col element", e);
                        }
                    }
                }
            }

            logger.info("Successfully scraped " + spirits.size() + " spirit items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape spirits", e);
        } finally {
            quitDriver(driver);
        }

        return spirits;
    }
}

