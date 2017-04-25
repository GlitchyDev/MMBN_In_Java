package GlitchyDev.ChipMenu;

import GlitchyDev.States.ChipType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Robert on 4/20/2017.
 */
public class ChipMenuState {
    public ArrayList<Chip> chipMenu = new ArrayList<>();
    public ArrayList<Chip> currentlySelectedChips = new ArrayList<>();

    private int cursorX = 1;
    private int cursorY = 1;
    public int addCount = 1;

    private boolean onAdd = false;
    private boolean canAdd = true;
    private boolean showDescription = false;

    public boolean buttonSkidEnabled = false;
    public String heldButton = "";
    public long heldButtonStartTime = 0;

    private long chipLoadTime = 0;

    public boolean loadChipsComplete = false;
    public boolean addChipsComplete = false;


    private MediaPlayer soundEffect;

    private final String Chip_Cancel = "GameData/Sounds/Chip_Cancel.wav";
    private final String Chip_Choose = "GameData/Sounds/Chip_Choose.wav";
    private final String Chip_Confirm = "GameData/Sounds/Chip_Confirm.wav";
    private final String Chip_Description = "GameData/Sounds/Chip_Description.wav";
    private final String Close_Chip_Description = "GameData/Sounds/Close_Chip_Description.wav";
    private final String Chip_Select = "GameData/Sounds/Chip_Select.wav";








    public ChipMenuState()
    {
        reEnter();
    }

    public void reEnter()
    {
        Random random = new Random();

        ArrayList<Chip> temp = new ArrayList<>();
        for(int i = 0; i < chipMenu.size(); i++)
        {
            if(!chipMenu.get(i).isSelected)
            {
                temp.add(chipMenu.get(i));
            }
        }
        chipMenu = temp;
        for(int i = chipMenu.size(); i < addCount * 5; i++)
        {
            switch(random.nextInt(6))
            {
                case 0:
                    chipMenu.add(new Chip(ChipType.Cannon,"A"));
                    break;
                case 1:
                    chipMenu.add(new Chip(ChipType.Cannon,"B"));
                    break;
                case 2:
                    chipMenu.add(new Chip(ChipType.Recover_10,"A"));
                    break;
                case 3:
                    chipMenu.add(new Chip(ChipType.Recover_10,"L"));
                    break;
                case 4:
                    chipMenu.add(new Chip(ChipType.Sword,"S"));
                    break;
                case 5:
                    chipMenu.add(new Chip(ChipType.Steal,"S"));
                    break;
            }
        }

        cursorX = 1;
        cursorY = 1;
        onAdd = false;
        if(addCount != 3) {
            canAdd = true;
        }
        else
        {
            canAdd = false;
        }
        showDescription = false;
        buttonSkidEnabled= false;
        heldButton = "";
        heldButtonStartTime = 0;
        chipLoadTime = 0;
        loadChipsComplete = false;
        addChipsComplete = false;
        soundEffect = null;
        currentlySelectedChips = new ArrayList<>();
    }

