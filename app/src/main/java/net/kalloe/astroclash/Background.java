package net.kalloe.astroclash;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Jamie on 14-1-2016.
 */
public class Background {

    //Variables
    private Bitmap image;
    private int x, y, dx;

    public Background(Bitmap image) {
        this.image = image;
        dx = GamePanel.MOVESPEED;
    }

    public void update() {
        x += dx;
        if(x < GamePanel.WIDTH) {
            x = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
        if(x < 0) {
            canvas.drawBitmap(image, (x + GamePanel.WIDTH), y, null);
        }
    }
}
