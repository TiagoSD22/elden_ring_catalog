package eldenring.poc.screens;

import eldenring.poc.models.AmmoBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import eldenring.poc.models.AttackPower;
import eldenring.poc.navigation.AppNavigator;

public class AmmoDetailScreen {
    private final AppNavigator navigator;
    private final AmmoBase ammo;
    private final ScrollPane view;

    public AmmoDetailScreen(AppNavigator navigator, AmmoBase ammo) {
        this.navigator = navigator;
        this.ammo = ammo;

        VBox root = new VBox();
        root.setSpacing(16);
        root.setPadding(new Insets(16));
        root.setAlignment(Pos.TOP_CENTER);

        // Back button
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Button back = new Button("Back");
        back.setOnAction(e -> this.navigator.goBack());
        top.getChildren().add(back);
        root.getChildren().add(top);

        // Image
        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.setFitWidth(300);
        try {
            Image img = new Image(ammo.getImage(), true);
            iv.setImage(img);
        } catch (Exception ignored) {}
        root.getChildren().add(iv);

        // Details grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(8));

        int row = 0;
        grid.add(new Label("Name:"), 0, row);
        grid.add(new Label(nullToEmpty(ammo.getName())), 1, row++);

        root.getChildren().add(grid);

        view = new ScrollPane(root);
        view.setFitToWidth(true);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public Node getView() {
        return view;
    }
}

