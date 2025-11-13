package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eldenring.poc.models.WeaponCategoryBase;
import eldenring.poc.scrapers.WeaponCategoryScraper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeaponCategoryService {
    private static final Logger LOGGER = Logger.getLogger(WeaponCategoryService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "weaponCategory/page/";

    private final WeaponCategoryScraper scraper = new WeaponCategoryScraper();

    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    public List<WeaponCategoryBase> fetchWeaponCategories(int limit, int page) {
        try {
            String pageKey = buildPageKey(page);
            List<WeaponCategoryBase> cachedPage = (List<WeaponCategoryBase>) cache.getIfPresent(pageKey);

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

            cacheAllPagesInChunks(allCategories, limit);

            List<WeaponCategoryBase> result = (List<WeaponCategoryBase>) cache.getIfPresent(pageKey);
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

    private void cacheAllPagesInChunks(List<WeaponCategoryBase> allCategories, int pageSize) {
        int totalItems = allCategories.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        LOGGER.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<WeaponCategoryBase> pageCategories = allCategories.subList(startIndex, endIndex);

            String pageKey = buildPageKey(pageNum);
            cache.put(pageKey, pageCategories);
            LOGGER.fine("Cached page " + pageNum + " with " + pageCategories.size() + " items");
        }

        LOGGER.info("Successfully cached all " + totalPages + " pages");
    }

    private String buildPageKey(int pageNumber) {
        return CACHE_KEY_PAGE_PREFIX + pageNumber;
    }

    public void clearCache() {
        cache.invalidateAll();
        LOGGER.info("Cache cleared");
    }
}

