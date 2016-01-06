package com.nm019689.breakout;

import android.media.AudioManager;
import android.media.SoundPool;
import android.content.Context;

public class SoundManager {
    public boolean soundOn;
    public SoundPool soundPool;
    public int paddleSoundId;
    public int blockSoundId;
    public int bottomSoundId;
    public int powerupSoundId;

    public void initSounds(Context context) {
        if (soundOn) {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            paddleSoundId = soundPool.load(context, R.raw.paddle, 0);
            blockSoundId = soundPool.load(context, R.raw.block, 0);
            bottomSoundId = soundPool.load(context, R.raw.bottom, 0);
            powerupSoundId = soundPool.load(context, R.raw.powerup2, 0);
        }
    }

    public void playBottomSound() {
        this.soundPool.play(bottomSoundId, 1, 1, 1, 0, 1);
    }

    public void playPaddleSound() {
        soundPool.play(paddleSoundId, 1, 1, 1, 0, 1);
    }

    public void playBlockSound() {
        soundPool.play(blockSoundId, 1, 1, 1, 0, 1);
    }

    public void playPowerupSound() {
        soundPool.play(powerupSoundId, 1, 1, 1, 0, 1);
    }

    /**
     * Releases sound assets.
     */
    public void close() {
        if (soundOn) {
            soundPool.release();
            soundPool = null;
        }
    }
}
