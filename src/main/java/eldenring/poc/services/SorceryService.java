package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eldenring.poc.models.SorceryBase;
import eldenring.poc.scrapers.SorceryScraper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SorceryService {
    private static final Logger LOGGER = Logger.getLogger(SorceryService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "sorcery/page/";

    private final SorceryScraper scraper = new SorceryScraper();

    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    public List<SorceryBase> fetchSorceries(int limit, int page) {
        try {
            String pageKey = buildPageKey(page);
            List<SorceryBase> cachedPage = (List<SorceryBase>) cache.getIfPresent(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping sorcery data from wiki...");
            List<SorceryBase> allSorceries = scraper.scrapeSorceries();

            if (allSorceries == null || allSorceries.isEmpty()) {
                LOGGER.warning("No sorcery data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allSorceries.size() + " sorcery items");

            cacheAllPagesInChunks(allSorceries, limit);

            List<SorceryBase> result = (List<SorceryBase>) cache.getIfPresent(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch sorceries", e);
            return Collections.emptyList();
        }
    }

    private void cacheAllPagesInChunks(List<SorceryBase> allSorceries, int pageSize) {
        int totalItems = allSorceries.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        LOGGER.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<SorceryBase> pageSorceries = allSorceries.subList(startIndex, endIndex);

            String pageKey = buildPageKey(pageNum);
            cache.put(pageKey, pageSorceries);
            LOGGER.fine("Cached page " + pageNum + " with " + pageSorceries.size() + " items");
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

