package eldenring.poc.screens;

import eldenring.poc.models.WeaponCategoryBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.WeaponCategoryService;

import java.util.List;
import java.util.logging.Logger;

public class WeaponCategoryScreen extends BaseListScreen<WeaponCategoryBase> {
    private static final Logger LOGGER = Logger.getLogger(WeaponCategoryScreen.class.getName());
    private WeaponCategoryService service;

    public WeaponCategoryScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "Weapon Categories");
    }

    @Override
    protected List<WeaponCategoryBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new WeaponCategoryService();
        }
        return service.fetchWeaponCategories(limit, page);
    }
}

