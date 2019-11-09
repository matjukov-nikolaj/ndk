class TreeRenderer {
    constructor(tree) {
        this.canvas = document.getElementById('canvas');
        this.canvas.focus();
        this.ctx = this.canvas.getContext("2d");
        this.canvas.width = globalConfig.MIN_CANVAS_WIDTH;
        this.canvas.height = globalConfig.MIN_CANVAS_HEIGHT;
        this.tree = tree;
        this.treeWithRects = null;
        this.dragInfo = null;
        this.canvasSize = {
            width: this.canvas.width,
            height: this.canvas.height,
        };
    }

    setTree(tree) {
        this.tree = tree;
        this.treeWithRects = null;
    }

    startDrag(nodeWithRect) {
        let isSubsection = true;
        for (const child of this.tree.root.children) {
            if (child == nodeWithRect.node) {
                isSubsection = false;
                break;
            }
        }
        this.dragInfo = {
            node: nodeWithRect.node,
            position: null,
            isSubsection: isSubsection,
            rect: nodeWithRect.rect,
            intersectedNode: null,
        }
    }

    onDrag(position) {
        this.dragInfo.position = position;
        const newX = position.x - this.dragInfo.rect.width() / 2;
        const newY = position.y - this.dragInfo.rect.height() / 2;
        this.dragInfo.rect.moveTo(newX, newY);
        this.dragInfo.intersectedNode = null;
        this.getIntersectedNode(this.treeWithRects, 0);
    }

    onDragEnd() {
        if (this.dragInfo.intersectedNode) {
            const node = this.dragInfo.node;
            const index = node.parent.children.indexOf(node);
            node.parent.children.splice(index, 1);
            this.dragInfo.intersectedNode.children.push(node);
            node.parent = this.dragInfo.intersectedNode;
        }
        this.dragInfo = null;
    }

    getIntersectedNode(nodeWithRect, bestArea) {
        if (!this.allowIntersection(nodeWithRect.node)) {
            return 0;
        }
        let area = nodeWithRect.rect.getIntersectionArea(this.dragInfo.rect);
        if (area > bestArea && nodeWithRect) {
            this.dragInfo.intersectedNode = nodeWithRect.node;
            bestArea = area;
        }
        for (const child of nodeWithRect.children) {
            this.getIntersectedNode(child, bestArea);
        }
    }

    allowIntersection(node) {
        let parentNode = node;
        while (parentNode) {
            if (parentNode == this.dragInfo.node) {
                return false;
            }
            parentNode = parentNode.parent;
        }
        return true;
    }

    getNodeRect(node) {
        return this.getChildNodeRect(node, this.treeWithRects);
    }

    getChildNodeRect(node, child) {
        if (child.node == node) {
            return child.rect;
        }
        let result = null;
        for (let treeChild of child.children) {
            const found = this.getChildNodeRect(node, treeChild);
            if (found) {
                result = found;
                break;
            }
        }
        return result;

    }

    findNodeByPoint(point) {
        return this.findChildWithPoint(point, this.treeWithRects);
    }

    findChildWithPoint(point, nodeWithRect) {
        if (nodeWithRect.rect.pointInRect(point)) {
            return {
                node: nodeWithRect.node,
                rect: nodeWithRect.rect,
            };
        }
        let result = null;
        for (let node of nodeWithRect.children) {
            const found = this.findChildWithPoint(point, node);
            if (found) {
                result = found;
                break;
            }
        }
        return result;
    }

    drawAllTree(selection) {
        this._clearCanvas();
        this._drawRoot(selection);
        this._drawMainChild(selection);
        this._drawTransparentElement();
    }

    _drawTransparentElement() {
        if (this.dragInfo && this.dragInfo.position) {
            const rect = this.dragInfo.rect;
            if (this.dragInfo.isSubsection) {
                this._drawElementLine(rect.leftTop, rect.width(), rect.height(), this.dragInfo.node.title, true)
            } else {
                this._drawElementRect(rect.leftTop, rect.width(), rect.height(), this.dragInfo.node.title, true);
            }
        }
    }

    _getFontSettings() {
        this.ctx.font = globalConfig.FONT_SETTINGS;
        this.ctx.textAlign = globalConfig.FONT_TEXT_ALIGN;
    }

    _getTextWidth(text, min, max) {
        this._getFontSettings();
        return Math.min(Math.max(this.ctx.measureText(text).width + globalConfig.MARGIN_FOR_TEXT * 2, min), max);
    }

    _drawRectFrame(leftTop, width, height) {
        this.ctx.lineTo(leftTop.x + width, leftTop.y);
        this.ctx.lineTo(leftTop.x + width, leftTop.y + height);
        this.ctx.lineTo(leftTop.x, leftTop.y + height);
        this.ctx.lineTo(leftTop.x, leftTop.y);
    }

    _drawElementRect(leftTop, width, height, title, isTransparent) {
        this.ctx.beginPath();
        this.ctx.fillStyle = isTransparent ? rendererMainConfig.BACKGROUND_COLOR_OPACITY : rendererMainConfig.BACKGROUND_COLOR;
        this.ctx.fillRect(leftTop.x, leftTop.y, width, height);
        this.ctx.fillStyle = colorConfig.SELECT_ELEMENT;
        this._getFontSettings();
        this.ctx.fillText(title, leftTop.x + (width / 2), leftTop.y + (height + globalConfig.FONT_SIZE / 2) / 2);
        this.ctx.fill();
        this.ctx.moveTo(leftTop.x, leftTop.y);
        this._drawRectFrame(leftTop, width, height);
        this.ctx.strokeStyle = isTransparent ? colorConfig.FRAME_COLOR_OPACITY : colorConfig.FRAME_COLOR;
        if (leftTop.x == rendererRootConfig.leftTop.x) {
            this.ctx.lineWidth = rendererRootConfig.LINE_WIDTH;
        } else {
            this.ctx.lineWidth = rendererMainConfig.LINE_WIDTH;
        }
        this.ctx.stroke();
        this.ctx.closePath();
    }

    _drawElementLine(leftTop, width, height, title, isTransparent) {
        this.ctx.beginPath();
        this.ctx.fillStyle = isTransparent ? rendererSubConfig.BACKGROUND_COLOR_OPACITY : rendererSubConfig.BACKGROUND_COLOR;
        this.ctx.fillRect(leftTop.x, leftTop.y, width, height);
        this.ctx.fillStyle = colorConfig.SELECT_ELEMENT;
        this._getFontSettings();
        this.ctx.fillText(title, leftTop.x + (width / 2), leftTop.y + (height + globalConfig.FONT_SIZE / 2) / 2);
        this.ctx.fill();
        this.ctx.moveTo(leftTop.x, leftTop.y + height);
        this.ctx.lineTo(leftTop.x + width, leftTop.y + height);
        this.ctx.strokeStyle = isTransparent ? colorConfig.FRAME_COLOR_OPACITY : colorConfig.FRAME_COLOR;
        this.ctx.lineWidth = rendererSubConfig.LINE_WIDTH;
        this.ctx.stroke();
        this.ctx.closePath();
    }

    _drawOutline(leftTop, width, height, draggingFrame) {
        this.ctx.beginPath();
        let outlineInfo = null;
        outlineInfo = this._getOutlineInfo(leftTop, width, height, globalConfig.INDENT_FROM_FRAME);
        this.ctx.beginPath();
        this.ctx.fill();
        this.ctx.moveTo(outlineInfo.leftTop.x, outlineInfo.leftTop.y);
        this._drawRectFrame(outlineInfo.leftTop, outlineInfo.width, outlineInfo.height);
        this.ctx.strokeStyle = draggingFrame ? colorConfig.INSERT_NODE : colorConfig.SELECT_ELEMENT;
        this.ctx.lineWidth = 2;
        this.ctx.stroke();
        this.ctx.closePath();
    }

    _getOutlineInfo(leftTop, width, height, indent) {
        const newPoint = new Point(
            leftTop.x - indent,
            leftTop.y - indent
        );
        width += indent * 2;
        height += indent * 2;
        return {
            leftTop: newPoint,
            width: width,
            height: height,
        }
    }

    _drawSelection(rect, selection, node) {
        if (node == selection.curr) {
            const intersectedNode = false;
            this._drawOutline(rect.leftTop, rect.width(), rect.height(), intersectedNode);
        }
    }

    _getRootWidth() {
        const rootWidth = this._getTextWidth(this.tree.root.title, rendererRootConfig.MIN_WIDTH, Number.POSITIVE_INFINITY);
        return rootWidth;
    }

    _drawRoot(selection) {
        const leftTop = new Point(rendererRootConfig.leftTop.x, (this.canvasSize.height - rendererRootConfig.HEIGHT) / 2);
        const rootWidth = this._getRootWidth();
        const rightBottom = new Point(leftTop.x + rootWidth, leftTop.y + rendererRootConfig.HEIGHT);
        const rootRect = new Rect(leftTop, rightBottom);
        this.treeWithRects = {
            node: this.tree.root,
            rect: rootRect,
            children: [],
        };
        this._drawElementRect(leftTop, rootWidth, rendererRootConfig.HEIGHT, this.tree.root.title);
        this._drawSelection(rootRect, selection, this.tree.root);
        if (this.dragInfo && this.dragInfo.intersectedNode == this.tree.root) {
            this._drawOutline(leftTop, rootWidth, rendererRootConfig.HEIGHT, true);
        }
    }

    _drawConnection(start, end) {
        this.ctx.beginPath();
        this.ctx.moveTo(start.x, start.y);
        this.ctx.lineTo(end.x, end.y);
        this.ctx.strokeStyle = colorConfig.FRAME_COLOR;
        this.ctx.lineWidth = 2;
        this.ctx.stroke();
        this.ctx.closePath();
    }

    _drawMainChild(selection) {
        const rootRect = this.treeWithRects.rect;
        const rightRootCenter = new Point(rootRect.leftTop.x + this._getRootWidth(), rootRect.leftTop.y + rendererRootConfig.HEIGHT / 2);
        const children = this.tree.root.children;
        let connectFrom = null;
        if (children.length > 0) {
            connectFrom = new Point(rightRootCenter.x + rendererRootConfig.INDENT, rightRootCenter.y);
            const startConnectFromRoot = new Point(rightRootCenter.x, rightRootCenter.y);
            this._drawConnection(startConnectFromRoot, connectFrom);
        }
        const fullHeight = this._getMainSectionsHeight(children);
        const startPoint = new Point(
            rendererMainConfig.DISTANCE + rightRootCenter.x - rendererMainConfig.MIN_WIDTH / 2,
            rightRootCenter.y - fullHeight / 2);
        const elementSettings = {
            elements: children,
            selection: selection,
            startPoint: startPoint,
            currentNode: this.treeWithRects,
            config: rendererMainConfig,
            connectionPoint: connectFrom,
            drawElementFn: (point, elementWidth, height, title) => this._drawElementRect(point, elementWidth, height, title),
            getMarginTopFn: (children) => this._getMainMarginTop(children),
            getMarginBottomFn: (children) => this._getMainMarginBottom(children),
            calculateConnectionYFn: (pointY) => pointY + rendererMainConfig.HEIGHT / 2,
        };
        this._drawTreeElement(elementSettings);
    };

    _drawSubsections(bottomPoint, subsections, selection, connectionPoint, currentNode) {
        if (subsections.length != 0) {
            const fullHeight = this._getSubsectionsHeight(subsections);
            const x = bottomPoint.x + rendererSubConfig.DISTANCE;
            const y = bottomPoint.y - rendererSubConfig.HEIGHT / 2;
            const startPoint = new Point(x, y - fullHeight / 2);
            const elementSettings = {
                elements: subsections,
                selection: selection,
                startPoint: startPoint,
                currentNode: currentNode,
                config: rendererSubConfig,
                connectionPoint: connectionPoint,
                drawElementFn: (point, elementWidth, height, title) => this._drawElementLine(point, elementWidth, height, title),
                getMarginTopFn: (children) => this._getSubsectionMarginTop(children),
                getMarginBottomFn: (children) => this._getSubsectionMarginBottom(children),
                calculateConnectionYFn: (pointY) => pointY + rendererSubConfig.HEIGHT,
            };
            this._drawTreeElement(elementSettings);
        }
    }

    _drawTreeElement(elementSettings) {
        const elements = elementSettings.elements,
            selection = elementSettings.selection,
            startPoint = elementSettings.startPoint,
            currentNode = elementSettings.currentNode,
            config = elementSettings.config,
            connectionPoint = elementSettings.connectionPoint,
            drawElementFn = elementSettings.drawElementFn,
            getMarginTopFn = elementSettings.getMarginTopFn,
            getMarginBottomFn = elementSettings.getMarginBottomFn,
            calculateConnectionYFn = elementSettings.calculateConnectionYFn;
        let topOffset = 0;
        for (let i = 0; i < elements.length; i++) {
            const element = elements[i];
            const elementWidth = this._getTextWidth(element.title, config.MIN_WIDTH, Number.POSITIVE_INFINITY);
            if (i != 0) {
                topOffset += getMarginTopFn(element.children);
            }
            const title = element.title;
            const point = new Point(startPoint.x, startPoint.y + topOffset);
            const rightBottom = new Point(point.x + elementWidth, point.y + config.HEIGHT);
            if (rightBottom.x > this.canvasSize.width) {
                this.canvasSize.width = rightBottom.x + globalConfig.CANVAS_INDENT;
            }
            if (point.y < 0) {
                this.canvasSize.height -= point.y + globalConfig.CANVAS_INDENT;
            }
            if (point.y + config.HEIGHT > this.canvasSize.height) {
                this.canvasSize.height = point.y + config.HEIGHT + globalConfig.CANVAS_INDENT;
            }
            const elementRect = new Rect(point, rightBottom);
            const newNode = {
                node: element,
                rect: elementRect,
                children: [],
            };
            currentNode.children.push(newNode);
            const connectionEndPoint = new Point(point.x, calculateConnectionYFn(point.y));
            this._drawConnection(connectionPoint, connectionEndPoint);
            const elementConnection = new Point(point.x + elementWidth + config.INDENT, calculateConnectionYFn(point.y));
            if (element.children.length > 0) {
                const connectionStartPoint = new Point(point.x, calculateConnectionYFn(point.y));
                this._drawConnection(connectionStartPoint, elementConnection);
            }
            drawElementFn(point, elementWidth, config.HEIGHT, title);
            this._drawSelection(elementRect, selection, element);
            if (this.dragInfo && this.dragInfo.intersectedNode == element) {
                this._drawOutline(point, elementWidth, config.HEIGHT, true);
            }
            this._drawSubsections(elementConnection, element.children, selection, elementConnection, newNode);
            const subBottom = getMarginBottomFn(element.children);
            topOffset += subBottom + config.HEIGHT + config.MARGIN * 2;
        }
    }

    _clearCanvas() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    }

    _getSubsectionMarginTop(subsections) {
        let marginTop = 0;
        if (subsections.length > 0) {
            marginTop += Math.max(0, this._calculateMargin(subsections, rendererSubConfig.HEIGHT));
            marginTop += this._getSubsectionMarginTop(subsections[0].children);
        }
        return marginTop;
    }

    _getSubsectionMarginBottom(subsections) {
        let marginBottom = 0;
        if (subsections.length > 0) {
            marginBottom += Math.max(0, this._calculateMargin(subsections, rendererSubConfig.HEIGHT));
            marginBottom += this._getSubsectionMarginBottom(subsections[subsections.length - 1].children);
        }
        return marginBottom;
    }

    _getSubsectionsHeight(subsections) {
        let height = 0;
        for (let i = 0; i < subsections.length; ++i) {
            const subsection = subsections[i];
            height += rendererSubConfig.HEIGHT;
            if (i != 0) {
                height += this._getSubsectionMarginTop(subsection.children);
                height += rendererSubConfig.MARGIN;
            }
            if (i != subsections.length - 1) {
                const bottom = this._getSubsectionMarginBottom(subsection.children);
                height += bottom;
                height += rendererSubConfig.MARGIN;
            }
        }
        return height;
    }

    _calculateMargin(subsections, elementHeight) {
        return (this._getSubsectionsHeight(subsections) - elementHeight) / 2
    }

    _getMainMarginTop(subsections) {
        let marginTop = 0;
        if (subsections.length > 0) {
            marginTop += Math.max(0, this._calculateMargin(subsections, this._getMainSectionSize()));
            marginTop += this._getSubsectionMarginTop(subsections[0].children);
            marginTop += rendererMainConfig.MARGIN;
        }
        return marginTop;
    }

    _getMainMarginBottom(subsections) {
        let marginBottom = 0;
        if (subsections.length > 0) {
            marginBottom += Math.max(0, this._calculateMargin(subsections, this._getMainSectionSize()));
            marginBottom += this._getSubsectionMarginBottom(subsections[subsections.length - 1].children);
            marginBottom += rendererMainConfig.MARGIN;
        }
        return marginBottom;
    }

    _getMainSectionsHeight(mainSections) {
        let height = 0;
        for (let i = 0; i < mainSections.length; ++i) {
            const mainSection = mainSections[i];
            height += rendererMainConfig.HEIGHT;
            if (i != 0) {
                height += this._getMainMarginTop(mainSection.children);
                height += rendererMainConfig.MARGIN;
            }
            if (i != mainSections.length - 1) {
                height += this._getMainMarginBottom(mainSection.children);
                height += rendererMainConfig.MARGIN;
            }
        }
        return height;
    }

    _getSubsectionSize() {
        return rendererSubConfig.HEIGHT + rendererSubConfig.MARGIN * 2;
    }

    _getMainSectionSize() {
        return rendererMainConfig.HEIGHT + rendererMainConfig.MARGIN * 2;
    }
}
