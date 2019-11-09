class TreeLoader {
    load(json) {
        const jsonRoot = this._checkValue(json[globalConfig.ROOT_KEY]);
        const tree = new Tree(this._checkValue(jsonRoot[globalConfig.TITLE_KEY]));
        tree.root = this.loadNode(jsonRoot);
        return tree;
    }

    loadNode(nodeJson) {
        const node = new Node(this._checkValue(nodeJson[globalConfig.TITLE_KEY]));
        const children = this._checkValue(nodeJson[globalConfig.CHILDREN_KEY]);
        for (const child of children) {
            const childNode = this.loadNode(child);
            node.appendChild(childNode);
        }
        return node;
    }

    _checkValue(value) {
        if (!value) {
            throw new Error("Invalid json");
        }
        return value;
    }
}