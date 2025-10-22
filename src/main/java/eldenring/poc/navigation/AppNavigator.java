package eldenring.poc.navigation;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.util.ArrayDeque;
import java.util.Deque;

public class AppNavigator {
    private final BorderPane root;
    private final Deque<Node> backStack = new ArrayDeque<>();

    public AppNavigator(BorderPane root) {
        this.root = root;
    }

    public void setCenter(Node node) {
        Node current = root.getCenter();
        if (current != null) {
            backStack.push(current);
        }
        root.setCenter(node);
    }

    public void replaceCenter(Node node) {
        root.setCenter(node);
    }

    public void goBack() {
        if (!backStack.isEmpty()) {
            Node node = backStack.pop();
            root.setCenter(node);
        }
    }
}

