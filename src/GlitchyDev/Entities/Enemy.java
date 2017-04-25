package GlitchyDev.Entities;

import GlitchyDev.States.DamageType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import GlitchyDev.States.EnemyType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robert on 4/19/2017.
 */
public class Enemy {
    protected EnemyType enemyType;
    protected int CurrentHealth = 100;
    protected int X;
    protected int Y;
    protected boolean isdead;
    protected boolean doRenderHeath;
    public long resumeStateTime;
    public long pauseStateTime;



    public boolean paused = false;

    protected int enemyID;

    public Enemy(int X, int Y, int enemyID)
    {
        this.X = X;
        this.Y = Y;
        this.enemyID = enemyID;
        resumeStateTime = System.nanoTime();
    }

    public void doEnemy(GraphicsContext gc, Canvas canvas, HashMap<String,Image> sprites, ArrayList<Enemy> enemies, Megaman megaman, ArrayList<Tile> tiles)
    {
        // Implemented by Superclass
    }

    public void renderHP(GraphicsContext gc, Canvas canvas, HashMap<String,Image> sprites)
    {

    }

    public void takeDamage(int amount,DamageType type)
    {
        // Override
        setCurrentHealth(getCurrentHealth() - amount);
        if(getCurrentHealth() < 0)
        {
            setCurrentHealth(0);
        }
    }


    public void fixSpawnin()
    {

    }
    public int getTileUpperRightX(int X) {return 0 + (X - 1) * 80;}

    public int getTileUpperRightY(int Y) {
        return 105 + (Y - 1) * 48;
    }

    public int getCurrentHealth() {
        return CurrentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        CurrentHealth = currentHealth;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public boolean getDoRenderHealth()
    {
        return doRenderHeath;
    }

    public boolean getIsDead()
    {
        return isdead;
    }


}
