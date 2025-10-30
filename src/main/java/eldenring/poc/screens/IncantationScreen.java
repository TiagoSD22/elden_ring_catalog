package eldenring.poc.screens;

import eldenring.poc.models.IncantationBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.IncantationService;

import java.util.List;
import java.util.logging.Logger;

public class IncantationScreen extends BaseListScreen<IncantationBase> {
    private static final Logger LOGGER = Logger.getLogger(IncantationScreen.class.getName());
    private IncantationService service;

    public IncantationScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "incantations");
    }

    @Override
    protected List<IncantationBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new IncantationService();
        }
        return service.fetchIncantations(limit, page);
    }
}

