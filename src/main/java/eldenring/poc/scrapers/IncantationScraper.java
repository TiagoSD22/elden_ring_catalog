package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.IncantationBase;
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

public class IncantationScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(IncantationScraper.class.getName());
    private static final String INCANTATION_PAGE_URL = ApiConfig.getBaseUrl() + "/Incantations";

    public IncantationScraper() {
        super(LOGGER);
    }

    public List<IncantationBase> scrapeIncantations() {
        List<IncantationBase> incantations = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting incantation scraping from: " + INCANTATION_PAGE_URL);

            driver = createWebDriver();
            driver.get(INCANTATION_PAGE_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement typeTab = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(text(), 'Incantations by Type')]")
            ));

            LOGGER.info("Found 'Incantations by Type' tab, clicking...");
            typeTab.click();

            Thread.sleep(1000);

            List<WebElement> wikiTables = driver.findElements(By.cssSelector("div[class*='tabcontent 2-tab']"));

            for (WebElement wikiTable : wikiTables) {
                List<WebElement> rows = wikiTable.findElements(By.cssSelector("div[class*='row']"));

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
                                IncantationBase incantation = new IncantationBase(title, fullImageUrl);
                                incantations.add(incantation);
                                logger.fine("Scraped incantation: " + title + " with image: " + fullImageUrl);
                            }

                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Failed to extract incantation from col element", e);
                        }
                    }
                }
            }

            logger.info("Successfully scraped " + incantations.size() + " incantation items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape incantations", e);
        } finally {
            quitDriver(driver);
        }

        return incantations;
    }
}

