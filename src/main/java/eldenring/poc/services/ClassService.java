package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import eldenring.poc.models.ClassBase;
import eldenring.poc.scrapers.ClassScraper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassService {
    private static final Logger LOGGER = Logger.getLogger(ClassService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "class/page/";

    private final ClassScraper scraper = new ClassScraper();

    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    public List<ClassBase> fetchClasses(int limit, int page) {
        try {
            String pageKey = buildPageKey(page);
            List<ClassBase> cachedPage = (List<ClassBase>) cache.getIfPresent(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping class data from wiki...");
            List<ClassBase> allClasses = scraper.scrapeClasses();

            if (allClasses == null || allClasses.isEmpty()) {
                LOGGER.warning("No class data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allClasses.size() + " class items");

            cacheAllPagesInChunks(allClasses, limit);

            List<ClassBase> result = (List<ClassBase>) cache.getIfPresent(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch classes", e);
            return Collections.emptyList();
        }
    }

    private void cacheAllPagesInChunks(List<ClassBase> allClasses, int pageSize) {
        int totalItems = allClasses.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        LOGGER.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<ClassBase> pageClasses = allClasses.subList(startIndex, endIndex);

            String pageKey = buildPageKey(pageNum);
            cache.put(pageKey, pageClasses);
            LOGGER.fine("Cached page " + pageNum + " with " + pageClasses.size() + " items");
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

