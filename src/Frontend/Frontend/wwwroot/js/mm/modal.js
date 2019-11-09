class Modal {
    constructor(overlay, content, openButton, closeButton) {
        this.overlay = overlay;
        this.content = content;
        this.openButton = openButton;
        this.openButton.onclick = () => {
            this._showModal(this.overlay, this.content);
        };
        this.closeButton = closeButton;
    }

    hideModal(overlay, content) {
        overlay.classList.remove("open_modal_window");
        content.classList.remove("open_modal_content");
    }

    _showModal(overlay, content) {
        overlay.classList.add("open_modal_window");
        content.classList.add("open_modal_content");
        this.closeButton.onclick = () => {
            this.hideModal(this.overlay, this.content);
        }
    }
}