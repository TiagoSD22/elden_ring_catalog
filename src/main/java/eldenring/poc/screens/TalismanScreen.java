package eldenring.poc.screens;

import eldenring.poc.models.TalismanBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.TalismanService;

import java.util.List;
import java.util.logging.Logger;

public class TalismanScreen extends BaseListScreen<TalismanBase> {
    private static final Logger LOGGER = Logger.getLogger(TalismanScreen.class.getName());
    private TalismanService service;

    public TalismanScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "talismans");
    }

    @Override
    protected List<TalismanBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new TalismanService();
        }
        return service.fetchTalismans(limit, page);
    }
}

