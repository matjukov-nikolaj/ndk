class TreeController {
    constructor(tree, renderer) {
        this.tree = tree;
        this.renderer = renderer;
        this.selection = {
            curr: this.tree.root,
            prev: null
        };
        this.scrollController = new ScrollController(renderer);
        this.canvas = document.getElementById('canvas');
        this.canvasBlock = document.getElementById('canvasDiv');
        this.input = null;
        this._addEventHandlers();
    }

    setTree(tree) {
        this.tree = tree;
        this.selection = {
            curr: this.tree.root,
            prev: null
        };
        this.renderer.setTree(this.tree);
        this._redrawingTree();
    }

    _changeSelection(newSelection) {
        this.selection.prev = this.selection.curr;
        this.selection.curr = newSelection;
    }

    _redrawingTree() {
        const oldSize = {
            width: this.renderer.canvasSize.width,
            height: this.renderer.canvasSize.height,
        };
        this.renderer.drawAllTree(this.selection);
        if (oldSize.width != this.renderer.canvasSize.width || oldSize.height != this.renderer.canvasSize.height) {
            this.canvas.width = this.renderer.canvasSize.width;
            this.canvas.height = this.renderer.canvasSize.height;
            this.scrollController.scrollCanvas({
                x: 0,
                y: this.renderer.canvasSize.height - oldSize.height
            });
            this.renderer.drawAllTree(this.selection);
        }
    }

    _onPressedLeft() {
        if (this.input) {
            return;
        }
        if (this.selection.curr.parent) {
            this._changeSelection(this.selection.curr.parent);
            this._redrawingTree();
        }
    }

    _onPressedRight() {
        if (this.input) {
            return;
        }
        if (this.selection.curr.children.length != 0) {
            this._changeSelection(this.selection.curr.children[0]);
            console.log(this.selection);

            this._redrawingTree();
        }
    }

    _onPressedUp() {
        if (this.selection.curr.parent) {
            const parent = this.selection.curr.parent;
            const children = parent.children;
            const index = children.indexOf(this.selection.curr);
            const isFirstChild = 0 == index;
            if (!isFirstChild) {
                this._changeSelection(children[index - 1]);
                this._redrawingTree();
            }
        }
    }

    _onPressedDown() {
        if (this.selection.curr.parent) {
            const parent = this.selection.curr.parent;
            const children = parent.children;
            const index = children.indexOf(this.selection.curr);
            const isLastChild = children.length - 1 == index;
            if (!isLastChild) {
                this._changeSelection(children[index + 1]);
                this._redrawingTree();
            }
        }
    }

    _onPressedTab() {
        let parent = this.selection.curr.parent;
        let level= 0;
        while (parent) {
            ++level;
            parent = parent.parent;
        }
        if (level >= globalConfig.MAX_DEPTH){
            return;
        }
        const childCounter = this.selection.curr.children.length;
        let nodeName = "";
        if (this.selection.curr == this.tree.root) {
            nodeName = globalConfig.MAIN_NAME + (childCounter + 1);
        } else {
            nodeName = globalConfig.SUB_NAME + (childCounter + 1)
        }
        const newNode = this.selection.curr.addChild(nodeName);
        if (newNode) {
            this._changeSelection(newNode);
            this._redrawingTree();
        }
    }

    _onPressedDel() {
        if (this.input) {
            this.selection.curr.title = this.input.value;
            return;
        }
        if (this.selection.curr == this.tree.root) {
            return;
        }
        this._changeSelection(this.selection.curr.parent);
        const index = this.selection.curr.children.indexOf(this.selection.prev);
        this.selection.curr.children.splice(index, 1);
        this._redrawingTree();
    }

    _onPressedF2() {
        this._createInput();
        const rect = this.renderer.getNodeRect(this.selection.curr);
        this.input.value = this.selection.curr.title;
        this.input.select();
        this._changeInputStyle(rect.leftTop, rect.width(), rect.height());
        this.input.focus();
    }

    _addKeyDownHandler() {
        let self = this;
        window.addEventListener("keydown", function (event) {
                switch (event.code) {
                    case "ArrowLeft":
                        self._onPressedLeft();
                        break;
                    case "ArrowUp":
                        self._closeInput();
                        self._onPressedUp();
                        break;
                    case "ArrowRight":
                        self._onPressedRight();
                        break;
                    case "ArrowDown":
                        self._closeInput();
                        self._onPressedDown();
                        break;
                    case "Tab":
                        self._closeInput();
                        self._onPressedTab();
                        event.preventDefault();
                        break;
                    case "Enter":
                        self._closeInput();
                        event.preventDefault();
                        break;
                    case "Delete":
                        self._onPressedDel();
                        break;
                    case "F2":
                        self._onPressedF2();
                        break;
                }
            }
        );
    }

    _onClick(point) {
        this._closeInput();
        const result = this.renderer.findNodeByPoint(point);
        if (result) {
            this.selection.curr = result.node;
            this._redrawingTree();
        }
    }

    _addMouseClickHandler() {
        this.canvas.onclick = (e) => {
            const offset = new Point(this.canvas.getBoundingClientRect().left, this.canvas.getBoundingClientRect().top);
            const currentPoint = new Point(e.clientX - offset.x, e.clientY - offset.y);
            this._onClick(currentPoint);
        }
    }

    _addMouseDownHandler() {
        this.canvasBlock.addEventListener('mousedown', (e) => {
            const offset = new Point(this.canvas.getBoundingClientRect().left, this.canvas.getBoundingClientRect().top);
            const checkPoint = new Point(e.clientX - offset.x, e.clientY - offset.y);
            const nodeWidthRect = this.renderer.findNodeByPoint(checkPoint);
            if (nodeWidthRect && nodeWidthRect.node != this.tree.root)
            {
                this.renderer.startDrag(nodeWidthRect);
            }
        }, false);
    }

    _addMouseMoveHandler() {
        this.canvasBlock.addEventListener('mousemove', (e) => {
            if (this.renderer.dragInfo) {
                const offset = new Point(this.canvas.getBoundingClientRect().left, this.canvas.getBoundingClientRect().top);
                const point = new Point(e.clientX - offset.x, e.clientY - offset.y);
                this.renderer.onDrag(point);
                this._redrawingTree();
            }
        }, false);
    }

    _addMouseUpHandler() {
        this.canvasBlock.addEventListener('mouseup', (e) => {
            if (this.renderer.dragInfo)
            {
                this.renderer.onDragEnd();
                this._redrawingTree();
            }
        }, false);
    }

    _closeInput() {
        if (this.input) {
            this.selection.curr.title = this.input.value;
            this._redrawingTree();
            this.canvasBlock.removeChild(this.input);
            this.input = null;
        }
    }

    _changeInputStyle(position, width, height) {
        this.input.style.display = 'block';
        this.input.style.left = position.x + 'px';
        this.input.style.top = (position.y + (height - this.input.offsetHeight) / 2) + 'px';
        this.input.style.opacity = '1';
        this.input.style.width = width + 'px';
    }

    _showInput(point) {
        this._createInput();
        const rect = point.rect;
        this.selection.curr = point.node;
        this.input.value = this.selection.curr.title;
        this.input.select();
        this._changeInputStyle(rect.leftTop, rect.width(), rect.height());
        this.input.focus();
    }

    _createInput() {
        this._closeInput();
        this.input = document.createElement('input');
        this.input.setAttribute("id", "input_block");
        this.canvasBlock.appendChild(this.input);
    }

    _addCanvasDoubleClickHandler() {
        let self = this;
        this.canvas.ondblclick = function (e) {
            const offset = new Point(this.getBoundingClientRect().left, this.getBoundingClientRect().top);
            const currentPoint = new Point(e.clientX - offset.x, e.clientY - offset.y);
            const checkPoint = self.renderer.findNodeByPoint(currentPoint);
            if (checkPoint) {
                self._showInput(checkPoint);
            }
        }
    }

    _addEventHandlers() {
        this._addKeyDownHandler();
        this._addMouseClickHandler();
        this._addCanvasDoubleClickHandler();
        this._addMouseDownHandler();
        this._addMouseMoveHandler();
        this._addMouseUpHandler();
    }
}