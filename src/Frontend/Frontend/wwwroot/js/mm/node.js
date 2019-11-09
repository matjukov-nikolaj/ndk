class Node {
    constructor(title) {
        this.title = title;
        this.children = [];
        this.parent = null;
    }

    addChild(nodeTitle) {
        if (this.children.length >= globalConfig.MAX_CHILDREN_SIZE)
        {
            return;
        }
        const node = new Node(nodeTitle);
        node.parent = this;
        const pushed = this.children.push(node);
        return this.children[pushed - 1];
    }

    appendChild(node) {
        this.children.push(node);
        node.parent = this;
    }
}
