package eldenring.poc;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import eldenring.poc.navigation.AppNavigator;
import eldenring.poc.screens.AmmoScreen;

public class MainApp extends Application {

    private AppNavigator navigator;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1000, 700);

        // Top tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        String[] tabs = new String[]{"Ammos", "Armors", "Ashes of War", "Bosses", "Classes", "Creatures", "Incantations", "Items", "Locations", "NPCs", "Shields", "Sorceries", "Spirits", "Talismans", "Weapons"};
        for (String t : tabs) {
            Tab tab = new Tab(t);
            tab.setContent(new Label(""));
            tabPane.getTabs().add(tab);
        }

        root.setTop(tabPane);

        // Center initial background image
        Image bg = new Image(getClass().getResourceAsStream("/background.png"));
        ImageView bgView = new ImageView(bg);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(900);
        StackPane centerPane = new StackPane(bgView);
        centerPane.setPadding(new Insets(20));
        root.setCenter(centerPane);

        navigator = new AppNavigator(root);

        // Wire up tab selection for Ammos
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null) return;
            String title = newTab.getText();
            if ("Ammos".equals(title)) {
                navigator.setCenter(new AmmoScreen(navigator).getView());
            } else {
                // reset to background for unimplemented tabs
                navigator.setCenter(centerPane);
            }
        });

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Elden Ring Catalog");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
