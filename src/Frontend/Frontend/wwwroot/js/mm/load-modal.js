class LoadModal {
    constructor(tree) {
        this.tree = tree;
        this.modalLoad = document.getElementById("modalLoad");
        this.modalLoadFile = document.getElementById("modalLoadFile");
        this.openButton = document.getElementById("openLoad");
        this.closeButton = document.getElementById("closeLoad");
        this.onLoadTree = () => {};
        this.modal = new Modal(this.modalLoad, this.modalLoadFile, this.openButton, this.closeButton);
        this._addLoadButtonClickHandler();
    }

    _addLoadButtonClickHandler() {
        const loadButton = document.getElementById('loadButton');
        loadButton.onclick = () => {
            const loadFile = document.getElementById('files').files[0];
            const fileData = new FileReader(loadFile);
            const file = fileData.readAsText(loadFile);
            this._onLoadData(fileData);
        }
    }

    _onLoadData(file) {
        file.onload = () => {
            try {
                const string = file.result;
                const json = JSON.parse(string);
                const loader = new TreeLoader();
                const tree = loader.load(json);
                this.tree = tree;
                this.onLoadTree(tree);
                this.modal.hideModal(this.modalLoad, this.modalLoadFile);
            } catch (e) {
                alert("Invalid json");
            }
        };
    }
}