package eldenring.poc.navigation;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.ArrayDeque;
import java.util.Deque;

public class AppNavigator {
    private final StackPane centerContainer;
    private final Deque<Node> backStack = new ArrayDeque<>();

    public AppNavigator(StackPane centerContainer) {
        this.centerContainer = centerContainer;
    }

    public void setCenter(Node node) {
        Node current = null;
        if (!centerContainer.getChildren().isEmpty()) {
            current = centerContainer.getChildren().get(0);
        }
        if (current != null) {
            backStack.push(current);
        }
        centerContainer.getChildren().setAll(node);
    }

    public void replaceCenter(Node node) {
        centerContainer.getChildren().setAll(node);
    }

    public void goBack() {
        if (!backStack.isEmpty()) {
            Node node = backStack.pop();
            centerContainer.getChildren().setAll(node);
        }
    }
}
