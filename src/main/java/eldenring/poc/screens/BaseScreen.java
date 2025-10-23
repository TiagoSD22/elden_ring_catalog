package eldenring.poc.screens;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Base abstract screen providing common UI pieces (pagination controls, card creation, common styles).
 * Now extends BorderPane so the screen itself is the root node and gets the default styling.
 */
public abstract class BaseScreen extends BorderPane {
    protected final HBox paginationBox;
    protected final Button previousButton;
    protected final Button nextButton;
    protected final Label pageLabel;

    protected final FlowPane contentFlow;
    protected final ScrollPane contentScroll;

    private static final double DEFAULT_CARD_WIDTH = 160;
    private static final double DEFAULT_CARD_HEIGHT = 200;

    protected BaseScreen() {
        this.getStyleClass().add("screen-content");

        contentFlow = new FlowPane();
        contentFlow.getStyleClass().add("items-flow");
        contentFlow.setAlignment(Pos.TOP_CENTER);

        contentScroll = new ScrollPane(contentFlow);
        contentScroll.setFitToWidth(true);
        contentScroll.getStyleClass().add("items-scroll");
        this.setCenter(contentScroll);

        previousButton = new Button("Previous");
        previousButton.getStyleClass().add("pagination-button");
        previousButton.setDisable(true);

        pageLabel = new Label("Page 1");
        pageLabel.getStyleClass().add("page-label");

        nextButton = new Button("Next");
        nextButton.getStyleClass().add("pagination-button");

        paginationBox = new HBox(10, previousButton, pageLabel, nextButton);
        paginationBox.getStyleClass().add("pagination-box");
        paginationBox.setAlignment(Pos.CENTER);
        paginationBox.setPadding(new Insets(8));
    }

    protected void setOnPrevious(EventHandler<ActionEvent> handler) {
        previousButton.setOnAction(handler);
    }

    protected void setOnNext(EventHandler<ActionEvent> handler) {
        nextButton.setOnAction(handler);
    }

    protected void setPreviousDisabled(boolean disabled) {
        previousButton.setDisable(disabled);
    }

    protected void setNextDisabled(boolean disabled) {
        nextButton.setDisable(disabled);
    }

    protected void setPageLabel(int pageZeroBased) {
        pageLabel.setText("Page " + (pageZeroBased + 1));
    }

    /**
     * Create a simple image card with a name below using default sizes. Styles are pulled from CSS classes.
     */
    protected Node createImageCard(String imageUrl, String name) {
        return createImageCard(imageUrl, name, DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT);
    }

    /**
     * Create a simple image card with a name below. Styles are pulled from CSS classes.
     * @param imageUrl remote or resource URL for the image
     * @param name the item name
     * @param prefWidth preferred width of the card
     * @param prefHeight preferred height of the card
     * @return a Node ready to be inserted in a layout
     */
    private Node createImageCard(String imageUrl, String name, double prefWidth, double prefHeight) {
        VBox card = new VBox();
        card.getStyleClass().add("item-card");
        card.setSpacing(8);
        card.setPadding(new Insets(8));
        card.setPrefSize(prefWidth, prefHeight);
        card.setAlignment(Pos.CENTER);

        ImageView iv = new ImageView();
        iv.getStyleClass().add("item-image");
        iv.setPreserveRatio(true);
        // set fallbacks for size; CSS can also influence these
        iv.setFitWidth(Math.max(80, prefWidth - 40));
        iv.setFitHeight(Math.max(80, prefHeight - 80));
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Image img = new Image(imageUrl, true);
                iv.setImage(img);
            }
        } catch (Exception ignored) {
        }

        Label lbl = new Label(name == null ? "" : name);
        lbl.getStyleClass().add("item-name");
        lbl.setWrapText(true);
        lbl.setMaxWidth(prefWidth - 20);

        card.getChildren().addAll(iv, lbl);

        StackPane wrapper = new StackPane(card);
        wrapper.getStyleClass().add("card-wrapper");
        wrapper.setPrefSize(prefWidth, prefHeight);
        return wrapper;
    }

    protected Label createLoadingLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("loading-label");
        return l;
    }

    protected Label createErrorLabel(String text) {
        Label l = new Label(text == null ? "An error occurred" : text);
        l.getStyleClass().add("error-label");
        return l;
    }
}
