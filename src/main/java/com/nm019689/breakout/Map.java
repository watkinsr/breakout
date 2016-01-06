package com.nm019689.breakout;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;


public class Map {
    public ArrayList<Block> blocksList;
    public ArrayList<Powerup> powerupList;
    private int levelCount;
    private int levelXAmount = 0;
    private int levelYAmount = 0;
    private int levelBlockXOffset = 0;
    private int levelBlockYOffset = 0;
    private ArrayList<int[]> blockCoords = new ArrayList<int[]>();
    private ArrayList<int[]> powerupCoords = new ArrayList<int[]>();
    private String LEVEL_FILE;

    /**
     * Initializes blocks. Measures the width and height of the canvas for the
     * dimensions and coordinates of the blocks. Sets the color depending on the
     * block's row. Adds the block to an ArrayList.
     *
     * @param canvas graphics canvas
     */

    public void initLevel(Context context, Canvas canvas, int p_level_count) {
        levelCount = p_level_count;
        loadLevelFromFile(context, canvas);
    }

    /**
     * Loads text files containing block coordinates depending on level count
     * Stores each individual block coordinate pair into a block array element
     */
    private void loadLevelFromFile(Context context, Canvas canvas) {
        switch (levelCount) {
            case 1:
                LEVEL_FILE = "level1.csv";
                break;
            case 2:
                LEVEL_FILE = "level2.csv";
                break;
            case 3:
                LEVEL_FILE = "level3.csv";
                break;
        }
        readLevelCSVFile(context);
        initBlocksFromLevelFile(canvas);

    }

    private void readLevelCSVFile(Context context) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean firstLine = true;
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(LEVEL_FILE);
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] values = line.split(cvsSplitBy);
                if (firstLine) {
                    levelXAmount = Integer.parseInt(values[0]);
                    levelYAmount = Integer.parseInt(values[1]);
                    levelBlockXOffset = Integer.parseInt(values[2]);
                    levelBlockYOffset = Integer.parseInt(values[3]);
                    firstLine = false;
                } else {
                    if (Integer.parseInt(values[2]) == 1) {
                        powerupCoords.add(new int[]{Integer.parseInt(values[0]), Integer.parseInt(values[1])});
                    } else
                        blockCoords.add(new int[]{Integer.parseInt(values[0]), Integer.parseInt(values[1])});
                }

            }
            String temp = "";

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    /**
     * Restores a saved ArrayList of blocks. Reads through an ArrayList of
     * integer Arrays. Passes the values to construct a block and adds the block
     * to an ArrayList.
     *
     * @param arr ArrayList of integer arrays containing the coordinates and
     *            color of the saved blocks.
     */
    public void restoreBlocks(ArrayList<int[]> arr) {
        for (int i = 0; i < arr.size(); i++) {
            Rect r = new Rect();
            int[] blockNums = arr.get(i);
            r.set(blockNums[0], blockNums[1], blockNums[2], blockNums[3]);
            Block b = new Block(r, blockNums[4]);
            blocksList.add(b);
        }
    }

    public void initBlocksFromLevelFile(Canvas canvas) {
        int blockHeight = canvas.getWidth() / 36;
        int spacing = canvas.getWidth() / 144;
        int blockWidth = (canvas.getWidth() / 10) - spacing;
        int blockScaleX = (blockWidth + spacing) / levelBlockXOffset;
        int blockScaleY = (blockHeight + spacing) / levelBlockYOffset;
        int y = 0;
        int j = 0;
        int remainder;
        for (int i = 0; i < blockCoords.size(); i++) {
            if (i != 1 && i != 0) {
                 remainder = i % levelYAmount;
                if ((remainder) == 0) {
                    y++;
                }
            }

            int x_coordinate = blockCoords.get(i)[0] * blockScaleX;
            int y_coordinate = blockCoords.get(i)[1] * blockScaleY;
            Rect r = new Rect();
            r.set(x_coordinate, y_coordinate, x_coordinate + blockWidth,
                    y_coordinate + blockHeight);

            int color;

            color = dertBlockColor(y);

            for (int pupindx = 0; pupindx < powerupCoords.size(); pupindx++) {
                if (blockCoords.get(i)[0] == powerupCoords.get(pupindx)[0] && blockCoords.get(i)[1] == powerupCoords.get(pupindx)[1]) {
                    color = Color.WHITE;
                    powerupList.add(new Powerup(r, color));
                } else {
                    blocksList.add(new Block(r, color));
                    break;
                }
                j++;
            }
        }
    }

    private int dertBlockColor(int y){
        if (y < 2)
            return Color.RED;
        else if (y < 4)
            return Color.YELLOW;
        else if (y < 6)
            return Color.GREEN;
        else if (y < 8)
            return Color.MAGENTA;
        else
            return Color.LTGRAY;
    }

    /**
     * Draws blocks to screen
     *
     * @param canvas graphical canvas
     */
    public void drawBlocks(Canvas canvas) {
        for (int i = 0; i < blocksList.size(); i++) {
            blocksList.get(i).drawEntity(canvas);
        }
    }

    /**
     * Returns the point value of a block based on color.
     *
     * @param color block color
     * @return point value of block
     */
    public int getPoints(int color) {
        int points = 0;
        if (color == Color.LTGRAY)
            points = 100;
        else if (color == Color.MAGENTA)
            points = 200;
        else if (color == Color.GREEN)
            points = 300;
        else if (color == Color.YELLOW)
            points = 400;
        else if (color == Color.RED)
            points = 500;

        return points;
    }

    public void drawPowerups(Canvas canvas) {
        for (int i = 0; i < powerupList.size(); i++) {
            powerupList.get(i).drawEntity(canvas);
        }
    }

}
