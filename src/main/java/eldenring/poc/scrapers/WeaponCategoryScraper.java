package eldenring.poc.scrapers;

import eldenring.poc.config.ApiConfig;
import eldenring.poc.models.WeaponCategoryBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeaponCategoryScraper extends BaseScraper {
    private static final Logger LOGGER = Logger.getLogger(WeaponCategoryScraper.class.getName());
    private static final String WEAPON_PAGE_URL = ApiConfig.getBaseUrl() + "/Weapons";

    public WeaponCategoryScraper() {
        super(LOGGER);
    }

    public List<WeaponCategoryBase> scrapeWeaponCategories() {
        List<WeaponCategoryBase> categories = new ArrayList<>();
        WebDriver driver = null;

        try {
            logger.info("Starting weapon category scraping from: " + WEAPON_PAGE_URL);

            driver = createWebDriver();
            driver.get(WEAPON_PAGE_URL);

            Thread.sleep(1000);

            List<WebElement> galleryRows = driver.findElements(By.cssSelector("div.row.gallery"));

            for (WebElement row : galleryRows) {
                List<WebElement> cols = row.findElements(By.cssSelector("div[class*='col-']"));

                for (WebElement col : cols) {
                    try {
                        String categoryName = null;
                        String imgSrc = null;

                        WebElement aElement = col.findElement(By.cssSelector("a.wiki_link"));
                        if (aElement != null) {
                            categoryName = aElement.getText().trim();
                        }

                        WebElement imgElement = col.findElement(By.tagName("img"));
                        if (imgElement != null) {
                            imgSrc = imgElement.getAttribute("src");
                            if (imgSrc == null || imgSrc.isEmpty()) {
                                imgSrc = imgElement.getAttribute("data-src");
                            }
                        }

                        String fullImageUrl = imgSrc;
                        if (imgSrc != null && imgSrc.startsWith("/")) {
                            fullImageUrl = ApiConfig.getBaseUrl() + imgSrc;
                        }

                        if (categoryName != null && !categoryName.isEmpty() && fullImageUrl != null && !fullImageUrl.isEmpty()) {
                            WeaponCategoryBase category = new WeaponCategoryBase(categoryName, fullImageUrl);
                            categories.add(category);
                            logger.fine("Scraped weapon category: " + categoryName + " with image: " + fullImageUrl);
                        }

                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to extract weapon category from col element", e);
                    }
                }
            }

            logger.info("Successfully scraped " + categories.size() + " weapon category items");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to scrape weapon categories", e);
        } finally {
            quitDriver(driver);
        }

        return categories;
    }
}

