package com.nm019689.breakout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
//TODO: num of balls determined from map.csv
/**
 * Creates and draws the graphics for the game. Runs a Thread for animation and
 * game physics. Saves and restores game data when paused or restored.
 */
public class GameView extends SurfaceView implements Runnable {
    private boolean showGameOverBanner = false;
    private int levelCompleted = 0;
    private int playerTurns;
    private static final int PLAYER_START_TURNS = 3;
    public static final int NUM_OF_BALLS = 2;
    private static final String playerTurnsText = "TURNS = ";
    private static final String FILE_PATH = "data/data/com.dhbikoff.breakout/data.dat";
    private static final int frameRate = 33;
    private static final int startTimer = 66;
    private Paint turnsPaint;
    private int startNewGame;
    private ObjectOutputStream oos;
    private boolean topHalfTouched = false;
    private boolean bottomHalfTouched = false;
    private float topEventX;
    private float bottomEventX;
    private SurfaceHolder holder;
    private Thread gameThread = null;
    private boolean running = false;
    private Canvas canvas;
    private boolean checkSize = true;
    private boolean newGame = true;
    private int waitCount = 0;
    private SoundManager soundManager;
    private CollisionHandler collisionHandler;
    private PowerupManager powerupManager;
    private Map map;
    private ArrayList<Ball> balls;
    private Paddle player2Paddle;
    private Paddle player1Paddle;
    private static final String getReady = "GET READY...";
    private Paint getReadyPaint;
    private int player1Points = 0;
    private int player2Points = 0;
    private Paint scorePaint;
    private final String score = "SCORE = ";

    /**
     * Constructor. Sets sound state and new game signal depending on the
     * incoming intent from the Breakout class. Instantiates the ball, blocks,
     * and paddle. Sets up the Paint parameters for drawing text to the screen.
     *
     * @param context       Android Context
     * @param launchNewGame start new game or load save game
     * @param sound         sound on/off
     */
    public GameView(Context context, int launchNewGame, boolean sound) {
        super(context);

        startNewGame = launchNewGame; // new game or continue
        playerTurns = PLAYER_START_TURNS;
        powerupManager = new PowerupManager();
        soundManager = new SoundManager();
        soundManager.soundOn = sound;
        soundManager.initSounds(context);
        collisionHandler = new CollisionHandler();
        holder = getHolder();
        balls = new ArrayList<Ball>();
        for (int i = 0; i < NUM_OF_BALLS; i++) {
            balls.add(new Ball());
        }
        player1Paddle = new Paddle();
        player2Paddle = new Paddle();
        map = new Map();
        map.blocksList = new ArrayList<Block>();
        map.powerupList = new ArrayList<Powerup>();

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(25);

        turnsPaint = new Paint();
        turnsPaint.setTextAlign(Paint.Align.RIGHT);
        turnsPaint.setColor(Color.WHITE);
        turnsPaint.setTextSize(25);

        getReadyPaint = new Paint();
        getReadyPaint.setTextAlign(Paint.Align.CENTER);
        getReadyPaint.setColor(Color.WHITE);
        getReadyPaint.setTextSize(45);
    }

    /**
     * Runs the game thread. Sets the frame rate for drawing graphics. Acquires
     * a canvas for drawing. If no blocks exist, initialize game objects. Moves
     * the paddle according to touch events. Checks for collisions as the ball's
     * moves. Keeps track of player turns and ends the game when turns run out.
     * Awards the player an extra turn when a level is completed. Draws text to
     * the screen showing player score and turns remaining. Draws text to
     * announce when the game begins or ends.
     */
    public void run() {
        while (running) {
            try {
                Thread.sleep(frameRate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.BLACK);

                if (map.blocksList.size() == 0) {
                    checkSize = true;
                    newGame = true;
                    levelCompleted++;
                }

                if (checkSize) {
                    initObjects(canvas);
                    checkSize = false;
                    // extra turn for finished level
                    if (levelCompleted > 1) {
                        playerTurns++;
                    }
                }

                handlePlayerTouch();

                drawToCanvas(canvas);

                // pause screen on new game
                if (newGame) {
                    waitCount = 0;
                    newGame = false;
                }
                waitCount++;

                engine(canvas, waitCount);
                drawPointsAndTurns();
                holder.unlockCanvasAndPost(canvas); // release canvas
            }
        }
    }

