package net.kalloe.astroclash;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Jamie on 14-1-2016.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    //Variables
    private GameThread thread;
    private Background background;
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static int MOVESPEED = -5;

    public GamePanel(Context context) {
        super(context);

        //Add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new GameThread(getHolder(), this);

        //Make the GamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        //Start the game loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void update() {
        background.update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final float scaleFactorX = (float) getWidth() / (WIDTH * 1.f);
        final float scaleFactorY = (float) getHeight() / (HEIGHT * 1.f);

        if(canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            background.draw(canvas);
            canvas.restoreToCount(savedState);
        }
    }
}
