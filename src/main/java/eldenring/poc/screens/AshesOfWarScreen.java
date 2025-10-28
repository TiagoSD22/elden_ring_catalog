package eldenring.poc.screens;

import eldenring.poc.models.AshesOfWarBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.AshesOfWarService;

import java.util.List;
import java.util.logging.Logger;

/**
 * Screen for displaying Ashes of War items with pagination.
 * Extends BaseListScreen to leverage common pagination logic.
 */
public class AshesOfWarScreen extends BaseListScreen<AshesOfWarBase> {
    private static final Logger LOGGER = Logger.getLogger(AshesOfWarScreen.class.getName());
    private AshesOfWarService service;

    public AshesOfWarScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "Ashes of War");
    }

    @Override
    protected List<AshesOfWarBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new AshesOfWarService();
        }
        return service.fetchAshesOfWar(limit, page);
    }
}

