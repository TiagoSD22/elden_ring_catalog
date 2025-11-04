package eldenring.poc.screens;

import eldenring.poc.models.SpiritBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.SpiritService;

import java.util.List;
import java.util.logging.Logger;

public class SpiritScreen extends BaseListScreen<SpiritBase> {
    private static final Logger LOGGER = Logger.getLogger(SpiritScreen.class.getName());
    private SpiritService service;

    public SpiritScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "spirits");
    }

    @Override
    protected List<SpiritBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new SpiritService();
        }
        return service.fetchSpirits(limit, page);
    }
}

