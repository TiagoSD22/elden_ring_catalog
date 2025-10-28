package eldenring.poc.screens;

import eldenring.poc.models.AmmoBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.AmmoService;
import javafx.scene.Node;

import java.util.List;
import java.util.logging.Logger;

/**
 * Screen for displaying ammo items with pagination.
 * Extends BaseListScreen to leverage common pagination logic.
 */
public class AmmoScreen extends BaseListScreen<AmmoBase> {
    private static final Logger LOGGER = Logger.getLogger(AmmoScreen.class.getName());
    private AmmoService service;

    public AmmoScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "ammos");
    }

    @Override
    protected List<AmmoBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new AmmoService();
        }
        return service.fetchAmmos(limit, page);
    }

    @Override
    protected Node createItemCard(AmmoBase item) {
        Node wrapper = createImageCard(item.getImageUrl(), item.getDisplayName());
        wrapper.setOnMouseClicked(evt -> navigator.setCenter(new AmmoDetailScreen(navigator, item).getView()));
        return wrapper;
    }
}
