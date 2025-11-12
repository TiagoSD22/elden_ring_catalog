package eldenring.poc.screens;

import eldenring.poc.models.ShieldBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.ShieldService;

import java.util.List;
import java.util.logging.Logger;

public class ShieldScreen extends BaseListScreen<ShieldBase> {
    private static final Logger LOGGER = Logger.getLogger(ShieldScreen.class.getName());
    private ShieldService service;

    public ShieldScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "shields");
    }

    @Override
    protected List<ShieldBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new ShieldService();
        }
        return service.fetchShields(limit, page);
    }
}