    private void drawPointsAndTurns() {
        String printPlayer1Score = "P1: " + score + player1Points;
        String printPlayer2Score = "P2: " + score + player2Points;
        String turns = playerTurnsText + playerTurns;
        canvas.drawText(printPlayer1Score, 0, 25, scorePaint);
        canvas.drawText(printPlayer2Score, 300, 25, scorePaint);
        canvas.drawText(turns, canvas.getWidth(), 25, turnsPaint);
    }

    private void handlePlayerTouch() {
        if (bottomHalfTouched) {
            player1Paddle.movePaddle((int) bottomEventX);
            System.out.println("moving player1 paddle..");
        }
        if (topHalfTouched) {
            player2Paddle.movePaddle((int) topEventX);
            System.out.println("moving player2 paddle..");
        }
    }

    /**
     * Draws graphics to the screen.
     *
     * @param canvas graphics canvas
     */
    private void drawToCanvas(Canvas canvas) {
        map.drawBlocks(canvas);
        map.drawPowerups(canvas);
        player1Paddle.drawPaddle(canvas);
        player2Paddle.drawPaddle(canvas);
        for (int i = 0; i < NUM_OF_BALLS; i++) {
            balls.get(i).drawBall(canvas);
        }
    }

    /**
     * Pauses the animation until the wait counter is satisfied. Sets the
     * velocity and coordinates of the ball. Checks for collisions. Checks if
     * the game is over. Draws text to alert the user if the ball restarts or
     * the game is over.
     *
     * @param canvas graphics canvas
     * @param waitCt number of frames to pause the game
     */
    private void engine(Canvas canvas, int waitCt) {
        if (waitCount > startTimer) {
            showGameOverBanner = false;
            for (int i = 0; i < NUM_OF_BALLS; i++) {
                playerTurns -= collisionHandler.setBallVelocity(canvas, balls.get(i), soundManager, powerupManager, i);
            }

            collisionHandler.checkRemovePowerup(powerupManager);
            if (playerTurns < 0) {
                showGameOverBanner = true;
                gameOver(canvas);
                return;
            }
            // paddle collision
            for (int i = 0; i < NUM_OF_BALLS; i++) {
                if (!collisionHandler.checkBottomPaddleCollision(balls.get(i), player1Paddle, soundManager, i))
                    collisionHandler.checkTopPaddleCollision(balls.get(i), player2Paddle, soundManager, i);
                collisionHandler.checkPowerupCollision(balls.get(i), map.powerupList, soundManager, powerupManager);
                // block collision and points tally
                if (collisionHandler.bottomPaddleLastCollide) {
                    player1Points += collisionHandler.checkBlocksCollision(map, balls.get(i), map.blocksList, soundManager, powerupManager, i);
                } else {
                    player2Points += collisionHandler.checkBlocksCollision(map, balls.get(i), map.blocksList, soundManager, powerupManager, i);
                }
            }
        } else {
            if (showGameOverBanner) {
                getReadyPaint.setColor(Color.RED);
                canvas.drawText("GAME OVER!!!", canvas.getWidth() / 2,
                        (canvas.getHeight() / 2) - (balls.get(0).getBounds().height())
                                - 50, getReadyPaint);
            }
            getReadyPaint.setColor(Color.WHITE);
            canvas.drawText(getReady, canvas.getWidth() / 2,
                    (canvas.getHeight() / 2) - (balls.get(0).getBounds().height()),
                    getReadyPaint);

        }
    }


