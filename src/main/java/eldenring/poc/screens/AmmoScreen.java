package eldenring.poc.screens;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.models.Ammo;
import eldenring.poc.services.AmmoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmmoScreen {
    private final AppNavigator navigator;
    private final AmmoService service = new AmmoService();
    private final BorderPane view;
    private final FlowPane flow;
    private final Map<Integer, List<Ammo>> pageCache = new HashMap<>();
    private int currentPage = 0;
    private final Button previousButton;
    private final Button nextButton;
    private final Label pageLabel;

    public AmmoScreen(AppNavigator navigator) {
        this.navigator = navigator;

        // Create main layout
        view = new BorderPane();

        // Create content area
        flow = new FlowPane();
        flow.setPadding(new Insets(16));
        flow.setHgap(16);
        flow.setVgap(16);
        flow.setPrefWrapLength(900);

        ScrollPane scrollPane = new ScrollPane(flow);
        scrollPane.setFitToWidth(true);
        view.setCenter(scrollPane);

        // Create pagination controls at the bottom
        HBox paginationBox = new HBox(10);
        paginationBox.setPadding(new Insets(10));
        paginationBox.setAlignment(Pos.CENTER);

        previousButton = new Button("Previous");
        previousButton.setDisable(true);
        previousButton.setOnAction(e -> loadPreviousPage());

        pageLabel = new Label("Page 1");
        pageLabel.setStyle("-fx-font-weight: bold;");

        nextButton = new Button("Next");
        nextButton.setOnAction(e -> loadNextPage());

        paginationBox.getChildren().addAll(previousButton, pageLabel, nextButton);
        view.setBottom(paginationBox);

        // Load initial page
        loadPage(0);
    }

    private void loadPage(int page) {
        // Check if page is already cached
        if (pageCache.containsKey(page)) {
            List<Ammo> cachedList = pageCache.get(page);
            displayAmmos(cachedList);
            currentPage = page;
            updatePaginationControls();
            return;
        }

        // Show loading
        flow.getChildren().clear();
        Label loading = new Label("Loading ammos...");
        flow.getChildren().add(loading);
        nextButton.setDisable(true);

        // Fetch data in background thread
        new Thread(() -> {
            try {
                List<Ammo> list = service.fetchAmmos(20, page);

                // Cache the result
                pageCache.put(page, list);

                Platform.runLater(() -> {
                    displayAmmos(list);
                    currentPage = page;
                    updatePaginationControls();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    flow.getChildren().clear();
                    flow.getChildren().add(new Label("Failed to load ammos."));
                    nextButton.setDisable(false);
                });
            }
        }).start();
    }

    private void displayAmmos(List<Ammo> list) {
        flow.getChildren().clear();

        if (list.isEmpty()) {
            flow.getChildren().add(new Label("No items found."));
            return;
        }

        for (Ammo ammo : list) {
            Node card = createCard(ammo);
            flow.getChildren().add(card);
        }
    }

    private void updatePaginationControls() {
        previousButton.setDisable(currentPage == 0);
        pageLabel.setText("Page " + (currentPage + 1));

        // Enable next button if current page has items
        List<Ammo> currentPageData = pageCache.get(currentPage);
        nextButton.setDisable(currentPageData == null || currentPageData.isEmpty() || currentPageData.size() < 20);
    }

    private void loadPreviousPage() {
        if (currentPage > 0) {
            loadPage(currentPage - 1);
        }
    }

    private void loadNextPage() {
        loadPage(currentPage + 1);
    }

    private Node createCard(Ammo ammo) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(8);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-background-color: #222; -fx-background-radius: 8; -fx-border-radius:8; -fx-border-color: #444;");
        card.setPrefSize(160, 200);

        ImageView iv = new ImageView();
        iv.setFitWidth(120);
        iv.setFitHeight(120);
        iv.setPreserveRatio(true);
        try {
            Image img = new Image(ammo.getImage(), true);
            iv.setImage(img);
        } catch (Exception e) {
            // ignore image load errors
        }

        Label name = new Label(ammo.getName());
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        name.setWrapText(true);
        name.setMaxWidth(140);

        card.getChildren().addAll(iv, name);

        card.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
            navigator.setCenter(new AmmoDetailScreen(navigator, ammo).getView());
        });

        StackPane wrapper = new StackPane(card);
        wrapper.setPrefSize(160, 200);
        return wrapper;
    }

    public Node getView() {
        return view;
    }
}
