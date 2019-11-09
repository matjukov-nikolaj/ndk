class HelpModal {
    constructor(tree) {
        this.modalHelp = document.getElementById("modalHelp");
        this.modalContent = document.getElementById("modalContents");
        this.openInformation = document.getElementById("openInformation");
        this.closeInformation = document.getElementById("closeInformation");
        this._addOpenThemeButtonClickHandler();
    }

    _addOpenThemeButtonClickHandler() {
        const openSelectTheme = new Modal(this.modalHelp, this.modalContent, this.openInformation, this.closeInformation);
    };
}