package eldenring.poc.screens;

import eldenring.poc.models.CreatureBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.CreatureService;

import java.util.List;
import java.util.logging.Logger;

public class CreaturesScreen extends BaseListScreen<CreatureBase> {
    private static final Logger LOGGER = Logger.getLogger(CreaturesScreen.class.getName());
    private CreatureService service;

    public CreaturesScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "creatures");
    }

    @Override
    protected List<CreatureBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new CreatureService();
        }
        return service.fetchCreatures(limit, page);
    }
}

