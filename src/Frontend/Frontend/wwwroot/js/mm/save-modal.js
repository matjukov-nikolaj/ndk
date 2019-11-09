class SaveModal {
    constructor(tree) {
        this.tree = tree;
        this.modalSave = document.getElementById("modalSave");
        this.modalInput = document.getElementById("modalInput");
        this.closeSave = document.getElementById("closeSave");
        this.openSave = document.getElementById("openDownload");
        this.modal = new Modal(this.modalSave, this.modalInput, this.openSave, this.closeSave);
        this._addSaveButtonClickHandler();
    }

    _addSaveButtonClickHandler() {
        const saveButton = document.getElementById("saveButton");
        saveButton.onclick = () => {
            const inputSaver = document.getElementById("input_save");
            let inputValue = inputSaver.value;
            const saver = new TreeSaver();
            const json = saver.save(this.tree);
            const blob = new Blob([JSON.stringify(json)], {type: 'application/json'});
            if (inputValue) {
                saveAs(blob, inputValue + ".json");
                inputSaver.value = '';
            } else {
                saveAs(blob, "mind-map.json");
            }
            this.modal.hideModal(this.modalSave, this.modalInput);
        }
    }
}