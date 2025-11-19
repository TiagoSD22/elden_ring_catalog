package eldenring.poc.services;

import eldenring.poc.models.ShieldBase;
import eldenring.poc.scrapers.ShieldScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShieldService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(ShieldService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "shield/page/";

    private final ShieldScraper scraper = new ShieldScraper();

    public ShieldService() {
        super(LOGGER);
    }

    public List<ShieldBase> fetchShields(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<ShieldBase> cachedPage = getCachedPage(pageKey);

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

            cacheAllPagesInChunks(allShields, limit, CACHE_KEY_PAGE_PREFIX);

            List<ShieldBase> result = getCachedPage(pageKey);
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
}
