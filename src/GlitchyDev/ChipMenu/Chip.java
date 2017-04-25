package GlitchyDev.ChipMenu;

import GlitchyDev.States.ChipType;

/**
 * Created by Robert on 4/20/2017.
 */
public class Chip {
    private ChipType chipType;
    private String letter;
    boolean isSelected;

    public Chip(ChipType chipType, String letter)

    {
        this.chipType = chipType;
        this.letter = letter;
        isSelected = false;
    }

    public boolean getIsSelected()
    {
        return isSelected;
    }
    public void setIsSelected(boolean isSelected)
    {
        this.isSelected = isSelected;
    }
    public String getChipIcon()
    {
        if(isSelected) {
            return chipType.toString() + "_Selected";
        }
        else
        {
            return chipType.toString() + "_Icon";
        }
    }
    public String getUnselectedChipIcon()
    {

        return chipType.toString() + "_Icon";

    }
    public String getChipInfo()
    {
        return chipType.toString() + "_Info";
    }
    public String getChipLoad()
    {
        return chipType.toString() + "_Load";
    }

    public String getChipDescription()
    {
        return chipType.toString() + "_Description";
    }
    public ChipType getChipType() {
        return chipType;
    }


    public String getLetter() {
        return letter;
    }

}

