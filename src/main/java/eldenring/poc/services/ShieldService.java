package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eldenring.poc.models.ShieldBase;
import eldenring.poc.scrapers.ShieldScraper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShieldService {
    private static final Logger LOGGER = Logger.getLogger(ShieldService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "shield/page/";

    private final ShieldScraper scraper = new ShieldScraper();

    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    public List<ShieldBase> fetchShields(int limit, int page) {
        try {
            String pageKey = buildPageKey(page);
            List<ShieldBase> cachedPage = (List<ShieldBase>) cache.getIfPresent(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping shield data from wiki...");
            List<ShieldBase> allShields = scraper.scrapeShields();

            if (allShields == null || allShields.isEmpty()) {
                LOGGER.warning("No shield data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allShields.size() + " shield items");

            cacheAllPagesInChunks(allShields, limit);

            List<ShieldBase> result = (List<ShieldBase>) cache.getIfPresent(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch shields", e);
            return Collections.emptyList();
        }
    }

    private void cacheAllPagesInChunks(List<ShieldBase> allShields, int pageSize) {
        int totalItems = allShields.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        LOGGER.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<ShieldBase> pageShields = allShields.subList(startIndex, endIndex);

            String pageKey = buildPageKey(pageNum);
            cache.put(pageKey, pageShields);
            LOGGER.fine("Cached page " + pageNum + " with " + pageShields.size() + " items");
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

