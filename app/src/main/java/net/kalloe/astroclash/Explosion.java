package net.kalloe.astroclash;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Jamie on 16-1-2016.
 */
public class Explosion {

    private int x, y, width, height, row;
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Explosion(Bitmap img, int x, int y, int w, int h, int numberOfFrames) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;

        Bitmap[] image = new Bitmap[numberOfFrames];

        this.spritesheet = img;

        for(int i = 0; i < image.length; i++) {
            if(i % 5 == 0 && i > 0) {
                image[i] = Bitmap.createBitmap(spritesheet, (i - (5 * row)) * width, row * height, width, height);
            }
        }

        animation.setFrames(image);
        animation.setDelay(10);
    }

    public void draw(Canvas canvas) {
        if(!animation.hasPlayed()) {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }
    }

    public void update() {
        if(!animation.hasPlayed()) {
            animation.update();
        }
    }

    public int getHeight() {
        return this.height;
    }

}
