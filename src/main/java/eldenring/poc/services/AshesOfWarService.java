package eldenring.poc.services;

import eldenring.poc.models.AshesOfWarBase;
import eldenring.poc.scrapers.AshesOfWarScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AshesOfWarService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(AshesOfWarService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "ashesofwar/page/";

    private final AshesOfWarScraper scraper = new AshesOfWarScraper();

    public AshesOfWarService() {
        super(LOGGER);
    }

    /**
     * Fetches Ashes of War for the specified page with pagination.
     * First checks cache, if not found, scrapes data and caches it in page-sized chunks.
     *
     * @param limit Number of items per page
     * @param page Page number (0-based)
     * @return List of AshesOfWarBase objects for the requested page
     */
    public List<AshesOfWarBase> fetchAshesOfWar(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<AshesOfWarBase> cachedPage = getCachedPage(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping Ashes of War data from wiki...");
            List<AshesOfWarBase> allAshesOfWar = scraper.scrapeAshesOfWar();

            if (allAshesOfWar == null || allAshesOfWar.isEmpty()) {
                LOGGER.warning("No Ashes of War data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allAshesOfWar.size() + " Ashes of War items");

            cacheAllPagesInChunks(allAshesOfWar, limit, CACHE_KEY_PAGE_PREFIX);

            List<AshesOfWarBase> result = getCachedPage(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch Ashes of War", e);
            return Collections.emptyList();
        }
    }
}
