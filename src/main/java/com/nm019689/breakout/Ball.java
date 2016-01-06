package com.nm019689.breakout;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;


/**
 * Represents a game ball. Responsible for drawing the ball, updating the ball's
 * position, collision checking, sound events, and point scoring.
 */
public class Ball extends ShapeDrawable {

    // ball dimensions
    public int left;
    public int right;
    public int top;
    public int bottom;
    public int radius;

    // ball speed
    public int velocityX;
    public int velocityY;

    // timer when ball hits screen bottom
    private final int resetBallTimer = 1000;

    public Rect ballRect;


    /**
     * Constructor. Sets the Paint parameters.
     */
    public Ball() {
        super(new OvalShape());
        this.getPaint().setColor(Color.CYAN);
    }

    public int getResetBallTimer() {
        return resetBallTimer;
    }

    /**
     * Initializes ball parameters. Calculates the ball's dimensions according
     * to the screen's width and height. Sets a starting velocity. Randomly
     * chooses whether the ball moves right or left at start.
     *
     * @param width  screen width
     * @param height screen height
     */
    public void initCoords(int width, int height) {
        Random rnd = new Random(); // starting x velocity direction
        int SCREEN_WIDTH = width;
        int SCREEN_HEIGHT = height;
        radius = SCREEN_WIDTH / 72;
        velocityX = radius;
        velocityY = radius * 2;

        // ball coordinates
        left = (SCREEN_WIDTH / 2) - radius;
        right = (SCREEN_WIDTH / 2) + radius;
        top = (SCREEN_HEIGHT / 2) - radius;
        bottom = (SCREEN_HEIGHT / 2) + radius;

        int startingXDirection = rnd.nextInt(2); // random beginning direction
        if (startingXDirection > 0) {
            velocityX = -velocityX;
        }
    }

    /**
     * Draws ball to canvas.
     *
     * @param canvas graphical canvas
     */
    public void drawBall(Canvas canvas) {
        this.setBounds(left, top, right, bottom);
        this.draw(canvas);
    }

    /**
     * Returns the current Y velocity value of the ball.
     *
     * @return current Y velocity of the ball
     */
    public int getVelocityY() {
        return velocityY;
    }
}
