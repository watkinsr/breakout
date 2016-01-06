package com.nm019689.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.OvalShape;

public abstract class Entity extends ShapeDrawable {

    private Paint paint;
    private int entityColor;

    private int radius;
    private Rect ballRect;
    private boolean stopDrawing = false;

    public Entity(Rect rect, int color) {
        super(new RectShape());
        this.setBounds(rect);
        paint = new Paint();
        paint.setColor(color);
        entityColor = color;
    }

    public void setStopDrawing(boolean val) {
        stopDrawing = val;
    }

    public int getColor() {
        return paint.getColor();
    }

    /**
     * Returns an integer array containing the color and coordinates of the
     * block. Used to save a entity's state to a data file. The first four values
     * represent the entity's coordinates. The last value is the entity's color.
     *
     * @return integer array containing the entity's coordinates and color values.
     */

    public void drawEntity(Canvas canvas) {
        if (!stopDrawing) canvas.drawRect(this.getBounds(), paint);
    }

    public int[] toIntArray() {
        int[] arr = {this.getBounds().left, this.getBounds().top,
                this.getBounds().right, this.getBounds().bottom, entityColor};
        return arr;
    }
}
