package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eldenring.poc.models.IncantationBase;
import eldenring.poc.scrapers.IncantationScraper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IncantationService {
    private static final Logger LOGGER = Logger.getLogger(IncantationService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "incantation/page/";

    private final IncantationScraper scraper = new IncantationScraper();

    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    public List<IncantationBase> fetchIncantations(int limit, int page) {
        try {
            String pageKey = buildPageKey(page);
            List<IncantationBase> cachedPage = (List<IncantationBase>) cache.getIfPresent(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping incantation data from wiki...");
            List<IncantationBase> allIncantations = scraper.scrapeIncantations();

            if (allIncantations == null || allIncantations.isEmpty()) {
                LOGGER.warning("No incantation data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allIncantations.size() + " incantation items");

            cacheAllPagesInChunks(allIncantations, limit);

            List<IncantationBase> result = (List<IncantationBase>) cache.getIfPresent(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch incantations", e);
            return Collections.emptyList();
        }
    }

    private void cacheAllPagesInChunks(List<IncantationBase> allIncantations, int pageSize) {
        int totalItems = allIncantations.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        LOGGER.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<IncantationBase> pageIncantations = allIncantations.subList(startIndex, endIndex);

            String pageKey = buildPageKey(pageNum);
            cache.put(pageKey, pageIncantations);
            LOGGER.fine("Cached page " + pageNum + " with " + pageIncantations.size() + " items");
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

