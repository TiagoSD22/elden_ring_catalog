package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eldenring.poc.models.Ammo;
import eldenring.poc.models.AmmoBase;
import eldenring.poc.scrapers.AmmoScraper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for fetching and caching ammo data.
 * Uses web scraping to extract data from Fextralife wiki and caches results.
 */
public class AmmoService {
    private static final Logger LOGGER = Logger.getLogger(AmmoService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "ammo/page/";

    private final AmmoScraper scraper = new AmmoScraper();

    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    /**
     * Fetches ammos for the specified page with pagination.
     * First checks cache, if not found, scrapes data and caches it in page-sized chunks.
     *
     * @param limit Number of items per page
     * @param page Page number (0-based)
     * @return List of Ammo objects for the requested page
     */
    public List<Ammo> fetchAmmos(int limit, int page) {
        try {
            String pageKey = buildPageKey(page);
            List<Ammo> cachedPage = (List<Ammo>) cache.getIfPresent(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping ammo data from wiki...");
            List<AmmoBase> allAmmos = scraper.scrapeAmmos();

            if (allAmmos == null || allAmmos.isEmpty()) {
                LOGGER.warning("No ammo data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allAmmos.size() + " ammo items");

            cacheAllPagesInChunks(allAmmos, limit);

            List<Ammo> result = (List<Ammo>) cache.getIfPresent(pageKey);

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch ammos", e);
            return Collections.emptyList();
        }
    }

    /**
     * Splits the complete ammo list into page-sized chunks and caches each page.
     *
     * @param allAmmos Complete list of scraped ammo data
     * @param pageSize Number of items per page
     */
    private void cacheAllPagesInChunks(List<AmmoBase> allAmmos, int pageSize) {
        int totalItems = allAmmos.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        LOGGER.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<AmmoBase> pageAmmos = allAmmos.subList(startIndex, endIndex);

            // Convert AmmoBase to Ammo
            List<Ammo> pageResult = new ArrayList<>();
            for (AmmoBase base : pageAmmos) {
                Ammo ammo = new Ammo();
                ammo.setName(base.getName());
                ammo.setImage(base.getImage());
                pageResult.add(ammo);
            }

            String pageKey = buildPageKey(pageNum);
            cache.put(pageKey, pageResult);
            LOGGER.fine("Cached page " + pageNum + " with " + pageResult.size() + " items");
        }

        LOGGER.info("Successfully cached all " + totalPages + " pages");
    }

    /**
     * Builds a cache key for a specific page.
     *
     * @param pageNumber Page number (0-based)
     * @return Cache key in format "ammo/page/{pageNumber}"
     */
    private String buildPageKey(int pageNumber) {
        return CACHE_KEY_PAGE_PREFIX  + pageNumber;
    }

    /**
     * Clears the cache, forcing a fresh scrape on next request.
     */
    public void clearCache() {
        cache.invalidateAll();
        LOGGER.info("Cache cleared");
    }
}
