package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eldenring.poc.models.ArmorBase;
import eldenring.poc.scrapers.ArmorScraper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for fetching and caching armor data.
 * Uses web scraping to extract data from Fextralife wiki and caches results.
 */
public class ArmorService {
    private static final Logger LOGGER = Logger.getLogger(ArmorService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "armor/page/";

    private final ArmorScraper scraper = new ArmorScraper();

    // Singleton cache shared across all ArmorService instances to survive tab switches
    // Key format: "armor/page/{pageNumber}" for paginated data
    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    /**
     * Fetches armors for the specified page with pagination.
     * First checks cache, if not found, scrapes data and caches it in page-sized chunks.
     *
     * @param limit Number of items per page
     * @param page Page number (0-based)
     * @return List of ArmorBase objects for the requested page
     */
    public List<ArmorBase> fetchArmors(int limit, int page) {
        try {
            String pageKey = buildPageKey(page);
            List<ArmorBase> cachedPage = (List<ArmorBase>) cache.getIfPresent(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping armor data from wiki...");
            List<ArmorBase> allArmors = scraper.scrapeArmors();

            if (allArmors == null || allArmors.isEmpty()) {
                LOGGER.warning("No armor data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allArmors.size() + " armor items");

            cacheAllPagesInChunks(allArmors, limit);

            List<ArmorBase> result = (List<ArmorBase>) cache.getIfPresent(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch armors", e);
            return Collections.emptyList();
        }
    }

    /**
     * Splits the complete armor list into page-sized chunks and caches each page.
     *
     * @param allArmors Complete list of scraped armor data
     * @param pageSize Number of items per page
     */
    private void cacheAllPagesInChunks(List<ArmorBase> allArmors, int pageSize) {
        int totalItems = allArmors.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        LOGGER.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<ArmorBase> pageArmors = allArmors.subList(startIndex, endIndex);

            String pageKey = buildPageKey(pageNum);
            cache.put(pageKey, pageArmors);
            LOGGER.fine("Cached page " + pageNum + " with " + pageArmors.size() + " items");
        }

        LOGGER.info("Successfully cached all " + totalPages + " pages");
    }

    /**
     * Builds a cache key for a specific page.
     *
     * @param pageNumber Page number (0-based)
     * @return Cache key in format "armor/page/{pageNumber}"
     */
    private String buildPageKey(int pageNumber) {
        return CACHE_KEY_PAGE_PREFIX + pageNumber;
    }

    /**
     * Clears the cache, forcing a fresh scrape on next request.
     */
    public void clearCache() {
        cache.invalidateAll();
        LOGGER.info("Cache cleared");
    }
}

