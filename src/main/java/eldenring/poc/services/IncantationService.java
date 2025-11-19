package eldenring.poc.services;

import eldenring.poc.models.IncantationBase;
import eldenring.poc.scrapers.IncantationScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IncantationService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(IncantationService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "incantation/page/";

    private final IncantationScraper scraper = new IncantationScraper();

    public IncantationService() {
        super(LOGGER);
    }

    public List<IncantationBase> fetchIncantations(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<IncantationBase> cachedPage = getCachedPage(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping incantation data from wiki...");
            List<IncantationBase> allIncantations = scraper.scrapeIncantations();

            if (allIncantations == null || allIncantations.isEmpty()) {
                LOGGER.warning("No incantation data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allIncantations.size() + " incantation items");

            cacheAllPagesInChunks(allIncantations, limit, CACHE_KEY_PAGE_PREFIX);

            List<IncantationBase> result = getCachedPage(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch incantations", e);
            return Collections.emptyList();
        }
    }
}
