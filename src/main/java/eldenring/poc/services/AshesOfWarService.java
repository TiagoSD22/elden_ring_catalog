package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eldenring.poc.models.AshesOfWarBase;
import eldenring.poc.scrapers.AshesOfWarScraper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for fetching and caching Ashes of War data.
 * Uses web scraping to extract data from Fextralife wiki and caches results.
 */
public class AshesOfWarService {
    private static final Logger LOGGER = Logger.getLogger(AshesOfWarService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "ashesofwar/page/";

    private final AshesOfWarScraper scraper = new AshesOfWarScraper();

    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    /**
     * Fetches Ashes of War for the specified page with pagination.
     * First checks cache, if not found, scrapes data and caches it in page-sized chunks.
     *
     * @param limit Number of items per page
     * @param page Page number (0-based)
     * @return List of AshesOfWarBase objects for the requested page
     */
    public List<AshesOfWarBase> fetchAshesOfWar(int limit, int page) {
        try {
            String pageKey = buildPageKey(page);
            List<AshesOfWarBase> cachedPage = (List<AshesOfWarBase>) cache.getIfPresent(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping Ashes of War data from wiki...");
            List<AshesOfWarBase> allAshesOfWar = scraper.scrapeAshesOfWar();

            if (allAshesOfWar == null || allAshesOfWar.isEmpty()) {
                LOGGER.warning("No Ashes of War data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allAshesOfWar.size() + " Ashes of War items");

            cacheAllPagesInChunks(allAshesOfWar, limit);

            List<AshesOfWarBase> result = (List<AshesOfWarBase>) cache.getIfPresent(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch Ashes of War", e);
            return Collections.emptyList();
        }
    }

    /**
     * Splits the complete Ashes of War list into page-sized chunks and caches each page.
     *
     * @param allAshesOfWar Complete list of scraped Ashes of War data
     * @param pageSize Number of items per page
     */
    private void cacheAllPagesInChunks(List<AshesOfWarBase> allAshesOfWar, int pageSize) {
        int totalItems = allAshesOfWar.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        LOGGER.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<AshesOfWarBase> pageAshesOfWar = allAshesOfWar.subList(startIndex, endIndex);

            String pageKey = buildPageKey(pageNum);
            cache.put(pageKey, pageAshesOfWar);
            LOGGER.fine("Cached page " + pageNum + " with " + pageAshesOfWar.size() + " items");
        }

        LOGGER.info("Successfully cached all " + totalPages + " pages");
    }

    /**
     * Builds a cache key for a specific page.
     *
     * @param pageNumber Page number (0-based)
     * @return Cache key in format "ashesofwar/page/{pageNumber}"
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

