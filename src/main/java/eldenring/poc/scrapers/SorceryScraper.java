package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.SorceryBase;
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

public class SorceryScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(SorceryScraper.class.getName());
    private static final String SORCERY_PAGE_URL = ApiConfig.getBaseUrl() + "/Sorceries";

    public SorceryScraper() {
        super(LOGGER);
    }

    public List<SorceryBase> scrapeSorceries() {
        List<SorceryBase> sorceries = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting sorcery scraping from: " + SORCERY_PAGE_URL);

            driver = createWebDriver();
            driver.get(SORCERY_PAGE_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement typeTab = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(text(), 'Sorceries by Type')]")
            ));

            LOGGER.info("Found 'Sorceries by Type' tab, clicking...");
            typeTab.click();

            Thread.sleep(1000);

            List<WebElement> rows = driver.findElements(By.cssSelector("div.row"));

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
                            SorceryBase sorcery = new SorceryBase(title, fullImageUrl);
                            sorceries.add(sorcery);
                            logger.fine("Scraped sorcery: " + title + " with image: " + fullImageUrl);
                        }

                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to extract sorcery from col element", e);
                    }
                }
            }

            logger.info("Successfully scraped " + sorceries.size() + " sorcery items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape sorceries", e);
        } finally {
            quitDriver(driver);
        }

        return sorceries;
    }
}

