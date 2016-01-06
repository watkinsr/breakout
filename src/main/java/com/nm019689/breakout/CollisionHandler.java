package com.nm019689.breakout;

import android.graphics.Rect;
import android.graphics.Canvas;

import java.util.ArrayList;

import java.lang.*;

import static java.lang.Thread.sleep;

public class CollisionHandler {
    public boolean powerupCollision = false;
    public ArrayList<Boolean> blockCollision = new ArrayList<Boolean>();
    public ArrayList<Boolean> topPaddleCollision = new ArrayList<Boolean>();
    public ArrayList<Boolean> bottomPaddleCollision = new ArrayList<Boolean>();
    public boolean topPaddleLastCollide;
    public boolean bottomPaddleLastCollide;
    public boolean powerupActive = false;
    private Rect mPaddle;
    long elapsedTime = 0;
    long startTime = 0;
    boolean startTimeChecked = true;

    CollisionHandler() {
        for (int i = 0; i < GameView.NUM_OF_BALLS; i++){
            topPaddleCollision.add(false);
            bottomPaddleCollision.add(false);
            blockCollision.add(false);
        }
    }

    public boolean checkPowerupCollision(Ball ball, ArrayList<Powerup> powerups, SoundManager soundManager, PowerupManager powerupManager) {
        int powerupListLength = powerups.size();
        ball.ballRect = ball.getBounds();
        int ballLeft = ball.ballRect.left + ball.velocityX;
        int ballRight = ball.ballRect.right + ball.velocityY;
        int ballTop = ball.ballRect.top + ball.velocityY;
        int ballBottom = ball.ballRect.bottom + ball.velocityY;

        // check collision; remove block if true
        for (int i = powerupListLength - 1; i >= 0; i--) {
            powerupCollision = false;
            Rect blockRect = powerups.get(i).getBounds();

            if (ballLeft >= blockRect.left - (ball.radius * 2)
                    && ballLeft <= blockRect.right + (ball.radius * 2)
                    && (ballTop == blockRect.bottom || ballTop == blockRect.top)) {
                startTimeChecked = false;
                powerupActive = true;
                getPowerup(powerupManager);
                powerupCollision = true;
                powerups.remove(i);

            } else if (ballRight <= blockRect.right
                    && ballRight >= blockRect.left
                    && ballTop <= blockRect.bottom && ballTop >= blockRect.top) {
                startTimeChecked = false;
                powerupActive = true;
                getPowerup(powerupManager);
                powerupCollision = true;
                powerups.remove(i);

            } else if (ballLeft >= blockRect.left
                    && ballLeft <= blockRect.right
                    && ballBottom <= blockRect.bottom
                    && ballBottom >= blockRect.top) {
                startTimeChecked = false;
                powerupActive = true;
                getPowerup(powerupManager);
                powerupCollision = true;
                powerups.remove(i);

            } else if (ballRight <= blockRect.right
                    && ballRight >= blockRect.left
                    && ballBottom <= blockRect.bottom
                    && ballBottom >= blockRect.top) {
                startTimeChecked = false;
                powerupActive = true;
                getPowerup(powerupManager);
                powerupCollision = true;
                powerups.remove(i);
            }

            if (powerupCollision) { //play the sound and reset to false
                if (soundManager.soundOn) {
                    soundManager.playPowerupSound();
                    //powerupCollision = false;
                }
                return true;
            }
        }
        return true;
    }

    private void getPowerup(PowerupManager powerupManager) {
        powerupCollision = true;
        powerupManager.setDoublePoints(true);
        checkRemovePowerup(powerupManager);
    }

    public void checkRemovePowerup(PowerupManager powerupManager) {
        if (!startTimeChecked) {
            startTime = System.currentTimeMillis();
        }

        if (powerupActive) {
            elapsedTime = System.currentTimeMillis() - startTime;
            startTimeChecked = true;
            System.out.println(elapsedTime);
        }

        if (elapsedTime > 5000) {
            powerupManager.stopAllPowerups();
            powerupActive = false;
            elapsedTime = 0;
            startTime = 0;
            startTimeChecked = true;
        }
    }

