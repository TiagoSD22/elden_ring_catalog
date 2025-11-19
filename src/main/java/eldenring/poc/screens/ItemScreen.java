package eldenring.poc.screens;

import eldenring.poc.models.ItemBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.ItemService;

import java.util.List;
import java.util.logging.Logger;

public class ItemScreen extends BaseListScreen<ItemBase> {
    private static final Logger LOGGER = Logger.getLogger(ItemScreen.class.getName());
    private ItemService service;

    public ItemScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "items");
    }

    @Override
    protected List<ItemBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new ItemService();
        }
        return service.fetchItems(limit, page);
    }
}

