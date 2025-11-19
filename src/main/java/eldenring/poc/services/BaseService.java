package eldenring.poc.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BaseService {
    protected static final Cache<String, Object> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    protected final Logger logger;

    public BaseService(Logger logger) {
        this.logger = logger;
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> getCachedPage(String pageKey) {
        return (List<T>) CACHE.getIfPresent(pageKey);
    }

    protected <T> void cacheAllPagesInChunks(List<T> allItems, int pageSize, String cacheKeyPrefix) {
        int totalItems = allItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        logger.info("Splitting " + totalItems + " items into " + totalPages + " pages (size: " + pageSize + ")");

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            int startIndex = pageNum * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);

            List<T> pageItems = allItems.subList(startIndex, endIndex);

            String pageKey = buildPageKey(cacheKeyPrefix, pageNum);
            CACHE.put(pageKey, pageItems);
            logger.fine("Cached page " + pageNum + " with " + pageItems.size() + " items");
        }

        logger.info("Successfully cached all " + totalPages + " pages");
    }

    protected String buildPageKey(String prefix, int pageNumber) {
        return prefix + pageNumber;
    }

    public void clearCache() {
        CACHE.invalidateAll();
        logger.info("Cache cleared");
    }
}

