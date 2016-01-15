package net.kalloe.astroclash;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by Jamie on 15-1-2016.
 */
public class Asteroid extends GameObject {

    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Asteroid(Bitmap img, int x, int y, int w, int h, int s, int numberOfFrames) {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;

        speed = 7 + (int) (rand.nextDouble()*score / 30);

        //cap asteroid speed
        if(speed > 42) {
            speed = 42;
        }

        Bitmap[] image = new Bitmap[numberOfFrames];
        spritesheet = img;

        for(int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i*height, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100-speed);
    }

    public void update() {
        x -= speed;
        animation.update();
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getWidth() {
        //offset slightly more, for realistic cllision detection
        return (width - 10);
    }
}
