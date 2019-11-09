class TreeSaver {
    save(tree) {
        const json = {};
        json[globalConfig.ROOT_KEY] = this.saveNode(tree.root);
        return json;
    }

    saveNode(node) {
        const json = {};
        json[globalConfig.TITLE_KEY] = node.title;
        const children = [];
        for (const child of node.children) {
            children.push(this.saveNode(child));
        }
        json[globalConfig.CHILDREN_KEY] = children;
        console.log(json);
        return json;
    }
}