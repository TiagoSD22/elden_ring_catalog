package eldenring.poc.services;

import eldenring.poc.models.ItemBase;
import eldenring.poc.scrapers.ItemScraper;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(ItemService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "item/page/";

    private final ItemScraper scraper = new ItemScraper();

    public ItemService() {
        super(LOGGER);
    }

    public List<ItemBase> fetchItems(int limit, int page) {
        try {
            String pageKey = buildPageKey(CACHE_KEY_PAGE_PREFIX, page);
            List<ItemBase> cachedPage = getCachedPage(pageKey);

            if (cachedPage != null) {
                LOGGER.info("Returning cached data for page " + page + " (size: " + limit + ")");
                return cachedPage;
            }

            LOGGER.info("Cache miss for page " + page + " - scraping item data from wiki...");
            List<ItemBase> allItems = scraper.scrapeItems();

            if (allItems == null || allItems.isEmpty()) {
                LOGGER.warning("No item data scraped");
                return Collections.emptyList();
            }

            LOGGER.info("Scraped " + allItems.size() + " item items");

            cacheAllPagesInChunks(allItems, limit, CACHE_KEY_PAGE_PREFIX);

            List<ItemBase> result = getCachedPage(pageKey);
            if (result == null) {
                LOGGER.warning("Requested page still not found in cache after caching attempt: " + pageKey);
                return Collections.emptyList();
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch items", e);
            return Collections.emptyList();
        }
    }
}

