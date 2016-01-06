package com.nm019689.breakout;

public class PowerupManager {
    private boolean ballOnFire = false;
    private boolean doublePoints = false;

    public boolean getBallOnFire() {
        return ballOnFire;
    }

    public boolean getDoublePoints() {
        return doublePoints;
    }

    public void setBallOnFire(boolean val) {
        ballOnFire = val;
    }

    public void setDoublePoints(boolean val) {
        doublePoints = val;
    }

    public void stopAllPowerups() {
        ballOnFire = false;
        doublePoints = false;
    }
}
