package eldenring.poc.services;

import eldenring.poc.models.ClassBase;
import eldenring.poc.scrapers.ClassScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(ClassService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "class/page/";

    private final ClassScraper scraper = new ClassScraper();

    public ClassService() {
        super(LOGGER);
    }

    public List<ClassBase> fetchClasses(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<ClassBase> cachedPage = getCachedPage(pageKey);

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

            cacheAllPagesInChunks(allClasses, limit, CACHE_KEY_PAGE_PREFIX);

            List<ClassBase> result = getCachedPage(pageKey);
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
}
