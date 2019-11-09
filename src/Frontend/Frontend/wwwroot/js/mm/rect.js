class Rect {
    constructor(leftTop, rightBottom) {
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
    }

    pointInRect(point) {
        return (point.x >= this.leftTop.x && point.x <= this.rightBottom.x)
            && (point.y >= this.leftTop.y && point.y <= this.rightBottom.y);
    }

    width() {
        return this.rightBottom.x - this.leftTop.x;
    }

    height() {
        return this.rightBottom.y - this.leftTop.y;
    }

    moveTo(x, y) {
        const width = this.width();
        const height = this.height();
        this.leftTop.x = x;
        this.leftTop.y = y;
        this.rightBottom.x = x + width;
        this.rightBottom.y = y + height;
    }

    getIntersectionArea(rect) {
        let RectLeftTopX = rect.leftTop.x;
        let RectLeftTopY = rect.leftTop.y;
        let RectRightBottomX = RectLeftTopX + rect.width();
        let RectRightBottomY = RectLeftTopY + rect.height();
        if (this.leftTop.x > RectLeftTopX) {
            RectLeftTopX = this.leftTop.x;
        }
        if (this.leftTop.y > RectLeftTopY) {
            RectLeftTopY = this.leftTop.y;
        }
        if (this.rightBottom.x < RectRightBottomX) {
            RectRightBottomX = this.rightBottom.x;
        }
        if (this.rightBottom.y < RectRightBottomY) {
            RectRightBottomY = this.rightBottom.y;
        }
        return (RectRightBottomX <= RectLeftTopX || RectRightBottomY <= RectLeftTopY) ? 0 : ((RectRightBottomX - RectLeftTopX) * (RectRightBottomY - RectLeftTopY));
    }

}