    /**
     * Checks for a ball collision with each block in an ArrayList. If there is
     * a collision, the point value of the block is added to a points total. If
     * sound is enabled, a sound effect will play on collision. If there is a
     * collision, blockCollision is set to true for the setVelocity method.
     *
     * @param blocks ArrayList of block objects
     * @return points total from blocks
     */

    public int checkBlocksCollision(Map map, Ball ball, ArrayList<Block> blocks, SoundManager soundManager, PowerupManager powerupManager, int ballIndex) {
        int points = 0;
        int blockListLength = blocks.size();
        ball.ballRect = ball.getBounds();

        int ballLeft = ball.ballRect.left + ball.velocityX;
        int ballRight = ball.ballRect.right + ball.velocityY;
        int ballTop = ball.ballRect.top + ball.velocityY;
        int ballBottom = ball.ballRect.bottom + ball.velocityY;

        // check collision; remove block if true
        for (int i = blockListLength - 1; i >= 0; i--) {
            Rect blockRect = blocks.get(i).getBounds();
            int color = blocks.get(i).getColor();

            if (ballLeft >= blockRect.left - (ball.radius * 2)
                    && ballLeft <= blockRect.right + (ball.radius * 2)
                    && (ballTop == blockRect.bottom || ballTop == blockRect.top)) {
                blockCollision.set(ballIndex, true);
                blocks.remove(i);
            } else if (ballRight <= blockRect.right
                    && ballRight >= blockRect.left
                    && ballTop <= blockRect.bottom && ballTop >= blockRect.top) {
                blockCollision.set(ballIndex, true);
                blocks.remove(i);
            } else if (ballLeft >= blockRect.left
                    && ballLeft <= blockRect.right
                    && ballBottom <= blockRect.bottom
                    && ballBottom >= blockRect.top) {
                blockCollision.set(ballIndex, true);
                blocks.remove(i);
            } else if (ballRight <= blockRect.right
                    && ballRight >= blockRect.left
                    && ballBottom <= blockRect.bottom
                    && ballBottom >= blockRect.top) {
                blockCollision.set(ballIndex, true);
                blocks.remove(i);
            }

            if (blockCollision.get(ballIndex)) {
                if (soundManager.soundOn) {
                    soundManager.playBlockSound();
                }
                if (powerupManager.getDoublePoints()) {
                    return 2 * (points += map.getPoints(color));
                } else
                    return points += map.getPoints(color);

            }
        }
        return points;
    }

    /**
     * Checks if the ball has collided with the paddle. Plays a sound effect if
     * there is a collision and sound is enabled.
     *
     * @param paddle paddle object
     * @return true if there is a collision
     */
    public boolean checkTopPaddleCollision(Ball ball, Paddle paddle, SoundManager soundManager, int ballIndex) {
        mPaddle = paddle.getBounds();
        ball.ballRect = ball.getBounds();

        if (ball.ballRect.left >= mPaddle.left - (ball.radius * 2)
                && ball.ballRect.right <= mPaddle.right + (ball.radius * 2)
                && ball.ballRect.bottom > mPaddle.bottom
                && ball.ballRect.top <= mPaddle.top + (ball.radius * 2)) {
            topPaddleCollision.set(ballIndex,  true);
            topPaddleLastCollide = true;
            bottomPaddleLastCollide = false;
            if (soundManager.soundOn && ball.velocityY < 0) {
                soundManager.playPaddleSound();
            }
        } else
            topPaddleCollision.set(ballIndex,  false);
        return topPaddleCollision.get(ballIndex);
    }