    public void doChipMenu(GraphicsContext gc, Canvas canvas, double xOffset,HashMap<String,Image> sprites)
    {
        // DETECTING SKID
        double buttonHeldSeconds = (System.nanoTime() - heldButtonStartTime) / 1000000000.0;
        if(buttonHeldSeconds > 0.4 && !buttonSkidEnabled && !heldButton.equals(""))
        {
            buttonSkidEnabled = true;
            heldButtonStartTime = System.nanoTime();
        }
        if(buttonHeldSeconds > 0.05 && buttonSkidEnabled && !heldButton.equals("") && !onAdd)
        {
            heldButtonStartTime = System.nanoTime();
            switch(heldButton) {
                case "UP":
                    moveUp();
                    break;
                case "DOWN":
                    if(cursorX != 6)
                    {
                        moveDown();
                    }
                    break;
                case "RIGHT":
                    moveRight();
                    break;
                case "LEFT":
                    moveLeft();
                    break;
            }
        }
        if(heldButton.equals("") && buttonSkidEnabled)
        {
            buttonSkidEnabled = false;
        }

        // Drawing the Chip Menu
        double chipSelectScreenWidth = sprites.get("Chip_Select_Screen").getWidth() * 2;
        double chipSelectScreenHeight = sprites.get("Chip_Select_Screen").getHeight() * 2;
        gc.drawImage(sprites.get("Chip_Select_Screen"),-240 + xOffset,0,chipSelectScreenWidth,chipSelectScreenHeight);

        // Drawing Icons
        double chipIconWidth = sprites.get("Cannon_Icon").getWidth() * 2;
        double chipIconHeight = sprites.get("Cannon_Icon").getHeight() * 2;

        for(int i = 0; i < chipMenu.size(); i++)
        {
            Chip currentChip = chipMenu.get(i);
            gc.drawImage(sprites.get(currentChip.getChipIcon()),-240 + xOffset + 18 + 32 * ((i) - (i/5) * 5) ,210 + 32 * (i/5),chipIconWidth,chipIconHeight);
        }
        for(int i = 0; i < currentlySelectedChips.size(); i++)
        {
            Chip currentChip = currentlySelectedChips.get(i);
            gc.drawImage(sprites.get(currentChip.getUnselectedChipIcon()),-240 + xOffset + 194,50 + 32 * i,chipIconWidth,chipIconHeight);

            double chipLoadingSlideWidth = sprites.get("Chip_Loading_Side").getWidth() * 2;
            double chipLoadingSlideHeight = sprites.get("Chip_Loading_Side").getHeight() * 2;
            gc.drawImage(sprites.get("Chip_Loading_Side"),-240 + xOffset + 176,46 + 32 * i,chipLoadingSlideWidth,chipLoadingSlideHeight);
            gc.drawImage(sprites.get("Chip_Loading_Side"),-240 + xOffset + 240,46 + 32 * i,-chipLoadingSlideWidth,chipLoadingSlideHeight);


        }






        // Drawing Cursor
        double cycle = (System.nanoTime() / 1000000000.0)  % 0.3;
        String cursorNum = "";
        if(cycle < 0.1 ) {cursorNum = "1";}
        else
        {if(cycle < 0.2 ) {cursorNum = "2";}
        else
        {cursorNum = "3";}}

        //
        double cursorWidth = sprites.get("Cursor_1").getWidth() * 2;
        double cursorHeight = sprites.get("Cursor_1").getHeight() * 2;
        double buttonCursorWidth = sprites.get("Button_Cursor_1").getWidth() * 2;
        double buttonCursorHeight = sprites.get("Button_Cursor_1").getHeight() * 2;
        double addButtonCursorWidth = sprites.get("Add_Button_Cursor_1").getWidth() * 2;
        double addButtonCursorHeight = sprites.get("Add_Button_Cursor_1").getHeight() * 2;


        if(canAdd)
        {
            double addButtonWidth = sprites.get("Add_Button").getWidth() * 2;
            double addButtonHeight = sprites.get("Add_Button").getHeight() * 2;
            gc.drawImage(sprites.get("Add_Button"),-240 + xOffset + 178,276,addButtonWidth,addButtonHeight);

        }
        else
        {

        }

        if(cursorX == 6)
        {
            if(!onAdd) {
                gc.drawImage(sprites.get("Button_Cursor_" + cursorNum), -240 + xOffset + 14 + 32 * 5, 226, buttonCursorWidth, buttonCursorHeight);
            }
            else
            {
                gc.drawImage(sprites.get("Add_Button_Cursor_" + cursorNum), -240 + xOffset + 14 + 32 * 5, 272, addButtonCursorWidth, addButtonCursorHeight);
            }
        }
        else {
            gc.drawImage(sprites.get("Cursor_" + cursorNum), -240 + xOffset + 14 + 32 * (cursorX - 1), 206 + 32 * (cursorY - 1), cursorWidth, cursorHeight);
        }

        if((cursorX + (cursorY - 1) * 5) <= chipMenu.size()) {
            Chip chip = chipMenu.get((cursorX + (cursorY - 1) * 5) - 1);
            if(chip.isSelected)
            {
                for(int i = 0; i < currentlySelectedChips.size(); i++)
                {
                    Chip currentChip = currentlySelectedChips.get(i);
                    if(chip == currentChip)
                    {
                        gc.drawImage(sprites.get("Cursor_" + cursorNum), -240 + xOffset + 190, 46 + 32 * i, cursorWidth, cursorHeight);
                    }
                }
            }
        }



        // Drawing Info
        if(cursorX != 6) {

            if((cursorX + (cursorY - 1) * 5) <= chipMenu.size()) {
                Chip chip = chipMenu.get((cursorX + (cursorY - 1) * 5) - 1);

                double chipInfoWidth = sprites.get("Cannon_Info").getWidth() * 2;
                double chipInfoHeight = sprites.get("Cannon_Info").getHeight() * 2;
                gc.drawImage(sprites.get(chip.getChipInfo()), -240 + xOffset + 16, 16, chipInfoWidth, chipInfoHeight);
                double chipIDWidth = sprites.get("Chip_A").getWidth() * 2;
                double chipIDHeight = sprites.get("Chip_B").getHeight() * 2;
                gc.drawImage(sprites.get("Chip_" + chip.getLetter()), -240 + xOffset + 18, 169, chipIDWidth, chipIDHeight);
            }
            else
            {

            }

        }
        else
        {
            double infoWidth = sprites.get("Additional_Chip_Data").getWidth() * 2;
            double infoHeight = sprites.get("Additional_Chip_Data").getHeight() * 2;
            if(currentlySelectedChips.size() == 0 && !onAdd)
            {
                gc.drawImage(sprites.get("No_Chip_Data"), -240 + xOffset + 16, 49, infoWidth, infoHeight);
            }
            else
            {
                if(onAdd) {
                    gc.drawImage(sprites.get("Additional_Chip_Data"), -240 + xOffset + 16, 49, infoWidth, infoHeight);
                }
                else
                {
                    gc.drawImage(sprites.get("Chip_Data"), -240 + xOffset + 16, 49, infoWidth, infoHeight);
                }
            }

        }

        // Draw Chip loading
        double chipLoadingProgress = ((System.nanoTime() -chipLoadTime) / 1000000000.0);
        double chipLoadWidth = sprites.get("Load_Icon_1").getWidth() * 2;
        double chipLoadHeight= sprites.get("Load_Icon_1").getHeight() * 2;
        if(chipLoadingProgress <= 0.06)
        {
            gc.drawImage(sprites.get("Load_Icon_1"), -240 + xOffset + 192, 0, chipLoadWidth, chipLoadHeight);
        }
        else
        {
            if(chipLoadingProgress <= 0.12)
            {
                gc.drawImage(sprites.get("Load_Icon_2"), -240 + xOffset + 192, 0, chipLoadWidth, chipLoadHeight);
            }
            else
            {
                if(chipLoadingProgress <= 0.18)
                {
                    gc.drawImage(sprites.get("Load_Icon_3"), -240 + xOffset + 192, 0, chipLoadWidth, chipLoadHeight);
                }
                else
                {
                    if(chipLoadingProgress <= 0.24)
                    {
                        gc.drawImage(sprites.get("Load_Icon_1"), -240 + xOffset + 192, 0, chipLoadWidth, chipLoadHeight);
                    }
                    else
                    {
                        if(chipLoadingProgress <= 0.30)
                        {
                            gc.drawImage(sprites.get("Load_Icon_2"), -240 + xOffset + 192, 0, chipLoadWidth, chipLoadHeight);
                        }
                        else
                        {
                            if(chipLoadingProgress <= 0.36)
                            {
                                gc.drawImage(sprites.get("Load_Icon_3"), -240 + xOffset + 192, 0, chipLoadWidth, chipLoadHeight);
                            }
                            else
                            {

                            }
                        }
                    }
                }
            }
        }


        // Drawing Chip description
        if(showDescription)
        {
            if((cursorX + (cursorY - 1) * 5) <= chipMenu.size()) {
                Chip chip = chipMenu.get((cursorX + (cursorY - 1) * 5) - 1);

                double chipDescriptionWidth = sprites.get("Cannon_Description").getWidth() * 2;
                double chipDescriptionHeight = sprites.get("Cannon_Description").getHeight() * 2;
                gc.drawImage(sprites.get(chip.getChipDescription()), -240 + xOffset + 10, 206, chipDescriptionWidth, chipDescriptionHeight);
            }
        }


    }

