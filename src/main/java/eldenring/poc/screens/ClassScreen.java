package eldenring.poc.screens;

import eldenring.poc.models.ClassBase;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.services.ClassService;

import java.util.List;
import java.util.logging.Logger;

public class ClassScreen extends BaseListScreen<ClassBase> {
    private static final Logger LOGGER = Logger.getLogger(ClassScreen.class.getName());
    private ClassService service;

    public ClassScreen(AppNavigator navigator) {
        super(navigator, LOGGER, "classes");
    }

    @Override
    protected List<ClassBase> fetchItems(int limit, int page) {
        if (service == null) {
            service = new ClassService();
        }
        return service.fetchClasses(limit, page);
    }
}

