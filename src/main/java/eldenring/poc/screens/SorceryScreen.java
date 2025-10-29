package eldenring.poc.screens;

import eldenring.poc.models.SorceryBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.SorceryService;

import java.util.List;
import java.util.logging.Logger;

public class SorceryScreen extends BaseListScreen<SorceryBase> {
    private static final Logger LOGGER = Logger.getLogger(SorceryScreen.class.getName());
    private SorceryService service;

    public SorceryScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "sorceries");
    }

    @Override
    protected List<SorceryBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new SorceryService();
        }
        return service.fetchSorceries(limit, page);
    }
}

