package net.kalloe.astroclash;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
    private long smokeStartTime;
    private long missileStartTime;
    private GameThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<TopBorder> topborder;
    private ArrayList<BotBorder> botborder;
    private Random rand = new Random();
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    private boolean newGameCreated;
    //increase to slow down difficulty progression, decrease to speed up difficulty progression
    private int progressDenom = 20;
    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean dissapear;
    private boolean started;
    private int best;
    private SharedPrefManager prefManager;
    private Typeface fontDescription, fontTitle;


    public GamePanel(Context context)
    {
        super(context);


        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);


        //make gamePanel focusable so it can handle events
        setFocusable(true);

        this.prefManager = new SharedPrefManager(getContext());
        this.best = prefManager.get(SharedPrefManager.PREF_BEST_SCORE);

        //load fonts
        this.fontDescription = Typeface.createFromAsset(getContext().getAssets(), "fonts/korean_caligraphy.ttf");
        this.fontTitle = Typeface.createFromAsset(getContext().getAssets(), "fonts/chinese_takeaway.ttf");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while(retry && counter<1000)
        {
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;

            }catch(InterruptedException e){e.printStackTrace();}

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.material_landscape));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.material_bird), 99, 66, 3);
        asteroids = new ArrayList<Asteroid>();
        topborder = new ArrayList<TopBorder>();
        botborder = new ArrayList<BotBorder>();
        smokeStartTime=  System.nanoTime();
        missileStartTime = System.nanoTime();

        thread = new GameThread(getHolder(), this);


        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.getPlaying())
            {
                player.setPlaying(true);
                player.setUp(true);
            }
            else
            {
                player.setUp(true);
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
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

            //calculate the threshold of height the border can have based on the score
            //max and min border heart are updated, and the border switched direction when either max or
            //min is met

            maxBorderHeight = 30+player.getScore()/progressDenom;
            //cap max border height so that borders can only take up a total of 1/2 the screen
            if(maxBorderHeight > HEIGHT/4)maxBorderHeight = HEIGHT/4;
            minBorderHeight = 5+player.getScore()/progressDenom;

            //add missiles on timer
            long missileElapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missileElapsed >(2000 - player.getScore()/4)){


                //first missile always goes down the middle
                if(asteroids.size()==0)
                {
                    asteroids.add(new Asteroid(BitmapFactory.decodeResource(getResources(),R.drawable.
                            material_ninja_stars),WIDTH + 10, HEIGHT/2, 45, 15, player.getScore(), 13));
                }
                else
                {

                    asteroids.add(new Asteroid(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT - (maxBorderHeight * 2))+maxBorderHeight),45,15, player.getScore(),13));
                }

                //reset timer
                missileStartTime = System.nanoTime();
            }

            if(player.getY() <= -60 || player.getY() >= 500) {
                System.out.println("The player went off the map");
                try {
                    Thread.sleep(750);
                }

                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                finally {
                    player.setPlaying(false);
                }
            }

            //loop through every missile and check collision and remove
            for(int i = 0; i<asteroids.size();i++)
            {
                //update missile
                asteroids.get(i).update();

                if(collision(asteroids.get(i),player))
                {
                    try {
                        Thread.sleep(750);
                    }

                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    finally {
                        asteroids.remove(i);
                        player.setPlaying(false);
                    }
                    break;
                }
                //remove missile if it is way off the screen
                if(asteroids.get(i).getX()<-100)
                {
                    asteroids.remove(i);
                    break;
                }
            }
        }
        else{
            newGameCreated = false;
            if(!newGameCreated) {
                newGame();
            }
        }
    }
    public boolean collision(GameObject a, GameObject b)
    {
        if(Rect.intersects(a.getRectangle(), b.getRectangle()))
        {
            return true;
        }

        return false;
    }
    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas!=null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);

            //draw missiles
            for(Asteroid m: asteroids)
            {
                m.draw(canvas);
            }

            if(player.getScore() > best) {
                final int currentScore = player.getScore();
                final int oldBest = (best + 100);
                if(currentScore < oldBest && best != 0)
                    drawMessage(canvas, "NEW HIGHSCORE!");
            }

            drawText(canvas);
            canvas.restoreToCount(savedState);
        }
    }

    public void newGame()
    {
        if(player.getScore() > best) {
            try {
                best = player.getScore();
                prefManager.add(SharedPrefManager.PREF_BEST_SCORE, best);
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }

        botborder.clear();
        topborder.clear();
        asteroids.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.resetDYA();
        player.resetScore();
        player.setY(HEIGHT/2);

        newGameCreated = true;
    }

    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(33);
        paint.setTypeface(this.fontDescription);

        canvas.drawText("DISTANCE: " + player.getScore(), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + best, WIDTH - 175, HEIGHT - 10, paint);

        if(!player.getPlaying())
        {
            Paint paint1 = new Paint();
            paint1.setTextSize(55);
            paint1.setTypeface(this.fontTitle);
            canvas.drawText("Shuriken Bird", WIDTH / 2 - 70, HEIGHT / 2, paint1);

            Paint paint2 = new Paint();
            paint2.setTextSize(30);
            paint2.setTypeface(this.fontDescription);
            canvas.drawText("Press and hold to go up", WIDTH/2-70, HEIGHT/2 + 30, paint2);
            canvas.drawText("Release to go down", WIDTH/2-70, HEIGHT/2 + 60, paint2);
        }
    }

    public void drawMessage(Canvas canvas, String message) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
//        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTypeface(this.fontDescription);

        canvas.drawText(message, (WIDTH / 2) - 120, (HEIGHT / 2) - 207, paint);
    }
}