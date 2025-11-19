package eldenring.poc.services;

import eldenring.poc.models.AmmoBase;
import eldenring.poc.scrapers.AmmoScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AmmoService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(AmmoService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "ammo/page/";

    private final AmmoScraper scraper = new AmmoScraper();

    public AmmoService() {
        super(LOGGER);
    }

    /**
     * Fetches ammos for the specified page with pagination.
     * First checks cache, if not found, scrapes data and caches it in page-sized chunks.
     *
     * @param limit Number of items per page
     * @param page Page number (0-based)
     * @return List of Ammo objects for the requested page
     */
    public List<AmmoBase> fetchAmmos(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<AmmoBase> cachedPage = getCachedPage(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping ammo data from wiki...");
            List<AmmoBase> allAmmos = scraper.scrapeAmmos();

            if (allAmmos == null || allAmmos.isEmpty()) {
                LOGGER.warning("No ammo data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allAmmos.size() + " ammo items");

            cacheAllPagesInChunks(allAmmos, limit, CACHE_KEY_PAGE_PREFIX);

            return getCachedPage(pageKey);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch ammos", e);
            return Collections.emptyList();
        }
    }
}
