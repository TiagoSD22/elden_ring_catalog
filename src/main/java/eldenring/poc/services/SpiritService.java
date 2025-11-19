package eldenring.poc.services;

import eldenring.poc.models.SpiritBase;
import eldenring.poc.scrapers.SpiritScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpiritService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(SpiritService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "spirit/page/";

    private final SpiritScraper scraper = new SpiritScraper();

    public SpiritService() {
        super(LOGGER);
    }

    public List<SpiritBase> fetchSpirits(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<SpiritBase> cachedPage = getCachedPage(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping spirit data from wiki...");
            List<SpiritBase> allSpirits = scraper.scrapeSpirits();

            if (allSpirits == null || allSpirits.isEmpty()) {
                LOGGER.warning("No spirit data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allSpirits.size() + " spirit items");

            cacheAllPagesInChunks(allSpirits, limit, CACHE_KEY_PAGE_PREFIX);

            List<SpiritBase> result = getCachedPage(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch spirits", e);
            return Collections.emptyList();
        }
    }
}
