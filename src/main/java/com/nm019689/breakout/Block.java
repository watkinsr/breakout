package com.nm019689.breakout;

import android.graphics.Rect;

/**
 * Represents a single game block object. Extends a ShapeDrawable to include a
 * Color value and a method for exporting the coordinates and color in order to
 * save its state.
 */
public class Block extends Entity {

    public Block(Rect rect, int color) {
        super(rect, color);
    }

}
