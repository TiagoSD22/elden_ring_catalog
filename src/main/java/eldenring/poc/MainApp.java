package eldenring.poc;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.navigation.ScreenFactory;

import java.net.URL;
import javafx.scene.Node;
import javafx.geometry.Insets;

public class MainApp extends Application {

    private AppNavigator navigator;
    private ScreenFactory screenFactory;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1000, 700);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        String[] tabs = new String[]{"Ammos", "Armors", "Ashes of War", "Bosses", "Classes", "Creatures", "Incantations", "Items", "Locations", "NPCs", "Shields", "Sorceries", "Spirits", "Talismans", "Weapons"};
        for (String t : tabs) {
            Tab tab = new Tab(t);
            tabPane.getTabs().add(tab);
        }

        StackPane backgroundPane = new StackPane();
        URL bgUrl = getClass().getResource("/background.png");
        if (bgUrl != null) {
            Image bgImage = new Image(bgUrl.toExternalForm());
            BackgroundSize bSize = new BackgroundSize(100, 100, true, true, false, true); // cover
            BackgroundImage bImg = new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bSize);
            backgroundPane.setBackground(new Background(bImg));
        } else {
            backgroundPane.getChildren().add(new Label("Background image not found"));
        }

        StackPane contentContainer = new StackPane();

        StackPane overlay = new StackPane(backgroundPane, contentContainer, tabPane);
        StackPane.setMargin(tabPane, Insets.EMPTY);
        StackPane.setAlignment(tabPane, javafx.geometry.Pos.TOP_CENTER);
        overlay.setPadding(Insets.EMPTY);
        backgroundPane.setPadding(Insets.EMPTY);
        contentContainer.setPadding(Insets.EMPTY);

        root.setCenter(overlay);

        navigator = new AppNavigator(contentContainer);
        screenFactory = new ScreenFactory();

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null) return;
            String tabName = newTab.getText();
            Node screen = screenFactory.getScreen(tabName, navigator);
            navigator.setCenter(screen);
        });

        Scene scene = new Scene(root);

        URL cssUrl = getClass().getResource("/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        URL itemsCss = getClass().getResource("/items.css");
        if (itemsCss != null) {
            scene.getStylesheets().add(itemsCss.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("Elden Ring Catalog");
        primaryStage.show();

        Platform.runLater(() -> {
            Node headerArea = tabPane.lookup(".tab-header-area");
            if (headerArea != null) {
                double headerHeight = headerArea.getBoundsInParent().getHeight();
                if (headerHeight <= 0) {
                    headerHeight = headerArea.getLayoutBounds().getHeight();
                }
                if (headerHeight > 0) {
                    tabPane.setPrefHeight(headerHeight);
                    tabPane.setMinHeight(headerHeight);
                    tabPane.setMaxHeight(headerHeight);
                    contentContainer.setPadding(new Insets(headerHeight, 0, 0, 0));
                }
            }
            tabPane.getSelectionModel().select(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
