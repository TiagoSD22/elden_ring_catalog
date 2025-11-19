package eldenring.poc.services;

import eldenring.poc.models.SorceryBase;
import eldenring.poc.scrapers.SorceryScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SorceryService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(SorceryService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "sorcery/page/";

    private final SorceryScraper scraper = new SorceryScraper();

    public SorceryService() {
        super(LOGGER);
    }

    public List<SorceryBase> fetchSorceries(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<SorceryBase> cachedPage = getCachedPage(pageKey);

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

            cacheAllPagesInChunks(allSorceries, limit, CACHE_KEY_PAGE_PREFIX);

            List<SorceryBase> result = getCachedPage(pageKey);
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
}
