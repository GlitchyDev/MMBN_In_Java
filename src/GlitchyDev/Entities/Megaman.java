package GlitchyDev.Entities;

import GlitchyDev.ChipMenu.Chip;
import GlitchyDev.States.DamageType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import GlitchyDev.States.MegamanStates;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Robert on 4/19/2017.
 */
public class Megaman {
    private int maxHealth = 100;
    private int currentHealth = 100;
    private MegamanStates currentState;
    public ArrayList<Chip> chips = new ArrayList<>();
    private long stateStartTime;
    public long pauseStateTime;
    public long resumeStateTime;
    private int X;
    private int Y;

    public MediaPlayer mediaplayer;

    public boolean paused = false;
    public boolean showChips = true;

    private long healStartTime = 0;

    public boolean isDead = false;





    public Megaman(int X, int Y)
    {
        this.X = X;
        this.Y = Y;
        currentState = MegamanStates.Megaman_Spawning_In;
        stateStartTime = System.nanoTime();
        resumeStateTime = System.nanoTime();
    }


    public void doMegaman(GraphicsContext gc, Canvas canvas, HashMap<String,Image> sprites, ArrayList<Enemy> enemies, ArrayList<Tile> tiles)
    {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;

        switch(currentState)
        {
            case Megaman_Spawning_In:
                // Fade in time
                final double spawnInTime = 0.05;
                // How long to wait to run it
                final double waitTime = 0.2;

                if(progressedSeconds >= waitTime) {
                    if (progressedSeconds <= spawnInTime + waitTime) {
                        gc.setGlobalAlpha((progressedSeconds - waitTime) / spawnInTime);
                    } else {
                        gc.setGlobalAlpha(1.0);
                    }
                    double megamanWidth = 2 * sprites.get("Megaman_Idle_1").getWidth();
                    double megamanHeight = 2 * sprites.get("Megaman_Idle_1").getHeight();
                    double megamanShadowWidth = 2 * sprites.get("Megaman_Shadow").getWidth();
                    double megamanShadowHeight = 2 * sprites.get("Megaman_Shadow").getHeight();

                    gc.drawImage(sprites.get("Megaman_Shadow"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + 56, megamanShadowWidth, megamanShadowHeight);
                    gc.drawImage(sprites.get("Megaman_Idle_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                }
                break;

            case Megaman_Idle:
                double megamanWidth = 2 * sprites.get("Megaman_Idle_1").getWidth();
                double megamanHeight = 2 * sprites.get("Megaman_Idle_1").getHeight();
                double megamanShadowWidth = 2 * sprites.get("Megaman_Shadow").getWidth();
                double megamanShadowHeight = 2 * sprites.get("Megaman_Shadow").getHeight();

                gc.drawImage(sprites.get("Megaman_Shadow"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + 56, megamanShadowWidth, megamanShadowHeight);


                double animationProgress = (System.nanoTime() / 1000000000.0 ) % 4.0;
                if(animationProgress < 3.5) {
                    gc.drawImage(sprites.get("Megaman_Idle_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);}
                else {if(animationProgress < 3.7) {gc.drawImage(sprites.get("Megaman_Idle_2"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);}
                    else {if(animationProgress < 3.8) {gc.drawImage(sprites.get("Megaman_Idle_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);}
                        else {if(animationProgress < 3.85) {gc.drawImage(sprites.get("Megaman_Idle_4"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);}
                            else {if(animationProgress < 3.9) {gc.drawImage(sprites.get("Megaman_Idle_2"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);}
                                else {gc.drawImage(sprites.get("Megaman_Idle_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);}
                            }
                        }
                    }
                }

                if(showChips) {
                    for (int i = chips.size() - 1; i >= 0; i--) {
                        double iconWidth = sprites.get("Cannon_Icon").getWidth() * 2;
                        double iconHeight = sprites.get("Cannon_Icon").getHeight() * 2;
                        gc.drawImage(sprites.get(chips.get(i).getUnselectedChipIcon()), getTileUpperRightX(X) + 40 - 4 * (i - 1), getTileUpperRightY(Y) + -43 - 4 * (i - 1), iconWidth, iconHeight);
                    }
                    if (chips.size() != 0) {
                        Chip chip = chips.get(0);
                        double chipLoadWidth = sprites.get(chip.getChipLoad()).getWidth() * 2;
                        double chipLoadHeight = sprites.get(chip.getChipLoad()).getHeight() * 2;
                        gc.drawImage(sprites.get(chip.getChipLoad()), 10, 288, chipLoadWidth, chipLoadHeight);
                    }
                }
                drawHeal(sprites, gc);

                break;
            case Megaman_Move:
                megamanWidth = 2 * sprites.get("Megaman_Idle_1").getWidth();
                megamanHeight = 2 * sprites.get("Megaman_Idle_1").getHeight();
                megamanShadowWidth = 2 * sprites.get("Megaman_Shadow").getWidth();
                megamanShadowHeight = 2 * sprites.get("Megaman_Shadow").getHeight();

                gc.drawImage(sprites.get("Megaman_Shadow"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + 56, megamanShadowWidth, megamanShadowHeight);
                if(getProgressedTime() < 0.023) {
                    gc.drawImage(sprites.get("Megaman_Move_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                }
                else
                {
                    if(getProgressedTime() < 0.046) {
                        gc.drawImage(sprites.get("Megaman_Move_2"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                    }
                    else
                    {
                        if(getProgressedTime() < 0.069) {
                            gc.drawImage(sprites.get("Megaman_Move_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                        }
                        else
                        {
                            gc.drawImage(sprites.get("Megaman_Idle_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);

                        }
                    }
                }

                if(showChips) {
                    for (int i = chips.size() - 1; i >= 0; i--) {
                        double iconWidth = sprites.get("Cannon_Icon").getWidth() * 2;
                        double iconHeight = sprites.get("Cannon_Icon").getHeight() * 2;
                        gc.drawImage(sprites.get(chips.get(i).getUnselectedChipIcon()), getTileUpperRightX(X) + 40 - 4 * (i - 1), getTileUpperRightY(Y) + -43 - 4 * (i - 1), iconWidth, iconHeight);
                    }
                    if (chips.size() != 0) {
                        Chip chip = chips.get(0);
                        double chipLoadWidth = sprites.get(chip.getChipLoad()).getWidth() * 2;
                        double chipLoadHeight = sprites.get(chip.getChipLoad()).getHeight() * 2;
                        gc.drawImage(sprites.get(chip.getChipLoad()), 10, 288, chipLoadWidth, chipLoadHeight);
                    }
                }
                drawHeal(sprites, gc);
                if(getProgressedTime() > 0.08)
                {
                    currentState = MegamanStates.Megaman_Idle;
                    resumeStateTime = System.nanoTime();
                    stateStartTime = System.nanoTime();
                    pauseStateTime = System.nanoTime();
                }
                break;
            case Megaman_Firing:

                megamanShadowWidth = 2 * sprites.get("Megaman_Shadow").getWidth();
                megamanShadowHeight = 2 * sprites.get("Megaman_Shadow").getHeight();

                gc.drawImage(sprites.get("Megaman_Shadow"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + 56, megamanShadowWidth, megamanShadowHeight);
                if(getProgressedTime() < 0.01) {
                    megamanWidth = 2 * sprites.get("Megaman_Firing_1").getWidth();
                    megamanHeight = 2 * sprites.get("Megaman_Firing_1").getHeight();
                    gc.drawImage(sprites.get("Megaman_Firing_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                }
                else {
                    if(getProgressedTime() < 0.05) {

                        megamanWidth = 2 * sprites.get("Megaman_Firing_2").getWidth();
                        megamanHeight = 2 * sprites.get("Megaman_Firing_2").getHeight();
                        gc.drawImage(sprites.get("Megaman_Firing_2"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                    }
                    else
                    {
                        if(getProgressedTime() < 0.07) {
                            megamanWidth = 2 * sprites.get("Megaman_Firing_3").getWidth();
                            megamanHeight = 2 * sprites.get("Megaman_Firing_3").getHeight();
                            gc.drawImage(sprites.get("Megaman_Firing_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);

                        }
                        else
                        {
                            if(getProgressedTime() < 0.13) {
                                megamanWidth = 2 * sprites.get("Megaman_Firing_4").getWidth();
                                megamanHeight = 2 * sprites.get("Megaman_Firing_4").getHeight();
                                gc.drawImage(sprites.get("Megaman_Firing_4"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                            }
                            else
                            {
                                megamanWidth = 2 * sprites.get("Megaman_Firing_1").getWidth();
                                megamanHeight = 2 * sprites.get("Megaman_Firing_1").getHeight();
                                gc.drawImage(sprites.get("Megaman_Firing_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                            }

                        }
                    }
                }



                if(showChips) {
                    for (int i = chips.size() - 1; i >= 0; i--) {
                        double iconWidth = sprites.get("Cannon_Icon").getWidth() * 2;
                        double iconHeight = sprites.get("Cannon_Icon").getHeight() * 2;
                        gc.drawImage(sprites.get(chips.get(i).getUnselectedChipIcon()), getTileUpperRightX(X) + 40 - 4 * (i - 1), getTileUpperRightY(Y) + -43 - 4 * (i - 1), iconWidth, iconHeight);
                    }
                    if (chips.size() != 0) {
                        Chip chip = chips.get(0);
                        double chipLoadWidth = sprites.get(chip.getChipLoad()).getWidth() * 2;
                        double chipLoadHeight = sprites.get(chip.getChipLoad()).getHeight() * 2;
                        gc.drawImage(sprites.get(chip.getChipLoad()), 10, 288, chipLoadWidth, chipLoadHeight);
                    }
                }
                if(getProgressedTime() > 0.15)
                {
                    currentState = MegamanStates.Megaman_Idle;
                    resumeStateTime = System.nanoTime();
                    stateStartTime = System.nanoTime();
                    pauseStateTime = System.nanoTime();
                }
                break;
            case Megaman_Cannon:

                megamanShadowWidth = 2 * sprites.get("Megaman_Shadow").getWidth();
                megamanShadowHeight = 2 * sprites.get("Megaman_Shadow").getHeight();

                gc.drawImage(sprites.get("Megaman_Shadow"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + 56, megamanShadowWidth, megamanShadowHeight);
                int adj = 76;
                if(getProgressedTime() < 0.05) {
                    megamanWidth = 2 * sprites.get("Megaman_Cannon_1").getWidth();
                    megamanHeight = 2 * sprites.get("Megaman_Cannon_1").getHeight();
                    gc.drawImage(sprites.get("Megaman_Cannon_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                }
                else {
                    if(getProgressedTime() < 0.06) {

                        megamanWidth = 2 * sprites.get("Megaman_Cannon_2").getWidth();
                        megamanHeight = 2 * sprites.get("Megaman_Cannon_2").getHeight();
                        gc.drawImage(sprites.get("Megaman_Cannon_2"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                    }
                    else
                    {
                        if(getProgressedTime() < 0.12) {
                            megamanWidth = 2 * sprites.get("Megaman_Cannon_3").getWidth();
                            megamanHeight = 2 * sprites.get("Megaman_Cannon_3").getHeight();
                            gc.drawImage(sprites.get("Megaman_Cannon_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);

                        }
                        else
                        {
                            if(getProgressedTime() < 0.18) {
                                megamanWidth = 2 * sprites.get("Megaman_Cannon_4").getWidth();
                                megamanHeight = 2 * sprites.get("Megaman_Cannon_4").getHeight();
                                gc.drawImage(sprites.get("Megaman_Cannon_4"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                            }
                            else
                            {
                                if(getProgressedTime() < 0.24) {
                                    megamanWidth = 2 * sprites.get("Megaman_Cannon_6").getWidth();
                                    megamanHeight = 2 * sprites.get("Megaman_Cannon_6").getHeight();
                                    gc.drawImage(sprites.get("Megaman_Cannon_6"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                                }
                                else
                                {
                                    if(getProgressedTime() < 0.3) {
                                        megamanWidth = 2 * sprites.get("Megaman_Cannon_7").getWidth();
                                        megamanHeight = 2 * sprites.get("Megaman_Cannon_7").getHeight();
                                        gc.drawImage(sprites.get("Megaman_Cannon_7"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                                    }
                                    else
                                    {
                                        if(getProgressedTime() < 0.36) {
                                            megamanWidth = 2 * sprites.get("Megaman_Cannon_8").getWidth();
                                            megamanHeight = 2 * sprites.get("Megaman_Cannon_8").getHeight();
                                            gc.drawImage(sprites.get("Megaman_Cannon_8"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                                        }
                                        else
                                        {
                                            megamanWidth = 2 * sprites.get("Megaman_Cannon_2").getWidth();
                                            megamanHeight = 2 * sprites.get("Megaman_Cannon_2").getHeight();
                                            gc.drawImage(sprites.get("Megaman_Cannon_2"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                                        }
                                    }
                                }
                            }

                        }
                    }
                }



                if(showChips) {
                    for (int i = chips.size() - 1; i >= 0; i--) {
                        double iconWidth = sprites.get("Cannon_Icon").getWidth() * 2;
                        double iconHeight = sprites.get("Cannon_Icon").getHeight() * 2;
                        gc.drawImage(sprites.get(chips.get(i).getUnselectedChipIcon()), getTileUpperRightX(X) + 40 - 4 * (i - 1), getTileUpperRightY(Y) + -43 - 4 * (i - 1), iconWidth, iconHeight);
                    }
                    if (chips.size() != 0) {
                        Chip chip = chips.get(0);
                        double chipLoadWidth = sprites.get(chip.getChipLoad()).getWidth() * 2;
                        double chipLoadHeight = sprites.get(chip.getChipLoad()).getHeight() * 2;
                        gc.drawImage(sprites.get(chip.getChipLoad()), 10, 288, chipLoadWidth, chipLoadHeight);
                    }
                }
                drawHeal(sprites, gc);
                if(getProgressedTime() > 0.42)
                {
                    currentState = MegamanStates.Megaman_Idle;
                    resumeStateTime = System.nanoTime();
                    stateStartTime = System.nanoTime();
                    pauseStateTime = System.nanoTime();
                }

                break;


            case Megaman_Sword:

                megamanShadowWidth = 2 * sprites.get("Megaman_Shadow").getWidth();
                megamanShadowHeight = 2 * sprites.get("Megaman_Shadow").getHeight();

                gc.drawImage(sprites.get("Megaman_Shadow"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + 56, megamanShadowWidth, megamanShadowHeight);
                adj = 76;
                if(getProgressedTime() < 0.05) {
                    megamanWidth = 2 * sprites.get("Megaman_Sword_1").getWidth();
                    megamanHeight = 2 * sprites.get("Megaman_Sword_1").getHeight();
                    gc.drawImage(sprites.get("Megaman_Sword_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                }
                else {
                    if(getProgressedTime() < 0.06) {

                        megamanWidth = 2 * sprites.get("Megaman_Sword_2").getWidth();
                        megamanHeight = 2 * sprites.get("Megaman_Sword_2").getHeight();
                        gc.drawImage(sprites.get("Megaman_Sword_2"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                    }
                    else
                    {
                        if(getProgressedTime() < 0.12) {
                            megamanWidth = 2 * sprites.get("Megaman_Sword_3").getWidth();
                            megamanHeight = 2 * sprites.get("Megaman_Sword_3").getHeight();
                            gc.drawImage(sprites.get("Megaman_Sword_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);

                        }
                        else
                        {
                            if(getProgressedTime() < 0.18) {
                                megamanWidth = 2 * sprites.get("Megaman_Sword_4").getWidth();
                                megamanHeight = 2 * sprites.get("Megaman_Sword_4").getHeight();
                                gc.drawImage(sprites.get("Megaman_Sword_4"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                            }
                            else
                            {
                                if(getProgressedTime() < 0.24) {
                                    megamanWidth = 2 * sprites.get("Megaman_Sword_5").getWidth();
                                    megamanHeight = 2 * sprites.get("Megaman_Sword_5").getHeight();
                                    gc.drawImage(sprites.get("Megaman_Sword_5"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                                }
                                else
                                {
                                    megamanWidth = 2 * sprites.get("Megaman_Cannon_7").getWidth();
                                    megamanHeight = 2 * sprites.get("Megaman_Cannon_7").getHeight();
                                    gc.drawImage(sprites.get("Megaman_Cannon_7"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                                }
                            }

                        }
                    }
                }



                if(showChips) {
                    for (int i = chips.size() - 1; i >= 0; i--) {
                        double iconWidth = sprites.get("Cannon_Icon").getWidth() * 2;
                        double iconHeight = sprites.get("Cannon_Icon").getHeight() * 2;
                        gc.drawImage(sprites.get(chips.get(i).getUnselectedChipIcon()), getTileUpperRightX(X) + 40 - 4 * (i - 1), getTileUpperRightY(Y) + -43 - 4 * (i - 1), iconWidth, iconHeight);
                    }
                    if (chips.size() != 0) {
                        Chip chip = chips.get(0);
                        double chipLoadWidth = sprites.get(chip.getChipLoad()).getWidth() * 2;
                        double chipLoadHeight = sprites.get(chip.getChipLoad()).getHeight() * 2;
                        gc.drawImage(sprites.get(chip.getChipLoad()), 10, 288, chipLoadWidth, chipLoadHeight);
                    }
                }
                drawHeal(sprites, gc);
                if(getProgressedTime() > 0.3)
                {
                    currentState = MegamanStates.Megaman_Idle;
                    resumeStateTime = System.nanoTime();
                    stateStartTime = System.nanoTime();
                    pauseStateTime = System.nanoTime();
                }
                break;


            case Megaman_Hurt:

                megamanShadowWidth = 2 * sprites.get("Megaman_Shadow").getWidth();
                megamanShadowHeight = 2 * sprites.get("Megaman_Shadow").getHeight();

                gc.drawImage(sprites.get("Megaman_Shadow"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + 56, megamanShadowWidth, megamanShadowHeight);
                if(getProgressedTime() < 0.05) {
                    megamanWidth = 2 * sprites.get("Megaman_Hurt_1").getWidth();
                    megamanHeight = 2 * sprites.get("Megaman_Hurt_1").getHeight();
                    gc.drawImage(sprites.get("Megaman_Hurt_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                }
                else {
                    if(getProgressedTime() < 0.1) {
                        megamanWidth = 2 * sprites.get("Megaman_Hurt_2").getWidth();
                        megamanHeight = 2 * sprites.get("Megaman_Hurt_2").getHeight();
                        gc.drawImage(sprites.get("Megaman_Hurt_2"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);
                    }
                    else
                    {
                        if(getProgressedTime() < 0.15) {
                            megamanWidth = 2 * sprites.get("Megaman_Hurt_3").getWidth();
                            megamanHeight = 2 * sprites.get("Megaman_Hurt_3").getHeight();
                            gc.drawImage(sprites.get("Megaman_Hurt_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);

                        }
                        else
                        {
                            if(getProgressedTime() < 0.20) {
                                megamanWidth = 2 * sprites.get("Megaman_Hurt_3").getWidth();
                                megamanHeight = 2 * sprites.get("Megaman_Hurt_3").getHeight();
                                gc.drawImage(sprites.get("Megaman_Hurt_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);

                            }
                            else
                            {
                                megamanWidth = 2 * sprites.get("Megaman_Hurt_4").getWidth();
                                megamanHeight = 2 * sprites.get("Megaman_Hurt_4").getHeight();
                                gc.drawImage(sprites.get("Megaman_Hurt_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) - 14, megamanWidth, megamanHeight);


                            }
                        }
                    }
                }



                if(showChips) {
                    for (int i = chips.size() - 1; i >= 0; i--) {
                        double iconWidth = sprites.get("Cannon_Icon").getWidth() * 2;
                        double iconHeight = sprites.get("Cannon_Icon").getHeight() * 2;
                        gc.drawImage(sprites.get(chips.get(i).getUnselectedChipIcon()), getTileUpperRightX(X) + 40 - 4 * (i - 1), getTileUpperRightY(Y) + -43 - 4 * (i - 1), iconWidth, iconHeight);
                    }
                    if (chips.size() != 0) {
                        Chip chip = chips.get(0);
                        double chipLoadWidth = sprites.get(chip.getChipLoad()).getWidth() * 2;
                        double chipLoadHeight = sprites.get(chip.getChipLoad()).getHeight() * 2;
                        gc.drawImage(sprites.get(chip.getChipLoad()), 10, 288, chipLoadWidth, chipLoadHeight);
                    }
                }
                if(getProgressedTime() > 0.25)
                {
                    currentState = MegamanStates.Megaman_Idle;
                    resumeStateTime = System.nanoTime();
                    stateStartTime = System.nanoTime();
                    pauseStateTime = System.nanoTime();
                }
                break;

            case Megaman_dying:

                double megamanDeletedWidth;
                double megamanDeletedHeight;

                adj = 76;
                if(getProgressedTime() < 0.05) {
                    megamanWidth = 2 * sprites.get("Megaman_Hurt_1").getWidth();
                    megamanHeight = 2 * sprites.get("Megaman_Hurt_1").getHeight();
                    megamanDeletedWidth = 2 * sprites.get("Megaman_Deleted_1").getWidth();
                    megamanDeletedHeight = 2 * sprites.get("Megaman_Deleted_1").getHeight();


                    gc.drawImage(sprites.get("Megaman_Hurt_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                    gc.drawImage(sprites.get("Megaman_Deleted_1"), getTileUpperRightX(X) + 30 - megamanDeletedWidth/2, getTileUpperRightY(Y) + adj - 50 - megamanDeletedHeight/2, megamanDeletedWidth, megamanDeletedHeight);


                }
                else {
                     if(getProgressedTime() < 0.2) {

                        megamanWidth = 2 * sprites.get("Megaman_Hurt_1").getWidth();
                        megamanHeight = 2 * sprites.get("Megaman_Hurt_1").getHeight();
                        megamanDeletedWidth = 2 * sprites.get("Megaman_Deleted_2").getWidth();
                        megamanDeletedHeight = 2 * sprites.get("Megaman_Deleted_2").getHeight();


                        gc.drawImage(sprites.get("Megaman_Hurt_1"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                         gc.drawImage(sprites.get("Megaman_Deleted_2"), getTileUpperRightX(X) + 30 - megamanDeletedWidth/2, getTileUpperRightY(Y) + adj - 50 - megamanDeletedHeight/2, megamanDeletedWidth, megamanDeletedHeight);
                    }
                    else
                    {
                        if(getProgressedTime() < 0.4) {
                            megamanWidth = 2 * sprites.get("Megaman_Hurt_2").getWidth();
                            megamanHeight = 2 * sprites.get("Megaman_Hurt_2").getHeight();
                            megamanDeletedWidth = 2 * sprites.get("Megaman_Deleted_3").getWidth();
                            megamanDeletedHeight = 2 * sprites.get("Megaman_Deleted_3").getHeight();


                            gc.drawImage(sprites.get("Megaman_Hurt_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                            gc.drawImage(sprites.get("Megaman_Deleted_3"), getTileUpperRightX(X) + 30 - megamanDeletedWidth/2, getTileUpperRightY(Y) + adj - 50 - megamanDeletedHeight/2, megamanDeletedWidth, megamanDeletedHeight);

                        }
                        else
                        {
                            if(getProgressedTime() < 0.6) {
                                megamanWidth = 2 * sprites.get("Megaman_Hurt_2").getWidth();
                                megamanHeight = 2 * sprites.get("Megaman_Hurt_2").getHeight();
                                megamanDeletedWidth = 2 * sprites.get("Megaman_Deleted_4").getWidth();
                                megamanDeletedHeight = 2 * sprites.get("Megaman_Deleted_4").getHeight();


                                gc.setGlobalAlpha(0.8);
                                gc.drawImage(sprites.get("Megaman_Hurt_2"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                                gc.setGlobalAlpha(1.0);

                                gc.drawImage(sprites.get("Megaman_Deleted_4"), getTileUpperRightX(X) + 30 - megamanDeletedWidth/2, getTileUpperRightY(Y) + adj - 50 - megamanDeletedHeight/2, megamanDeletedWidth, megamanDeletedHeight);
                            }
                            else
                            {
                                if(getProgressedTime() < 0.8) {
                                    megamanWidth = 2 * sprites.get("Megaman_Hurt_3").getWidth();
                                    megamanHeight = 2 * sprites.get("Megaman_Hurt_3").getHeight();
                                    megamanDeletedWidth = 2 * sprites.get("Megaman_Deleted_5").getWidth();
                                    megamanDeletedHeight = 2 * sprites.get("Megaman_Deleted_5").getHeight();


                                    gc.setGlobalAlpha(0.5);
                                    gc.drawImage(sprites.get("Megaman_Hurt_3"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                                    gc.setGlobalAlpha(1.0);                                    gc.drawImage(sprites.get("Megaman_Deleted_5"), getTileUpperRightX(X) + 30 - megamanDeletedWidth/2, getTileUpperRightY(Y) + adj - 50 - megamanDeletedHeight/2, megamanDeletedWidth, megamanDeletedHeight);
                                }
                                else
                                {
                                    if(getProgressedTime() < 1.0) {
                                        megamanWidth = 2 * sprites.get("Megaman_Hurt_4").getWidth();
                                        megamanHeight = 2 * sprites.get("Megaman_Hurt_4").getHeight();
                                        megamanDeletedWidth =  2 * sprites.get("Megaman_Deleted_6").getWidth();
                                        megamanDeletedHeight = 2 * sprites.get("Megaman_Deleted_6").getHeight();


                                        gc.setGlobalAlpha(0.5);
                                        gc.drawImage(sprites.get("Megaman_Hurt_4"), getTileUpperRightX(X) + 5, getTileUpperRightY(Y) + adj - megamanHeight, megamanWidth, megamanHeight);
                                        gc.setGlobalAlpha(1.0);
                                        gc.drawImage(sprites.get("Megaman_Deleted_6"), getTileUpperRightX(X) + 30 - megamanDeletedWidth/2, getTileUpperRightY(Y) + adj - 50 - megamanDeletedHeight/2, megamanDeletedWidth, megamanDeletedHeight);
                                    }
                                    else
                                    {
                                        if(!isDead)
                                        {
                                            isDead = true;
                                        }

                                    }
                                }
                            }

                        }
                    }
                }




        }
    }

    public void moveUp(ArrayList<Tile> tiles, ArrayList<Enemy> enemies)
    {
        // Check if megaman CAN move
        Tile tile = null;
        for(Tile posTile: tiles)
        {
            if(posTile.getX() == X && posTile.getY() == Y - 1 && posTile.passable && posTile.isRed && (currentState == MegamanStates.Megaman_Idle||currentState == MegamanStates.Megaman_Firing||currentState == MegamanStates.Megaman_Hurt))
            {
                tile = posTile;
            }
        }
        if(tile != null)
        {
            Y--;
            currentState = MegamanStates.Megaman_Move;
            resumeStateTime = System.nanoTime();
            pauseStateTime = System.nanoTime();
            stateStartTime = System.nanoTime();
        }
    }
    public void moveDown(ArrayList<Tile> tiles, ArrayList<Enemy> enemies)
    {
        Tile tile = null;
        for(Tile posTile: tiles)
        {
            if(posTile.getX() == X && posTile.getY() == Y + 1 && posTile.passable && posTile.isRed && (currentState == MegamanStates.Megaman_Idle||currentState == MegamanStates.Megaman_Firing||currentState == MegamanStates.Megaman_Hurt))
            {
                tile = posTile;
            }
        }
        if(tile != null)
        {
            Y++;
            currentState = MegamanStates.Megaman_Move;
            resumeStateTime = System.nanoTime();
            pauseStateTime = System.nanoTime();
            stateStartTime = System.nanoTime();
            // Add State Change
        }
    }
    public void moveRight(ArrayList<Tile> tiles, ArrayList<Enemy> enemies)
    {
        Tile tile = null;
        for(Tile posTile: tiles)
        {
            if(posTile.getX() == X + 1 && posTile.getY() == Y && posTile.passable && posTile.isRed && (currentState == MegamanStates.Megaman_Idle||currentState == MegamanStates.Megaman_Firing||currentState == MegamanStates.Megaman_Hurt))
            {
                tile = posTile;
            }
        }
        if(tile != null)
        {
            X++;
            currentState = MegamanStates.Megaman_Move;
            resumeStateTime = System.nanoTime();
            pauseStateTime = System.nanoTime();
            stateStartTime = System.nanoTime();
            // Add State Change
        }
    }
    public void moveLeft(ArrayList<Tile> tiles, ArrayList<Enemy> enemies)
    {
        Tile tile = null;
        for(Tile posTile: tiles)
        {
            if(posTile.getX() == X - 1 && posTile.getY() == Y && posTile.passable && posTile.isRed && (currentState == MegamanStates.Megaman_Idle||currentState == MegamanStates.Megaman_Firing||currentState == MegamanStates.Megaman_Hurt))
            {
                tile = posTile;
            }
        }
        if(tile != null)
        {
            X--;
            currentState = MegamanStates.Megaman_Move;
            resumeStateTime = System.nanoTime();
            pauseStateTime = System.nanoTime();
            stateStartTime = System.nanoTime();
            // Add State Change
        }
    }
    public void pressA(ArrayList<Tile> tiles, ArrayList<Enemy> enemies)
    {
        if(chips.size() != 0) {
            switch (chips.get(0).getChipType()) {
                case Cannon:
                    for (Enemy enemy : enemies) {
                        if (enemy.getY() == Y) {

                            enemy.takeDamage(40, DamageType.CANNON);
                            currentState = MegamanStates.Megaman_Cannon;
                            resumeStateTime = System.nanoTime();
                            pauseStateTime = System.nanoTime();
                            stateStartTime = System.nanoTime();
                            Media sound = new Media(new File("GameData/Sounds/Cannon.wav").toURI().toString());
                            mediaplayer = new MediaPlayer(sound);
                            mediaplayer.play();
                        }
                    }
                    break;
                case Sword:
                    for (Enemy enemy : enemies) {
                        if (enemy.getY() == Y) {

                            enemy.takeDamage(80, DamageType.SWORD);
                            currentState = MegamanStates.Megaman_Sword;
                            resumeStateTime = System.nanoTime();
                            pauseStateTime = System.nanoTime();
                            stateStartTime = System.nanoTime();
                            Media sound = new Media(new File("GameData/Sounds/SwordSwing.wav").toURI().toString());
                            mediaplayer = new MediaPlayer(sound);
                            mediaplayer.play();
                        }
                    }
                case Steal:
                    // Add 3 Panels temporarly to side
                    int greatestX = 6;
                    for(Tile tile: tiles) {
                        if (!tile.isRed)
                        {
                            if(tile.getX() < greatestX)
                            {
                                greatestX = tile.getX();
                            }
                        }

                    }


                    for(Tile tile: tiles) {
                        if(tile.getX() == greatestX)
                        {
                            boolean notOcupied = true;
                            for(Enemy enemy: enemies)
                            {
                                if(enemy.getX() == tile.getX() && enemy.getY() == tile.getY())
                                {
                                    notOcupied = false;
                                }
                            }
                            if(notOcupied)
                            {
                                Media sound = new Media(new File("GameData/Sounds/AreaGrab.wav").toURI().toString());
                                mediaplayer = new MediaPlayer(sound);
                                mediaplayer.play();
                                tile.areaGrab();
                            }
                        }

                    }
                    break;
                case Recover_10:
                    currentHealth += 10;
                    if(currentHealth > maxHealth)
                    {
                        currentHealth = maxHealth;
                    }
                    Media sound = new Media(new File("GameData/Sounds/Recover.wav").toURI().toString());
                    mediaplayer = new MediaPlayer(sound);
                    mediaplayer.play();
                    healStartTime = System.nanoTime();
                    currentState = MegamanStates.Megaman_Idle;
                    resumeStateTime = System.nanoTime();
                    pauseStateTime = System.nanoTime();
                    stateStartTime = System.nanoTime();
                    break;
            }
            chips.remove(chips.get(0));
        }
        // Use Chip
    }
    public void pressB(ArrayList<Tile> tiles, ArrayList<Enemy> enemies)
    {
        if(currentState == MegamanStates.Megaman_Idle || currentState == MegamanStates.Megaman_Move) {
            currentState = MegamanStates.Megaman_Firing;
            resumeStateTime = System.nanoTime();
            pauseStateTime = System.nanoTime();
            stateStartTime = System.nanoTime();


            Media sound = new Media(new File("GameData/Sounds/Buster_Shoot.wav").toURI().toString());
            MediaPlayer soundP1 = new MediaPlayer(sound);
            soundP1.play();
            for (Enemy enemy : enemies) {
                if (enemy.getY() == Y) {
                    enemy.takeDamage(1, DamageType.BUSTER);
                    sound = new Media(new File("GameData/Sounds/Buster_Hit.wav").toURI().toString());
                    MediaPlayer soundP = new MediaPlayer(sound);
                    soundP.play();
                }
            }
        }
        // Fire Buster
    }

    public void pressB1(ArrayList<Tile> tiles, ArrayList<Enemy> enemies)
    {
        if(currentState == MegamanStates.Megaman_Idle || currentState == MegamanStates.Megaman_Move) {
            currentState = MegamanStates.Megaman_Firing;
            resumeStateTime = System.nanoTime();
            pauseStateTime = System.nanoTime();
            stateStartTime = System.nanoTime();


            Media sound = new Media(new File("GameData/Sounds/Buster_Shoot.wav").toURI().toString());
            MediaPlayer soundP1 = new MediaPlayer(sound);
            soundP1.play();
            for (Enemy enemy : enemies) {
                if (enemy.getY() == Y) {
                    enemy.takeDamage(100, DamageType.BUSTER);
                    sound = new Media(new File("GameData/Sounds/Buster_Hit.wav").toURI().toString());
                    MediaPlayer soundP = new MediaPlayer(sound);
                    soundP.play();
                }
            }
        }
        // Fire Buster
    }

    public void drawHeal(HashMap<String,Image> sprites, GraphicsContext gc)
    {
        double healWidth = sprites.get("Recover_1").getWidth() * 2;
        double healHeight = sprites.get("Recover_1").getWidth() * 3;

        if(getHealProgressedTime() < 0.02)
        {
            gc.drawImage(sprites.get("Recover_1"), getTileUpperRightX(X) + 12, getTileUpperRightY(Y) - 14, healWidth, healHeight);
        }
        else
        {
            if(getHealProgressedTime() < 0.04)
            {
                gc.drawImage(sprites.get("Recover_2"), getTileUpperRightX(X) + 12, getTileUpperRightY(Y) - 14, healWidth, healHeight);
            }
            else
            {
                if(getHealProgressedTime() < 0.06)
                {
                    gc.drawImage(sprites.get("Recover_3"), getTileUpperRightX(X) + 12, getTileUpperRightY(Y) - 14, healWidth, healHeight);
                }
                else
                {
                    if(getHealProgressedTime() < 0.08)
                    {
                        gc.drawImage(sprites.get("Recover_4"), getTileUpperRightX(X) + 12, getTileUpperRightY(Y) - 14, healWidth, healHeight);
                    }
                    else
                    {
                        if(getHealProgressedTime() < 0.1)
                        {
                            gc.drawImage(sprites.get("Recover_5"), getTileUpperRightX(X) + 12, getTileUpperRightY(Y) - 14, healWidth, healHeight);
                        }
                        else
                        {
                            if(getHealProgressedTime() < 0.12)
                            {
                                gc.drawImage(sprites.get("Recover_6"), getTileUpperRightX(X) + 12, getTileUpperRightY(Y) - 14, healWidth, healHeight);
                            }
                            else
                            {
                                if(getHealProgressedTime() < 0.14)
                                {
                                    gc.drawImage(sprites.get("Recover_7"), getTileUpperRightX(X) + 12, getTileUpperRightY(Y) - 14, healWidth, healHeight);
                                }
                                else
                                {

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void takeDamage(int damageAmount)
    {
        if(currentState != MegamanStates.Megaman_Hurt && currentState != MegamanStates.Megaman_dying) {
            currentHealth -= damageAmount;
            if(currentHealth > 0) {
                currentState = MegamanStates.Megaman_Hurt;
                resumeStateTime = System.nanoTime();
                pauseStateTime = System.nanoTime();
                stateStartTime = System.nanoTime();
            }
            else
            {
                currentState = MegamanStates.Megaman_dying;
                resumeStateTime = System.nanoTime();
                pauseStateTime = System.nanoTime();
                stateStartTime = System.nanoTime();
                Media sound = new Media(new File("GameData/Sounds/Megaman_Deleted.wav").toURI().toString());
                mediaplayer = new MediaPlayer(sound);
                mediaplayer.play();
            }
        }
    }

    public double getHealProgressedTime()
    {
        if(paused)
        {
            return (((System.nanoTime() - healStartTime) - (System.nanoTime() - pauseStateTime)) / 1000000000.0);

        }
        else
        {
            return (((System.nanoTime() - healStartTime) - (resumeStateTime - pauseStateTime)) / 1000000000.0);
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




    public int getTileUpperRightX(int X) {return 0 + (X - 1) * 80;}

    public int getTileUpperRightY(int Y) {
        return 106 + (Y - 1) * 48;
    }


    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public MegamanStates getCurrentState() {
        return currentState;
    }

    public void setCurrentState(MegamanStates currentState) {
        this.currentState = currentState;
    }

    public long getStateStartTime() {
        return stateStartTime;
    }

    public void setStateStartTime(long stateStartTime) {
        this.stateStartTime = stateStartTime;
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
}
