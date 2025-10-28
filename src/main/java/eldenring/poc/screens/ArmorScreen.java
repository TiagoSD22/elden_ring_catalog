package eldenring.poc.screens;

import eldenring.poc.models.ArmorBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.ArmorService;

import java.util.List;
import java.util.logging.Logger;

/**
 * Screen for displaying armor items with pagination.
 * Extends BaseListScreen to leverage common pagination logic.
 */
public class ArmorScreen extends BaseListScreen<ArmorBase> {
    private static final Logger LOGGER = Logger.getLogger(ArmorScreen.class.getName());
    private ArmorService service;

    public ArmorScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "armors");
    }

    @Override
    protected List<ArmorBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new ArmorService();
        }
        return service.fetchArmors(limit, page);
    }
}

