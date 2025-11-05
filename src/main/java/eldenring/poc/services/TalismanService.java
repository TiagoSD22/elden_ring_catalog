package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eldenring.poc.models.TalismanBase;
import eldenring.poc.scrapers.TalismanScraper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TalismanService {
    private static final Logger LOGGER = Logger.getLogger(TalismanService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "talisman/page/";

    private final TalismanScraper scraper = new TalismanScraper();

    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    public List<TalismanBase> fetchTalismans(int limit, int page) {
        try {
            String pageKey = buildPageKey(page);
            List<TalismanBase> cachedPage = (List<TalismanBase>) cache.getIfPresent(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping talisman data from wiki...");
            List<TalismanBase> allTalismans = scraper.scrapeTalismans();

            if (allTalismans == null || allTalismans.isEmpty()) {
                LOGGER.warning("No talisman data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allTalismans.size() + " talisman items");

            cacheAllPagesInChunks(allTalismans, limit);

            List<TalismanBase> result = (List<TalismanBase>) cache.getIfPresent(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch talismans", e);
            return Collections.emptyList();
        }
    }

    private void cacheAllPagesInChunks(List<TalismanBase> allTalismans, int pageSize) {
        int totalItems = allTalismans.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        LOGGER.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<TalismanBase> pageTalismans = allTalismans.subList(startIndex, endIndex);

            String pageKey = buildPageKey(pageNum);
            cache.put(pageKey, pageTalismans);
            LOGGER.fine("Cached page " + pageNum + " with " + pageTalismans.size() + " items");
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

