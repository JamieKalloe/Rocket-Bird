package net.kalloe.astroclash;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;
    private long asteroidStartTime;
    private long asteroidsElapsed;
    private GameThread thread;
    private Background bg;
    private Player player;
    ArrayList<Asteroid> asteroids;
    private Random rand = new Random();

    public GamePanel(Context context)
    {
        super(context);


        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new GameThread(getHolder(), this);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while(retry && counter < 1000)
        {
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry = false;

            }catch(InterruptedException e){e.printStackTrace();}
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65,25, 3);
        asteroids = new ArrayList<>();
        asteroidStartTime = System.nanoTime();
        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!player.getPlaying()) {
                player.setPlaying(true);
            } else {
                player.setUp(true);
            }
            return true;
        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update()
    {
        if(player.getPlaying()) {
            bg.update();
            player.update();

            //add asteroids on timer
            asteroidsElapsed = (System.nanoTime() - asteroidStartTime) / 1000000;

            //the higher the score, the less delay time for asteroids.
            if(asteroidsElapsed > (2000 - player.getScore() / 4)) {
                System.out.println("Created new asteroid");

                //first asteroids will be in the middle, the rest random spots
                if(asteroids.size() == 0) {
                    asteroids.add(new Asteroid(BitmapFactory.decodeResource(getResources(), R.drawable.missile), (WIDTH + 10), (HEIGHT / 2), 45, 15, player.getScore(), 13));
                } else {
                    asteroids.add(new Asteroid(BitmapFactory.decodeResource(getResources(), R.drawable.missile)
                            ,(WIDTH + 10), (int) (rand.nextDouble() * (HEIGHT)), 45, 15, player.getScore(), 13));
                }
                //reset the timer
                asteroidStartTime = System.nanoTime();
            }

            //loop through the asteroids, to check if the player collides.
            for(int i = 0; i < asteroids.size(); i++) {
                //update the asteroids
                asteroids.get(i).update();
                if(collision(asteroids.get(i), player)) {
                    asteroids.remove(i);
                    player.setPlaying(false);
                    break;
                }

                //remove old asteroids (off the screen)
                if(asteroids.get(i).getX() < -100) {
                    asteroids.remove(i);
                    break;
                }
            }
        }
    }

    public boolean collision(GameObject a, GameObject b) {
        if(Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }

        return false;
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        final float scaleFactorX = getWidth()/(WIDTH * 1.f);
        final float scaleFactorY = getHeight()/(HEIGHT * 1.f);

        if(canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);

            for(Asteroid asteroid : asteroids) {
                asteroid.draw(canvas);
            }

            canvas.restoreToCount(savedState);
        }
    }

}