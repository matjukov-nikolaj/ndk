package compiler.ndk.mindMapTree;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String title;

    private List<Node> children;

    public Node(String title) {
        this.title = title;
        this.children = new ArrayList<>();
    }

    public void appendChild(Node node) {
        this.children.add(node);
    }

}
