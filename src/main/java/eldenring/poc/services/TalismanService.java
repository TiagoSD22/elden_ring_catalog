package eldenring.poc.services;

import eldenring.poc.models.TalismanBase;
import eldenring.poc.scrapers.TalismanScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TalismanService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(TalismanService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "talisman/page/";

    private final TalismanScraper scraper = new TalismanScraper();

    public TalismanService() {
        super(LOGGER);
    }

    public List<TalismanBase> fetchTalismans(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<TalismanBase> cachedPage = getCachedPage(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping talisman data from wiki...");
            List<TalismanBase> allTalismans = scraper.scrapeTalismans();

            if (allTalismans == null || allTalismans.isEmpty()) {
                LOGGER.warning("No talisman data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allTalismans.size() + " talisman items");

            cacheAllPagesInChunks(allTalismans, limit, CACHE_KEY_PAGE_PREFIX);

            List<TalismanBase> result = getCachedPage(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch talismans", e);
            return Collections.emptyList();
        }
    }
}
