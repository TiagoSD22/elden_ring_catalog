package eldenring.poc.screens;

import eldenring.poc.models.BaseModel;
import eldenring.poc.navigation.AppNavigator;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic base screen for displaying paginated lists of catalog items.
 * Reduces code duplication across screen implementations by providing common pagination logic.
 *
 * @param <T> The model type that extends BaseModel
 */
public abstract class BaseListScreen<T extends BaseModel> extends BaseScreen {
    protected final AppNavigator navigator;
    protected final Map<Integer, List<T>> pageCache = new HashMap<>();
    protected int currentPage = 0;
    protected static final int PAGE_SIZE = 20;

    private final Logger logger;
    private final String itemTypeName;

    /**
     * Creates a new BaseListScreen.
     *
     * @param navigator The app navigator for screen transitions
     * @param logger Logger instance for this screen
     * @param itemTypeName Display name for the item type (e.g., "ammos", "armors")
     */
    protected BaseListScreen(AppNavigator navigator, Logger logger, String itemTypeName) {
        super();
        this.navigator = navigator;
        this.logger = logger;
        this.itemTypeName = itemTypeName;

        // Use paginationBox from BaseScreen
        this.setBottom(paginationBox);

        // Wire base pagination buttons to local handlers
        setOnPrevious(e -> loadPreviousPage());
        setOnNext(e -> loadNextPage());

        // Load initial page
        loadPage(0);
    }

    /**
     * Fetches items for a specific page from the service layer.
     * Must be implemented by subclasses to call their specific service.
     *
     * @param limit Number of items per page
     * @param page Page number (0-based)
     * @return List of items for the requested page
     */
    protected abstract List<T> fetchItems(int limit, int page);

    /**
     * Creates a card node for an individual item.
     * Subclasses can override to customize card creation (e.g., add click handlers).
     *
     * @param item The item to create a card for
     * @return Node representing the card
     */
    protected Node createItemCard(T item) {
        return createImageCard(item.getImageUrl(), item.getDisplayName());
    }

    /**
     * Loads a specific page of items.
     * Checks cache first, then fetches from service if needed.
     *
     * @param page Page number to load (0-based)
     */
    private void loadPage(int page) {
        // Check if page is already cached
        if (pageCache.containsKey(page)) {
            List<T> cachedList = pageCache.get(page);
            displayItems(cachedList);
            currentPage = page;
            updatePaginationControls();
            return;
        }

        // Show loading
        contentFlow.getChildren().clear();
        contentFlow.getChildren().add(createLoadingLabel("Loading " + itemTypeName));
        setNextDisabled(true);

        // Fetch data in background thread
        new Thread(() -> {
            try {
                List<T> list = fetchItems(PAGE_SIZE, page);

                // Cache the result
                pageCache.put(page, list);

                Platform.runLater(() -> {
                    displayItems(list);
                    currentPage = page;
                    updatePaginationControls();
                });
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failed to fetch " + itemTypeName, ex);
                Platform.runLater(() -> {
                    contentFlow.getChildren().clear();
                    contentFlow.getChildren().add(createErrorLabel("Failed to load " + itemTypeName + "."));
                    setNextDisabled(false);
                });
            }
        }).start();
    }

    /**
     * Displays a list of items in the content area.
     *
     * @param list List of items to display
     */
    private void displayItems(List<T> list) {
        contentFlow.getChildren().clear();

        if (list == null || list.isEmpty()) {
            contentFlow.getChildren().add(createErrorLabel("No items found."));
            return;
        }

        for (T item : list) {
            Node card = createItemCard(item);
            contentFlow.getChildren().add(card);
        }
    }

    /**
     * Updates pagination controls based on current page state.
     */
    private void updatePaginationControls() {
        setPreviousDisabled(currentPage == 0);
        setPageLabel(currentPage);

        // Enable next button if current page has items
        List<T> currentPageData = pageCache.get(currentPage);
        setNextDisabled(currentPageData == null || currentPageData.isEmpty() || currentPageData.size() < PAGE_SIZE);
    }

    /**
     * Loads the previous page if available.
     */
    private void loadPreviousPage() {
        if (currentPage > 0) {
            loadPage(currentPage - 1);
        }
    }

    /**
     * Loads the next page.
     */
    private void loadNextPage() {
        loadPage(currentPage + 1);
    }

    /**
     * Gets the view node for this screen.
     *
     * @return This screen as a Node
     */
    public Node getView() {
        return this;
    }
}

