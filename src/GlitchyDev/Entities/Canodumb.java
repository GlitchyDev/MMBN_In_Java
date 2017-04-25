package GlitchyDev.Entities;

import GlitchyDev.States.DamageType;
import GlitchyDev.States.MegamanStates;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import GlitchyDev.States.CanodumbStates;
import GlitchyDev.States.EnemyType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Robert on 4/19/2017.
 */
public class Canodumb extends Enemy {
    private long stateStartTime;
    private long resumeStateTime;
    private CanodumbStates currentState;

    private MediaPlayer mediaplayer;

    private int cursorX = -1;
    private boolean tookShot = false;


    private MediaPlayer soundEffects;

    public Canodumb(int X, int Y, int enemyID) {
        super(X, Y, enemyID);
        enemyType = EnemyType.Canodumb;
        currentState = CanodumbStates.Canodumb_Spawning_In;
        stateStartTime = System.nanoTime();
        isdead = false;
        doRenderHeath = false;
        setCurrentHealth(50);



    }

    @Override
    public void doEnemy(GraphicsContext gc, Canvas canvas, HashMap<String,Image> sprites, ArrayList<Enemy> enemies, Megaman megaman, ArrayList<Tile> tiles)
    {
        switch(currentState) {
            case Canodumb_Spawning_In:
                // The blend time
                final double spawnInTime = 0.25;
                // The time inbetween each Enemy spawning
                final double inBetweenTime = 0.3;
                // How long from the creation of map it should wait
                double waitTime = 0.4;

                if (enemyID != 1) {
                    waitTime += (spawnInTime + inBetweenTime) * (enemyID - 1);
                }

                if(getProgressedTime() >= waitTime)
                {
                    if( getProgressedTime() <= spawnInTime + waitTime) {
                        if (soundEffects == null) {
                            Media sound = new Media(new File("GameData/Sounds/Enemy_Appears_Effect.wav").toURI().toString());
                            soundEffects = new MediaPlayer(sound);
                            soundEffects.play();
                        }
                        if(!doRenderHeath) {
                            doRenderHeath = true;
                        }
                        gc.setGlobalAlpha((getProgressedTime() - waitTime) / spawnInTime);
                    }
                    else {
                        gc.setGlobalAlpha(1.0);
                    }
                    double canodumbWidth = 2 * sprites.get("Canodumb_Idle").getWidth();
                    double canodumbHeight = 2 * sprites.get("Canodumb_Idle").getHeight();

                    gc.drawImage(sprites.get("Canodumb_Idle"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, canodumbWidth, canodumbHeight);

                }
                break;
            case Canodumb_Idle:
                    double canodumbWidth = 2 * sprites.get("Canodumb_Idle").getWidth();
                    double canodumbHeight = 2 * sprites.get("Canodumb_Idle").getHeight();
                    gc.drawImage(sprites.get("Canodumb_Idle"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, canodumbWidth, canodumbHeight);

                    if(megaman.getY() == Y && !paused && megaman.getCurrentState() != MegamanStates.Megaman_dying)
                    {
                        currentState = CanodumbStates.Canodumb_Pre_Shoot;
                        stateStartTime = System.nanoTime();
                        pauseStateTime = System.nanoTime();
                        resumeStateTime = System.nanoTime();
                        cursorX = 0;
                    }

                break;
            case Canodumb_Pre_Shoot:
                canodumbWidth = 2 * sprites.get("Canodumb_Idle").getWidth();
                canodumbHeight = 2 * sprites.get("Canodumb_Idle").getHeight();
                gc.drawImage(sprites.get("Canodumb_Idle"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, canodumbWidth, canodumbHeight);



                if(getProgressedTime() > cursorX * 0.3) {
                    cursorX++;
                    if (megaman.getX() == X - cursorX && megaman.getY() == Y) {
                        currentState = CanodumbStates.Canodumb_Shoot;
                        stateStartTime = System.nanoTime();
                        pauseStateTime = System.nanoTime();
                        resumeStateTime = System.nanoTime();
                        tookShot = false;
                    } else {
                        if (X - cursorX <= -1) {
                            currentState = CanodumbStates.Canodumb_Idle;
                            stateStartTime = System.nanoTime();
                            pauseStateTime = System.nanoTime();
                            resumeStateTime = System.nanoTime();
                            cursorX = 0;
                        }
                    }
                }
                if (cursorX != 0) {
                    double cursorWidth = sprites.get("Canodumb_Cursor").getWidth() * 2;
                    double cursorHeight = sprites.get("Canodumb_Cursor").getHeight() * 2;
                    gc.drawImage(sprites.get("Canodumb_Cursor"), getTileUpperRightX(X - cursorX), getTileUpperRightY(Y), cursorWidth, cursorHeight);
                }


                break;
            case Canodumb_Shoot:


                if(getProgressedTime() < 0.1)
                {
                    canodumbWidth = 2 * sprites.get("Canodumb_Shoot_1").getWidth();
                    canodumbHeight = 2 * sprites.get("Canodumb_Shoot_1").getHeight();
                    gc.drawImage(sprites.get("Canodumb_Shoot_1"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, canodumbWidth, canodumbHeight);
                    double cursorWidth = sprites.get("Canodumb_Cursor").getWidth() * 2;
                    double cursorHeight = sprites.get("Canodumb_Cursor").getHeight() * 2;
                    gc.drawImage(sprites.get("Canodumb_Cursor"), getTileUpperRightX(X - cursorX), getTileUpperRightY(Y), cursorWidth, cursorHeight);
                }
                else
                {
                    if(getProgressedTime() < 0.2)
                    {
                        canodumbWidth = 2 * sprites.get("Canodumb_Shoot_2").getWidth();
                        canodumbHeight = 2 * sprites.get("Canodumb_Shoot_2").getHeight();
                        gc.drawImage(sprites.get("Canodumb_Shoot_2"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, canodumbWidth, canodumbHeight);
                    }
                    else
                    {
                        if(getProgressedTime() < 0.3)
                        {
                            canodumbWidth = 2 * sprites.get("Canodumb_Shoot_3").getWidth();
                            canodumbHeight = 2 * sprites.get("Canodumb_Shoot_3").getHeight();
                            gc.drawImage(sprites.get("Canodumb_Shoot_3"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, canodumbWidth, canodumbHeight);

                            if(!tookShot) {
                                if( Y == megaman.getY())
                                megaman.takeDamage(10);
                                tookShot = true;
                                Media sound = new Media(new File("GameData/Sounds/Cannon.wav").toURI().toString());
                                mediaplayer = new MediaPlayer(sound);
                                mediaplayer.play();
                            }
                        }
                        else
                        {
                            canodumbWidth = 2 * sprites.get("Canodumb_Idle").getWidth();
                            canodumbHeight = 2 * sprites.get("Canodumb_Idle").getHeight();
                            gc.drawImage(sprites.get("Canodumb_Idle"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, canodumbWidth, canodumbHeight);
                        }
                    }



                }
                if(getProgressedTime() > 2.0)
                {
                    currentState = CanodumbStates.Canodumb_Idle;
                    stateStartTime = System.nanoTime();
                    pauseStateTime = System.nanoTime();
                    resumeStateTime = System.nanoTime();
                    cursorX = 0;
                }



                break;
            case Canodumb_Dying:
                gc.setGlobalAlpha(Math.sin(getProgressedTime() * Math.PI * 10.0));
                canodumbWidth = 2 * sprites.get("Canodumb_Idle").getWidth();
                canodumbHeight = 2 * sprites.get("Canodumb_Idle").getHeight();
                gc.drawImage(sprites.get("Canodumb_Idle"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, canodumbWidth, canodumbHeight);


                double explosionWidth;
                double explosionHeight;


                gc.setGlobalAlpha(1.0);
                if(getProgressedTime() < 0.1)
                {
                    explosionWidth = 2 * sprites.get("Enemy_Delete_1").getWidth();
                    explosionHeight = 2 * sprites.get("Enemy_Delete_1").getHeight();
                    gc.drawImage(sprites.get("Enemy_Delete_1"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, explosionWidth, explosionHeight);
                }
                else
                {
                    if(getProgressedTime() < 0.2)
                    {
                        explosionWidth = 2 * sprites.get("Enemy_Delete_2").getWidth();
                        explosionHeight = 2 * sprites.get("Enemy_Delete_2").getHeight();
                        gc.drawImage(sprites.get("Enemy_Delete_2"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, explosionWidth, explosionHeight);
                    }
                    else
                    {
                        if(getProgressedTime() < 0.3)
                        {
                            explosionWidth = 2 * sprites.get("Enemy_Delete_3").getWidth();
                            explosionHeight = 2 * sprites.get("Enemy_Delete_3").getHeight();
                            gc.drawImage(sprites.get("Enemy_Delete_3"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, explosionWidth, explosionHeight);
                        }
                        else
                        {
                            if(getProgressedTime() < 0.4)
                            {
                                explosionWidth = 2 * sprites.get("Enemy_Delete_4").getWidth();
                                explosionHeight = 2 * sprites.get("Enemy_Delete_4").getHeight();
                                gc.drawImage(sprites.get("Enemy_Delete_4"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, explosionWidth, explosionHeight);
                            }
                            else
                            {
                                if(getProgressedTime() < 0.5)
                                {
                                    explosionWidth = 2 * sprites.get("Enemy_Delete_5").getWidth();
                                    explosionHeight = 2 * sprites.get("Enemy_Delete_5").getHeight();
                                    gc.drawImage(sprites.get("Enemy_Delete_5"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, explosionWidth, explosionHeight);
                                }
                                else
                                {
                                    explosionWidth = 2 * sprites.get("Enemy_Delete_6").getWidth();
                                    explosionHeight = 2 * sprites.get("Enemy_Delete_6").getHeight();
                                    gc.drawImage(sprites.get("Enemy_Delete_6"), getTileUpperRightX(X) + 10, getTileUpperRightY(Y) - 11, explosionWidth, explosionHeight);
                                }
                            }
                        }
                    }
                }
                    if(getProgressedTime() > 0.6){
                    isdead = true;
                    currentState = CanodumbStates.Canodumb_Dead;
                    stateStartTime = System.nanoTime();
                    Y = 100;
                    X = 100;
                }




        }
    }

    public double getProgressedTime()
    {
        if(paused)
        {
            return (((System.nanoTime() - stateStartTime) - (System.nanoTime() - pauseStateTime)) / 1000000000.0);

        }
        else
        {
            return (((System.nanoTime() - stateStartTime) - (resumeStateTime - pauseStateTime)) / 1000000000.0);
        }
    }

    @Override
    public void takeDamage(int amount,DamageType type)
    {
        super.takeDamage(amount,type);
        if(getCurrentHealth() == 0 && currentState !=  CanodumbStates.Canodumb_Dying && currentState !=  CanodumbStates.Canodumb_Dead)
        {
            currentState = CanodumbStates.Canodumb_Dying;
            stateStartTime = System.nanoTime();
            pauseStateTime = System.nanoTime();
            resumeStateTime = System.nanoTime();
            Media sound = new Media(new File("GameData/Sounds/Virus_Deleted.wav").toURI().toString());
            mediaplayer = new MediaPlayer(sound);
            mediaplayer.play();
        }
    }

    @Override
    public void fixSpawnin()
    {
        if(currentState == CanodumbStates.Canodumb_Spawning_In) {
            currentState = CanodumbStates.Canodumb_Idle;
        }

        resumeStateTime = System.nanoTime();
    }


    @Override
    public void renderHP(GraphicsContext gc, Canvas canvas, HashMap<String,Image> sprites)
    {
        gc.setGlobalAlpha(1.0);
        int currentBuffer = 0;

        String num = String.valueOf(getCurrentHealth());
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < num.length(); i++)
        {
            list.add(String.valueOf(num.charAt(i)));
        }
        Collections.reverse(list);
        int a = 0;
        for(String n: list)
        {
            currentBuffer += sprites.get("EHP_" + n).getWidth() * 2;
            gc.drawImage(sprites.get("EHP_" + n), getTileUpperRightX(X) + 67 - currentBuffer,getTileUpperRightY(Y) - 37,sprites.get("EHP_" + n).getWidth() * 2,sprites.get("EHP_" + n).getHeight() * 2);
            a++;
        }
    }

    public CanodumbStates getState() {
        return currentState;
    }

}
