package com.nm019689.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

/**
 * Represents the game paddle. Extends a ShapeDrawable to include a color value and
 * methods for setting the paddle's location on screen.
 */
public class Paddle extends ShapeDrawable {

    // paddle dimensions
    private int left;
    private int right;
    private int top;
    private int bottom;
    private static final int paddle_width = Breakout.SCREEN_WIDTH / 10;
    private static final int paddle_height = Breakout.SCREEN_WIDTH / 72;
    private static final int paddle_offset = Breakout.SCREEN_HEIGHT / 6; // bottom screen offset
    private static final int paddle_move_offset = Breakout.SCREEN_WIDTH / 15; // move paddle to touch event speed


    /**
     * Constructor. Calls the constructor of the super class and sets color value.
     */
    public Paddle() {
        super(new RectShape());
        this.getPaint().setColor(Color.WHITE);
    }

    /**
     * Sets the paddle's dimensions and coordinates based on screen width and
     * height.
     *
     */
    public void initCoords(boolean topPaddle) {

        left = (Breakout.SCREEN_WIDTH / 2) - paddle_width;
        right = (Breakout.SCREEN_WIDTH / 2) + paddle_width;
        if (topPaddle) {
            top = (Breakout.SCREEN_HEIGHT / 4 - paddle_offset) - paddle_height;
            bottom = (Breakout.SCREEN_HEIGHT / 4 - paddle_offset) + paddle_height;
        } else {
            top = (Breakout.SCREEN_HEIGHT - paddle_offset) - paddle_height;
            bottom = (Breakout.SCREEN_HEIGHT - paddle_offset) + paddle_height;
        }
    }

    /**
     * Draws the paddle to a canvas.
     *
     * @param canvas graphics canvas
     */
    public void drawPaddle(Canvas canvas) {
        this.setBounds(left, top, right, bottom);
        this.draw(canvas);
    }

    /**
     * Sets the paddle's coordinates. The paddle may move across the screen
     * along the x axis only.
     *
     * @param x x axis value of touch event. Used to calculate the center of the paddle.
     */
    public void movePaddle(int x) {
        if (x >= left && x <= right) {
            left = x - paddle_width;
            right = x + paddle_width;
        } else if (x > right) {
            left += paddle_move_offset;
            right += paddle_move_offset;
        } else if (x < left) {
            left -= paddle_move_offset;
            right -= paddle_move_offset;
        }

        // keep paddle from going off screen left
        if (left < 0) {
            left = 0;
            right = paddle_width * 2;
        }

        // keep paddle from going off screen right
        if (right > Breakout.SCREEN_WIDTH) {
            right = Breakout.SCREEN_WIDTH;
            left = Breakout.SCREEN_WIDTH - (paddle_width * 2);
        }
    }
}
