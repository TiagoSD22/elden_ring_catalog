package eldenring.poc.services;

import eldenring.poc.models.CreatureBase;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Minimal CreatureService until a dedicated scraper is implemented.
 * Returns empty lists and logs actions to avoid build-time scraper dependencies.
 */
public class CreatureService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(CreatureService.class.getName());
    private static final String CACHE_KEY_PAGE_PREFIX = "creature/page/";

    public CreatureService() {
        super(LOGGER);
    }

    public List<CreatureBase> fetchCreatures(int limit, int page) {
        try {
            LOGGER.info("CreatureService currently has no scraper implemented - returning empty list");
            return Collections.emptyList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch creatures", e);
            return Collections.emptyList();
        }
    }
}

