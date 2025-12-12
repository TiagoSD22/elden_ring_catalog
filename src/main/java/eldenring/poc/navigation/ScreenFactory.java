package eldenring.poc.navigation;

import eldenring.poc.screens.AmmoScreen;
import eldenring.poc.screens.ArmorScreen;
import eldenring.poc.screens.AshesOfWarScreen;
import eldenring.poc.screens.ClassScreen;
import eldenring.poc.screens.CreaturesScreen;
import eldenring.poc.screens.IncantationScreen;
import eldenring.poc.screens.ItemScreen;
import eldenring.poc.screens.ShieldScreen;
import eldenring.poc.screens.SorceryScreen;
import eldenring.poc.screens.SpiritScreen;
import eldenring.poc.screens.TalismanScreen;
import eldenring.poc.screens.WeaponCategoryScreen;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory for creating screen instances based on tab names.
 * Uses a strategy pattern with a map to avoid long if-else chains.
 */
public class ScreenFactory {
    private final Map<String, Function<AppNavigator, Node>> screenCreators;

    public ScreenFactory() {
        screenCreators = new HashMap<>();

        screenCreators.put("Ammos", nav -> new AmmoScreen(nav).getView());
        screenCreators.put("Armors", nav -> new ArmorScreen(nav).getView());
        screenCreators.put("Ashes of War", nav -> new AshesOfWarScreen(nav).getView());
        screenCreators.put("Classes", nav -> new ClassScreen(nav).getView());
        screenCreators.put("Creatures", nav -> new CreaturesScreen(nav).getView());
        screenCreators.put("Incantations", nav -> new IncantationScreen(nav).getView());
        screenCreators.put("Shields", nav -> new ShieldScreen(nav).getView());
        screenCreators.put("Sorceries", nav -> new SorceryScreen(nav).getView());
        screenCreators.put("Spirits", nav -> new SpiritScreen(nav).getView());
        screenCreators.put("Talismans", nav -> new TalismanScreen(nav).getView());
        screenCreators.put("Items", nav -> new ItemScreen(nav).getView());
        screenCreators.put("Weapons", nav -> new WeaponCategoryScreen(nav).getView());
    }

    /**
     * Gets a screen for the specified tab name.
     *
     * @param tabName Name of the tab
     * @param navigator AppNavigator instance to pass to screens
     * @return Node containing the screen, or an empty label if tab not implemented
     */
    public Node getScreen(String tabName, AppNavigator navigator) {
        Function<AppNavigator, Node> creator = screenCreators.get(tabName);

        if (creator != null) {
            return creator.apply(navigator);
        }

        // Return empty label for unimplemented tabs
        return new Label("");
    }

    /**
     * Checks if a screen is implemented for the given tab name.
     *
     * @param tabName Name of the tab
     * @return true if screen is implemented, false otherwise
     */
    public boolean hasScreen(String tabName) {
        return screenCreators.containsKey(tabName);
    }
}