    public boolean checkBottomPaddleCollision(Ball ball, Paddle paddle, SoundManager soundManager, int ballIndex) {
        mPaddle = paddle.getBounds();
        ball.ballRect = ball.getBounds();

        if (ball.ballRect.left >= mPaddle.left - (ball.radius * 2)
                && ball.ballRect.right <= mPaddle.right + (ball.radius * 2)
                && ball.ballRect.bottom >= mPaddle.top - (ball.radius * 2)
                && ball.ballRect.top < mPaddle.bottom) {
            bottomPaddleCollision.set(ballIndex, true);
            bottomPaddleLastCollide = true;
            topPaddleLastCollide = false;
            if (soundManager.soundOn && ball.velocityY > 0) {
                soundManager.playPaddleSound();
            }
        } else
            bottomPaddleCollision.set(ballIndex, false);

        return bottomPaddleCollision.get(ballIndex);
    }

    /**
     * Updates the ball's coordinates. If there is a collision, the direction of
     * the ball's velocity is changed. Returns an integer depending on whether
     * the ball collides with the bottom of the screen. The return value is used
     * to decrement player turns.
     *
     * @return number to decrement player turns
     */
    public int setBallVelocity(Canvas canvas, Ball ball, SoundManager soundManager, PowerupManager powerupManager, int ballIndex) {
        int bottomHit = 0;
        int SCREEN_WIDTH = canvas.getWidth();
        int SCREEN_HEIGHT = canvas.getHeight();
        boolean firePowerupActivated = false;
        if (powerupManager.getBallOnFire()) {
            firePowerupActivated = true;
        } else

            if (blockCollision.get(ballIndex) && !firePowerupActivated) {
                ball.velocityY = -ball.velocityY;
                blockCollision.set(ballIndex, false); // reset
            } else if (blockCollision.get(ballIndex) && firePowerupActivated) {
                blockCollision.set(ballIndex, false); // reset
            }

        // paddle collision
        if (bottomPaddleCollision.get(ballIndex) && ball.velocityY > 0) {
            int paddleSplit = (mPaddle.right - mPaddle.left) / 4;
            int ballCenter = ball.ballRect.centerX();
            if (ballCenter < mPaddle.left + paddleSplit) {
                ball.velocityX = -(ball.radius * 3);
            } else if (ballCenter < mPaddle.left + (paddleSplit * 2)) {
                ball.velocityX = -(ball.radius * 2);
            } else if (ballCenter < mPaddle.centerX() + paddleSplit) {
                ball.velocityX = ball.radius * 2;
            } else {
                ball.velocityX = ball.radius * 3;
            }
            ball.velocityY = -ball.velocityY;
        } else if (topPaddleCollision.get(ballIndex) && ball.velocityY < 0) {
            int paddleSplit = (mPaddle.right - mPaddle.left) / 4;
            int ballCenter = ball.ballRect.centerX();
            if (ballCenter < mPaddle.left + paddleSplit) {
                ball.velocityX = -(ball.radius * 3);
            } else if (ballCenter < mPaddle.left + (paddleSplit * 2)) {
                ball.velocityX = -(ball.radius * 2);
            } else if (ballCenter < mPaddle.centerX() + paddleSplit) {
                ball.velocityX = ball.radius * 2;
            } else {
                ball.velocityX = ball.radius * 3;
            }
            ball.velocityY = -ball.velocityY;
        }


        // side walls collision
        if (ball.getBounds().right >= SCREEN_WIDTH) {
            ball.velocityX = -ball.velocityX;
        } else if (ball.getBounds().left <= 0) {
            ball.setBounds(0, ball.top, ball.radius * 2, ball.bottom);
            ball.velocityX = -ball.velocityX;
        }

        // screen top/bottom collisions
        if (ball.getBounds().top <= 0) {
            ball.velocityY = -ball.velocityY;
        } else if (ball.getBounds().top > SCREEN_HEIGHT) {
            bottomHit = 1; // lose a turn
            if (soundManager.soundOn) {
                soundManager.playBottomSound();
            }
            try {
                sleep(ball.getResetBallTimer());
                ball.initCoords(SCREEN_WIDTH, SCREEN_HEIGHT); // reset ball
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // move ball
        ball.left += ball.velocityX;
        ball.right += ball.velocityX;
        ball.top += ball.velocityY;
        ball.bottom += ball.velocityY;

        return bottomHit;
    }

}