    /**
     * Resets variables to signal a new game. Deletes the remaining blocks list.
     * When the run function sees the blocks list is empty, it will initialize a
     * new game board.
     *
     * @param canvas graphics canvas
     */
    private void gameOver(Canvas canvas) {
        levelCompleted = 0;
        player1Points = 0;
        player2Points = 0;
        playerTurns = PLAYER_START_TURNS;
        map.blocksList.clear();
    }

    /**
     * Initializes graphical objects. Restores game state if an existing game is
     * continued.
     *
     * @param canvas graphical canvas
     */
    private void initObjects(Canvas canvas) {
        topHalfTouched = false; // reset paddle location
        bottomHalfTouched = false;
        for (int i = 0; i < NUM_OF_BALLS; i++) {
            balls.get(i).initCoords(canvas.getWidth(), canvas.getHeight());
        }
        player1Paddle.initCoords(false); //boolean is for specifying top paddle
        player2Paddle.initCoords(true);
        if (startNewGame == 0) {
            restoreGameData();
        } else {
            System.out.println("New level being loaded...");
            map.initLevel(getContext(), canvas, levelCompleted);
        }
    }


    /**
     * Opens a saved game file and reads in data to restore saved game state.
     */
    private void restoreGameData() {
        try {
            FileInputStream fis = new FileInputStream(FILE_PATH);
            ObjectInputStream ois = new ObjectInputStream(fis);
            player1Points = ois.readInt(); // restore p1 points
            player2Points = ois.readInt(); // restore p2 points
            playerTurns = ois.readInt(); // restore player turns
            @SuppressWarnings("unchecked")
            ArrayList<int[]> arr = (ArrayList<int[]>) ois.readObject();
            map.restoreBlocks(arr); // restore blocks
            arr.clear();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        startNewGame = 1; // only restore once
    }

    /**
     * Saves game state. Reads block color and coordinates into an ArrayList.
     * Saves blocks, player points, and player turns into a data file.
     */
    private void saveGameData() {
        ArrayList<int[]> arr = new ArrayList<int[]>();

        for (int i = 0; i < map.blocksList.size(); i++) {
            arr.add(map.blocksList.get(i).toIntArray());
        }

        try {
            FileOutputStream fos = new FileOutputStream(FILE_PATH);
            oos = new ObjectOutputStream(fos);
            oos.writeInt(player1Points);
            oos.writeInt(player2Points);
            oos.writeInt(playerTurns);
            oos.writeObject(arr);
            arr.clear();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves game data and destroys Thread.
     */
    public void pause() {
        saveGameData();
        running = false;
        while (true) {
            try {
                gameThread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        gameThread = null;
        soundManager.close();
    }

    /**
     * Resumes the game. Starts a new game Thread.
     */
    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Overridden Touch event listener. Reads screen touches to move the paddle.
     * {@inheritDoc}
     *
     * @param event screen touch event
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN
                || event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            int index = MotionEventCompat.getActionIndex(event);
            int bottomIndex = 0;
            int topIndex = 0;
            float eventY;
            //multi touch event
            if (event.getPointerCount() > 1) {
                if ((int) MotionEventCompat.getY(event, 0) >= Breakout.SCREEN_HEIGHT / 2)
                    topIndex = 1;
                else
                    bottomIndex = 1;
                System.out.println("Multitouch event");
                bottomEventX = (int) MotionEventCompat.getX(event, bottomIndex);
                topEventX = (int) MotionEventCompat.getX(event, topIndex);
                bottomHalfTouched = true;
                topHalfTouched = true;
                return true;
            }

            //single touch event
            else {
                eventY = event.getY();
                if (eventY >= (Breakout.SCREEN_HEIGHT / 2)) {
                    bottomEventX = (int) MotionEventCompat.getX(event, index);
                    bottomHalfTouched = true;
                    topHalfTouched = false;
                    return bottomHalfTouched;

                } else {
                    topEventX = (int) MotionEventCompat.getX(event, index);
                    topHalfTouched = true;
                    bottomHalfTouched = false;
                    return topHalfTouched;
                }
            }
        }
        return false;
    }
}
