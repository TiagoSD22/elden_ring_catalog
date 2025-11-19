package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.ItemBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(ItemScraper.class.getName());
    private static final String ITEM_PAGE_URL = ApiConfig.getBaseUrl() + "/Items";

    public ItemScraper() {
        super(LOGGER);
    }

    public List<ItemBase> scrapeItems() {
        List<ItemBase> items = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting item scraping from: " + ITEM_PAGE_URL);

            driver = createWebDriver();
            driver.get(ITEM_PAGE_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement keyItemsTab;
            try {
                keyItemsTab = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(text(), 'Elden Ring Key Items Table')]")
                ));
                logger.info("Found key items tab element: text='" + keyItemsTab.getText().trim() + "'");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Key items tab element NOT found. Aborting scrape.", e);
                return items;
            }

            try {
                logger.info("Clicking key items tab...");
                keyItemsTab.click();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to click key items tab (continuing, some pages don't require click)", e);
            }

            WebElement table;
            try {
                table = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.wiki_table.sortable.searchable")));
                logger.info("Found items table element");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Items table not found after clicking tab. Aborting scrape.", e);
                return items;
            }

            List<WebElement> rows = table.findElements(By.tagName("tr"));
            logger.info("Found " + rows.size() + " table rows (including header rows)");

            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                WebElement row = rows.get(rowIndex);
                String rowHtml = "";
                try {
                    rowHtml = row.getAttribute("outerHTML");
                } catch (Exception ignored) {}
                String snippet = rowHtml == null ? "" : (rowHtml.length() > 200 ? rowHtml.substring(0, 200) + "..." : rowHtml);

                try {
                    WebElement h4 = row.findElement(By.tagName("h4"));
                    String title = h4.getText().trim();

                    WebElement img = row.findElement(By.tagName("img"));
                    String imgSrc = img.getAttribute("data-src");
                    if (imgSrc == null || imgSrc.isEmpty()) {
                        imgSrc = img.getAttribute("src");
                    }

                    if (imgSrc == null || imgSrc.isEmpty()) {
                        logger.fine("Row " + rowIndex + " - image src empty. row snippet=" + snippet);
                        throw new NoSuchElementException("Image src empty for row");
                    }

                    String fullImageUrl = imgSrc;
                    if (imgSrc.startsWith("/")) {
                        fullImageUrl = ApiConfig.getBaseUrl() + imgSrc;
                    }

                    if (title.isEmpty() || fullImageUrl.isEmpty()) {
                        logger.fine("Row " + rowIndex + " - missing title or image. title='" + title + "' image='" + fullImageUrl + "' snippet=" + snippet);
                        continue;
                    }

                    ItemBase item = new ItemBase(title, fullImageUrl);
                    items.add(item);
                    logger.fine("Scraped item (row " + rowIndex + "): title='" + title + "' image='" + fullImageUrl + "'");

                } catch (NoSuchElementException nse) {
                    logger.log(Level.FINER, "Row " + rowIndex + " - required element not found within row. row snippet=" + snippet, nse);
                } catch (StaleElementReferenceException sere) {
                    logger.log(Level.FINER, "Row " + rowIndex + " - stale element reference. Will skip. row snippet=" + snippet, sere);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Row " + rowIndex + " - unexpected failure while parsing. row snippet=" + snippet, e);
                }
            }

            logger.info("Successfully scraped " + items.size() + " items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape items", e);
        } finally {
            quitDriver(driver);
        }

        return items;
    }
}
