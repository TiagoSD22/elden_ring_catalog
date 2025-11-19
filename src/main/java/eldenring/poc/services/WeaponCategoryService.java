package eldenring.poc.services;

import eldenring.poc.models.WeaponCategoryBase;
import eldenring.poc.scrapers.WeaponCategoryScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeaponCategoryService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(WeaponCategoryService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "weaponCategory/page/";

    private final WeaponCategoryScraper scraper = new WeaponCategoryScraper();

    public WeaponCategoryService() {
        super(LOGGER);
    }

    public List<WeaponCategoryBase> fetchWeaponCategories(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<WeaponCategoryBase> cachedPage = getCachedPage(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping weapon category data from wiki...");
            List<WeaponCategoryBase> allCategories = scraper.scrapeWeaponCategories();

            if (allCategories == null || allCategories.isEmpty()) {
                LOGGER.warning("No weapon category data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allCategories.size() + " weapon category items");

            cacheAllPagesInChunks(allCategories, limit, CACHE_KEY_PAGE_PREFIX);

            List<WeaponCategoryBase> result = getCachedPage(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch weapon categories", e);
            return Collections.emptyList();
        }
    }
}
