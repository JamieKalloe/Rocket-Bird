package net.kalloe.astroclash;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Jamie on 15-1-2016.
 */
public class Player extends GameObject {
    private Bitmap spritesheet;
    private int score;
    private boolean up;
    private boolean playing;
    private Animation animation = new Animation();
    private long startTime;

    public Player(Bitmap image, int w, int h, int numberOfFrames) {
        x = 100;
        y = GamePanel.HEIGHT/2;
        dy = 0;
        score = 0;
        this.height = h;
        this.width = w;

        Bitmap[] playerImage = new Bitmap[numberOfFrames];
        spritesheet = image;

        for(int i = 0; i < playerImage.length; i++) {
            playerImage[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }

        animation.setFrames(playerImage);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void update() {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if(elapsed > 100) {
            score++;
            startTime = System.nanoTime();
        }

        animation.update();

        if(up) {
            dy -= 1;
        } else {
            dy += 1;
        }

        if(dy > 14) {
            dy = 14;
        }

        if(dy < -14) {
            dy = -14;
        }

        y += dy*2;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }

    public int getScore() {
        return this.score;
    }

    public boolean getPlaying() {
        return this.playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void resetDYA() {
        dy = 0;
    }

    public void resetScore() {
        this.score = 0;
    }
}