    public void moveUp()
    {

        if(!showDescription) {
            if (cursorX != 6 || onAdd) {
                Media sound = new Media(new File(Chip_Select).toURI().toString());
                soundEffect = new MediaPlayer(sound);
                soundEffect.play();
            }
            if (onAdd) {
                onAdd = false;
            } else {
                if (cursorX != 6) {
                    if (cursorY != 1) {
                        cursorY--;
                    } else {
                        cursorY = addCount;
                    }
                }
            }
        }

    }
    public void moveDown()
    {
        if(!showDescription) {

            // Moved up because setting onAdd resolves before Sound causing mute
            if (!onAdd) {
                Media sound = new Media(new File(Chip_Select).toURI().toString());
                soundEffect = new MediaPlayer(sound);
                soundEffect.play();
            }

            if (cursorX == 6 && !onAdd && canAdd) {
                onAdd = true;
            } else {
                if (cursorY != addCount) {
                    cursorY++;
                } else {
                    cursorY = 1;
                }
            }
        }
    }
    public void moveRight() {
        if(!showDescription) {

            if (cursorX != 6 && !onAdd) {
                cursorX++;
            } else {
                if (!onAdd) {
                    cursorX = 1;
                }
            }

            if (!onAdd) {
                Media sound = new Media(new File(Chip_Select).toURI().toString());
                soundEffect = new MediaPlayer(sound);
                soundEffect.play();
            }
        }
    }
    public void moveLeft()
    {
        if(!showDescription) {

            if (cursorX != 1 && !onAdd) {
                cursorX--;
            } else {
                if (!onAdd) {
                    cursorX = 6;
                }
            }

            if (!onAdd) {
                Media sound = new Media(new File(Chip_Select).toURI().toString());
                soundEffect = new MediaPlayer(sound);
                soundEffect.play();
            }
        }
    }
    public void pressA() {

        if (!showDescription) {
            // this also allows cursorX = 6 ( Add and Confirm )

            if (cursorX != 6) {
                Chip chip = chipMenu.get((cursorX + (cursorY - 1) * 5) - 1);
                if (chip.isSelected) {
                    Media sound = new Media(new File(Chip_Cancel).toURI().toString());
                    soundEffect = new MediaPlayer(sound);
                    soundEffect.play();
                } else {
                    if (checkIfChipCompatable(chip) && currentlySelectedChips.size() != 5) {
                        chip.setIsSelected(true);
                        currentlySelectedChips.add(chip);
                        chipLoadTime = System.nanoTime();
                        Media sound = new Media(new File(Chip_Choose).toURI().toString());
                        soundEffect = new MediaPlayer(sound);
                        soundEffect.play();
                        canAdd = false;

                    } else {
                        Media sound = new Media(new File(Chip_Cancel).toURI().toString());
                        soundEffect = new MediaPlayer(sound);
                        soundEffect.play();
                    }
                }
            } else {
                if (onAdd) {
                    addChipsComplete = true;
                    Media sound = new Media(new File(Chip_Confirm).toURI().toString());
                    soundEffect = new MediaPlayer(sound);
                    soundEffect.play();
                } else {
                    loadChipsComplete = true;
                    Media sound = new Media(new File(Chip_Confirm).toURI().toString());
                    soundEffect = new MediaPlayer(sound);
                    soundEffect.play();
                }
            }
        }
    }
    public void pressB()
    {
        if(!showDescription) {

            if (currentlySelectedChips.size() != 0) {
                Chip chip = currentlySelectedChips.get(currentlySelectedChips.size() - 1);
                chip.setIsSelected(false);
                currentlySelectedChips.remove(currentlySelectedChips.size() - 1);
                Media sound = new Media(new File(Chip_Cancel).toURI().toString());
                soundEffect = new MediaPlayer(sound);
                soundEffect.play();

                if(currentlySelectedChips.size() == 0)
                {
                    if(addCount != 3) {
                        canAdd = true;
                    }
                }
                // Play sound!
            }
        }
    }
    public void pressR()
    {
        if(cursorX != 6 && !buttonSkidEnabled) {
            showDescription = true;
            Media sound = new Media(new File(Chip_Description).toURI().toString());
            soundEffect = new MediaPlayer(sound);
            soundEffect.play();
            buttonSkidEnabled = false;
        }
    }
    public void unPressR()
    {
        showDescription = false;
        Media sound = new Media(new File(Close_Chip_Description).toURI().toString());
        soundEffect = new MediaPlayer(sound);
        soundEffect.play();
    }

    public boolean checkIfChipCompatable(Chip chip)
    {
        if(currentlySelectedChips.size() == 0)
        {
            return true;
        }
        else
        {
            String letter = chip.getLetter();
            boolean allSameLetter = true;
            for(Chip otherChip: currentlySelectedChips)
            {
                if(!letter.equals(otherChip.getLetter()))
                {
                    allSameLetter = false;
                }
            }
            if(allSameLetter)
            {
                return true;
            }
            else
            {
                ChipType type = chip.getChipType();
                boolean allSameType = true;
                for(Chip otherChip: currentlySelectedChips)
                {
                    if(!type.equals(otherChip.getChipType()))
                    {
                        allSameType = false;
                    }
                }
                if(allSameType)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
    }

}
