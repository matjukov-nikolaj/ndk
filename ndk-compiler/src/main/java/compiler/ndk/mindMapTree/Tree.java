package compiler.ndk.mindMapTree;

public class Tree {

    private Node root;

    public Tree(String rootTitle) {
        this.root = new Node(rootTitle);
    }

    public Node getRoot() {
        return this.root;
    }

}
