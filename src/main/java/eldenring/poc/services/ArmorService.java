package eldenring.poc.services;

import eldenring.poc.models.ArmorBase;
import eldenring.poc.scrapers.ArmorScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArmorService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(ArmorService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "armor/page/";

    private final ArmorScraper scraper = new ArmorScraper();

    public ArmorService() {
        super(LOGGER);
    }

    /**
     * Fetches armors for the specified page with pagination.
     * First checks cache, if not found, scrapes data and caches it in page-sized chunks.
     *
     * @param limit Number of items per page
     * @param page Page number (0-based)
     * @return List of ArmorBase objects for the requested page
     */
    public List<ArmorBase> fetchArmors(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<ArmorBase> cachedPage = getCachedPage(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping armor data from wiki...");
            List<ArmorBase> allArmors = scraper.scrapeArmors();

            if (allArmors == null || allArmors.isEmpty()) {
                LOGGER.warning("No armor data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allArmors.size() + " armor items");

            cacheAllPagesInChunks(allArmors, limit, CACHE_KEY_PAGE_PREFIX);

            List<ArmorBase> result = getCachedPage(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch armors", e);
            return Collections.emptyList();
        }
    }
}
