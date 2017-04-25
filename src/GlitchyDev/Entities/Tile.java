package GlitchyDev.Entities;

import java.util.ArrayList;

/**
 * Created by Robert on 4/19/2017.
 */
public class Tile {
    private int X;
    private int Y;
    public boolean isRed;
    public boolean passable;
    public boolean paused;

    public boolean areaGrab;
    public long stateStartTime;
    public long resumeStateTime;
    public long pauseStateTime;


    public Tile(int X, int Y, boolean isRed)
    {
        this.X = X;
        this.Y = Y;
        this.isRed = isRed;
        this.passable = true;

        stateStartTime = -1;
        resumeStateTime = System.nanoTime();
        pauseStateTime = System.nanoTime();
        paused = false;
        areaGrab = false;
    }

    public String getSprite(Megaman megaman, ArrayList<Tile> tiles)
    {
        if(areaGrab)
        {
            if (getProgressedTime() > 15)
            {
                if(megaman.getX() < X) {
                    boolean nonToRight = true;

                    for(Tile tile: tiles)
                    {
                        if(tile.getX() > X)
                        {
                            if(tile.isRed)
                            {
                                nonToRight = false;
                            }
                        }
                    }
                    if(nonToRight) {
                        areaGrab = false;
                        isRed = !isRed;
                    }
                }
            }
        }
        if(areaGrab)
        {
            if(getProgressedTime() < 0.25)
            {
                double filter = Math.sin(getProgressedTime() * 10 * Math.PI);
                if(filter < 0)
                {
                    return "Red_Tile_" + (4 - Y);
                }
                else
                {
                    return "Blue_Tile_" + (4 - Y);
                }
            }
            else
            {
                if(getProgressedTime() > 13.5)
                {
                    double filter = Math.sin(getProgressedTime() * 4 * Math.PI);
                    if(filter < 0.5)
                    {
                        return "Red_Tile_" + (4 - Y);
                    }
                    else
                    {
                        return "Blue_Tile_" + (4 - Y);
                    }
                }
                else
                {
                    if (isRed) {
                        return "Red_Tile_" + (4 - Y);
                    } else {
                        return "Blue_Tile_" + (4 - Y);
                    }
                }
            }
        }
        else {
            if (isRed) {
                return "Red_Tile_" + (4 - Y);
            } else {
                return "Blue_Tile_" + (4 - Y);
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

    public void areaGrab()
    {
        isRed = true;
        areaGrab = true;
        stateStartTime = System.nanoTime();
        pauseStateTime = System.nanoTime();
        resumeStateTime = System.nanoTime();

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

    public boolean isRed() {
        return isRed;
    }

    public void setRed(boolean red) {
        isRed = red;
    }
}
