package eldenring.poc.screens;

import javafx.application.Platform;
import javafx.scene.Node;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.models.ArmorBase;
import eldenring.poc.services.ArmorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Screen for displaying armor items with pagination.
 * Uses the same base styling as AmmoScreen.
 */
public class ArmorScreen extends BaseScreen {
    private static final Logger LOGGER = Logger.getLogger(ArmorScreen.class.getName());

    private final AppNavigator navigator;
    private final ArmorService service = new ArmorService();
    private final Map<Integer, List<ArmorBase>> pageCache = new HashMap<>();
    private int currentPage = 0;

    public ArmorScreen(AppNavigator navigator) {
        super();
        this.navigator = navigator;

        // Use paginationBox from BaseScreen
        this.setBottom(paginationBox);

        // Wire base pagination buttons to local handlers
        setOnPrevious(e -> loadPreviousPage());
        setOnNext(e -> loadNextPage());

        // Load initial page
        loadPage(0);
    }

    private void loadPage(int page) {
        // Check if page is already cached
        if (pageCache.containsKey(page)) {
            List<ArmorBase> cachedList = pageCache.get(page);
            displayArmors(cachedList);
            currentPage = page;
            updatePaginationControls();
            return;
        }

        // Show loading
        contentFlow.getChildren().clear();
        contentFlow.getChildren().add(createLoadingLabel("Loading armors"));
        setNextDisabled(true);

        // Fetch data in background thread
        new Thread(() -> {
            try {
                List<ArmorBase> list = service.fetchArmors(20, page);

                // Cache the result
                pageCache.put(page, list);

                Platform.runLater(() -> {
                    displayArmors(list);
                    currentPage = page;
                    updatePaginationControls();
                });
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to fetch armors", ex);
                Platform.runLater(() -> {
                    contentFlow.getChildren().clear();
                    contentFlow.getChildren().add(createErrorLabel("Failed to load armors."));
                    setNextDisabled(false);
                });
            }
        }).start();
    }

    private void displayArmors(List<ArmorBase> list) {
        contentFlow.getChildren().clear();

        if (list == null || list.isEmpty()) {
            contentFlow.getChildren().add(createErrorLabel("No items found."));
            return;
        }

        for (ArmorBase armor : list) {
            Node card = createCard(armor);
            contentFlow.getChildren().add(card);
        }
    }

    private void updatePaginationControls() {
        setPreviousDisabled(currentPage == 0);
        setPageLabel(currentPage);

        // Enable next button if current page has items
        List<ArmorBase> currentPageData = pageCache.get(currentPage);
        setNextDisabled(currentPageData == null || currentPageData.isEmpty() || currentPageData.size() < 20);
    }

    private void loadPreviousPage() {
        if (currentPage > 0) {
            loadPage(currentPage - 1);
        }
    }

    private void loadNextPage() {
        loadPage(currentPage + 1);
    }

    private Node createCard(ArmorBase armor) {
        return createImageCard(armor.getImage(), armor.getTitle());
        // TODO: Add click handler for detail view when ArmorDetailScreen is implemented
        // wrapper.setOnMouseClicked(evt -> navigator.setCenter(new ArmorDetailScreen(navigator, armor).getView()));
    }

    public Node getView() {
        return this;
    }
}

